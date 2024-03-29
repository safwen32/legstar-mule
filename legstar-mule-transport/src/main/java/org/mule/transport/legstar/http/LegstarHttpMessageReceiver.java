/*******************************************************************************
 * Copyright (c) 2009 LegSem.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     LegSem - initial API and implementation
 ******************************************************************************/
package org.mule.transport.legstar.http;

import org.mule.api.construct.FlowConstruct;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transport.Connector;
import org.mule.transport.http.HttpMessageReceiver;

/**
 * <code>LegstarMessageReceiver</code> delegates all processing
 * to <code>HttpMessageReceiver</code>.
 */
public class LegstarHttpMessageReceiver extends HttpMessageReceiver {

    /**
     * Constructs a message receiver for a component.
     * @param connector the Mule connector
     * @param flowConstruct the Mule flow 
     * @param endpoint the Mule endpoint
     * @throws CreateException if construction fails
     */
    public LegstarHttpMessageReceiver(
            final Connector connector,
            final FlowConstruct flowConstruct,
            final InboundEndpoint endpoint) throws CreateException {
        super(connector, flowConstruct, endpoint);
    }
}

