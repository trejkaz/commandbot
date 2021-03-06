/*
 * Copyright 2004-2005 Trypticon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.trypticon.xmpp.bot;

import java.util.Map;
import java.util.HashMap;

import org.jabberstudio.jso.Presence;
import org.jabberstudio.jso.JID;
import org.jabberstudio.jso.Stream;
import org.jabberstudio.jso.event.PacketListener;
import org.jabberstudio.jso.event.PacketEvent;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Basic implementation of a roster.
 */
public class BaseRoster implements Roster
{
    /**
     * Logging.
     */
    private static final Log log = LogFactory.getLog(BaseRoster.class);

    /**
     * A map of bare JIDs to maps of resource names to presence.
     */
    private Map<JID, Map<String, Presence>> presenceMap = new HashMap<JID, Map<String, Presence>>();

    /**
     * The current stream we're attached to.
     */
    private Stream stream;

    /**
     * The handler which we'll attach to the stream.
     */
    private PresenceHandler presenceHandler = new PresenceHandler();

    /**
     * Gets the highest priority presence for a JID.
     *
     * @param jid the JID
     * @return the highest priority presence for that JID, or <code>null</code> if it is not available.
     */
    public Presence getPresence(JID jid)
    {
        Presence highestPresence = null;
        Map<String, Presence> resourceMap = presenceMap.get(jid);
        if (resourceMap != null)
        {
            int highestPriority = Integer.MIN_VALUE;
            for (Presence presence : resourceMap.values())
            {
                if (presence.getPriority() > highestPriority)
                {
                    highestPresence = presence;
                    highestPriority = presence.getPriority();
                }
            }
        }
        return highestPresence;
    }

    /**
     * Attaches the roster to a stream, resetting all state.
     *
     * @param stream the stream.
     * @throws IllegalArgumentException if <code>stream</code> is null.
     */
    protected void attach(Stream stream)
    {
        log.debug("Attaching to stream " + stream);

        if (stream == null)
        {
            throw new IllegalArgumentException("The roster can't attach to a null stream");
        }

        // Detach the previous handler if need be.
        if (this.stream != null)
        {
            this.stream.removePacketListener(PacketEvent.RECEIVED, presenceHandler);
        }

        // TODO: Fetch the actual roster.

        // Any presence we previously held is invalidated now.
        presenceMap.clear();

        // Attach the handler to the new stream, which finally becomes the current stream.
        stream.addPacketListener(PacketEvent.RECEIVED, presenceHandler);
        this.stream = stream;
    }

    /**
     * Handles incoming presence packets.
     */
    private class PresenceHandler implements PacketListener
    {
        /**
         * Method called when a <tt>Packet</tt> is received or sent.</p>
         *
         * @param event the <tt>PacketEvent</tt> object.
         */
        public void packetTransferred(PacketEvent event)
        {
            if (event.getType() == PacketEvent.RECEIVED && event.getData() instanceof Presence)
            {
                Presence presence = (Presence) event.getData();

                // We only care about available/unavailable for now.
                if (presence.getType() == Presence.UNAVAILABLE ||
                    presence.getType() == null)
                {
                    JID bareJID = presence.getFrom().toBareJID();
                    String resource = presence.getFrom().getResource();

                    // Find the presence map for the bare JID, create one if necessary.
                    Map<String, Presence> resourceMap = presenceMap.get(bareJID);
                    if (resourceMap == null)
                    {
                        resourceMap = new HashMap<String, Presence>();
                        presenceMap.put(bareJID, resourceMap);
                    }

                    // If this is an availability presence packet, add it to the map.  Otherwise, remove it.
                    if (presence.getType() == null)
                    {
                        resourceMap.put(resource, presence);
                    }
                    else
                    {
                        resourceMap.remove(resource);
                    }

                    // If the resource map is now empty, we might as well remove the map entirely.
                    if (resourceMap.isEmpty())
                    {
                        presenceMap.remove(bareJID);
                    }
                }
            }
        }
    }
}
