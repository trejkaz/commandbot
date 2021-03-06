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

package org.trypticon.xmpp.logging;

import net.outer_planes.jso.JSO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jabberstudio.jso.event.PacketEvent;
import org.jabberstudio.jso.event.PacketListener;

/**
 * A packet listener which prints the packets to the log.
 */
public class LoggingPacketListener implements PacketListener
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(LoggingPacketListener.class);

    /**
     * Logs the packet.
     *
     * @param event the packet event.
     */
    public void packetTransferred(PacketEvent event)
    {
        String direction = event.getContext().isInbound() ? "Inbound" : "Outbound";
        log.debug(direction + " : " +
                  JSO.getInstance().createXMLExporter(event.getContext()).write(event.getData()));
    }
}
