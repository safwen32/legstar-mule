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
import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.transport.MessageReceiver;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.endpoint.URIBuilder;
import org.mule.transport.AbstractMessageReceiverTestCase;
import org.mule.transport.legstar.http.transformer.HostByteArrayToHttpResponse;
import org.mule.util.CollectionUtils;

import com.mockobjects.dynamic.Mock;


/**
 * Test the LegstarHttpMessageReceiver class.
 * For general guidelines on writing transports see http://mule.mulesource.org/display/MULE/Writing+Transports
 */
public class LegstarHttpMessageReceiverTestCase extends AbstractMessageReceiverTestCase {

    /** Legstar listening port. */
    private static final int LEGSTAR_PORT = 11958;

    /** {@inheritDoc} */
    public MessageReceiver getMessageReceiver() throws Exception {
        Mock mockService = new Mock(FlowConstruct.class);
        mockService.expectAndReturn("getResponseTransformer", null);
        mockService.expectAndReturn("getResponseRouter", null);
        return new LegstarHttpMessageReceiver(endpoint.getConnector(),
                (FlowConstruct) mockService.proxy(), endpoint);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
	public InboundEndpoint getEndpoint() throws Exception {
        EndpointBuilder endpointBuilder = new EndpointURIEndpointBuilder(
                new URIBuilder("legstar://localhost:" + Integer.toString(LEGSTAR_PORT), muleContext));
        endpointBuilder.setResponseMessageProcessors(CollectionUtils.singletonList(
                new HostByteArrayToHttpResponse()));
        endpoint = muleContext.getEndpointFactory().getInboundEndpoint(endpointBuilder);
        return endpoint;
    }

}
