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

package org.trypticon.xmpp.disco;

import java.util.List;

import org.jabberstudio.jso.x.disco.DiscoInfoQuery;

/**
 * An interface to be implemented by any objects which support returning info
 * from a service discovery query.
 */
public interface Discoverable
{
    /**
     * Gets the node which the discoverable is located at.
     *
     * @return the node which the discoverable is located at.
     */
    String getNode();

    /**
     * Populates info into the provided query, to form the response.
     *
     * @param query the query.
     */
    void populateDiscoInfo(DiscoInfoQuery query);

    /**
     * Gets disco items which exist at a child of this item.
     */
    List<Discoverable> getDiscoChildren();
}
