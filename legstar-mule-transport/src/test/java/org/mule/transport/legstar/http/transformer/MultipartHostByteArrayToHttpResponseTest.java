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
package org.mule.transport.legstar.http.transformer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpVersion;
import org.mule.api.MuleEvent;
import org.mule.api.transformer.Transformer;
import org.mule.api.transport.OutputHandler;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.AbstractTransformerTestCase;
import org.mule.transport.http.HttpResponse;
import org.mule.transport.legstar.cixs.transformer.HostToLegstarExecRequestMuleTransformer;
import org.mule.transport.legstar.cixs.transformer.HostToMultipartLegstarExecRequestMuleTransformerTest;
import org.mule.transport.legstar.config.ConfigUtils;

import com.legstar.coxb.host.HostData;
import com.legstar.messaging.HeaderPartException;
import com.legstar.messaging.HostMessageFormatException;
import com.legstar.messaging.LegStarMessage;
import com.legstar.messaging.LegStarMessagePart;
import com.legstar.test.coxb.LsfileacCases;
/**
 * Test HostByteArrayToHttpResponse class.
 *
 */
public class MultipartHostByteArrayToHttpResponseTest extends AbstractTransformerTestCase {

    private MuleEvent muleEvent;

    /** {@inheritDoc} */
    public AbstractMessageTransformer getTransformer() throws Exception {
    	AbstractMessageTransformer transformer = new HostByteArrayToHttpResponse();
        transformer.initialise();
        transformer.setMuleContext(muleContext);
        return transformer;
    }

    /** {@inheritDoc}
     * Multi part messages must be wrapped in a LegStar message otherwise the host
     * won't know how to unwrap the data sent.
     *  
     *  */
    public Object getTestData() {
        try {
            Map < String, byte[]> testData = new HashMap < String, byte[]>();
            testData.put("ReplyData",
                    HostData.toByteArray(LsfileacCases.getHostBytesHexReplyData()));
            testData.put("ReplyStatus",
                    HostData.toByteArray(LsfileacCases.getHostBytesHexReplyStatus()));
 
            HostToLegstarExecRequestMuleTransformer transformer = new HostToLegstarExecRequestMuleTransformer();
            transformer.setMuleContext(muleContext);
            transformer.setHostProgram(ConfigUtils.getHostProgram("lsfileac.properties"));
            
            return transformer.transform(testData);
        } catch (Exception e) {
            fail(e.toString());
            return null;
        }
        
    }

    /** {@inheritDoc} */
    public Object getResultData() {
        HttpResponse resultData = new HttpResponse();
        resultData.setStatusLine(HttpVersion.HTTP_1_1, 200, "OK");
        OutputHandler outputHandler = new OutputHandler() {

            public void write(
                    final MuleEvent event,
                    final OutputStream out) throws IOException {
                muleEvent = event;
                out.write(HostData.toByteArray(
                        HostToMultipartLegstarExecRequestMuleTransformerTest.LSFILEAC_MESSAGE_HOST_DATA));
            }

        };
        resultData.setBody(outputHandler);
        return resultData;
    }

    /** {@inheritDoc} */
    public Transformer getRoundTripTransformer() throws Exception {
        return null;
    }

    /** {@inheritDoc} */
    public boolean compareResults(final Object expected, final Object result) {
        try {
            if (expected instanceof HttpResponse && result instanceof HttpResponse) {
                HttpResponse httpExpected = (HttpResponse) expected;
                HttpResponse httpResult = (HttpResponse) result;
                if (!httpExpected.getStatusLine().equals(httpResult.getStatusLine())) {
                    return false;
                }
                ByteArrayOutputStream outExpected = new ByteArrayOutputStream();
                httpExpected.getBody().write(muleEvent, outExpected);
                byte[] bodyExpected = outExpected.toByteArray();

                ByteArrayOutputStream outResult = new ByteArrayOutputStream();
                httpResult.getBody().write(muleEvent, outResult);
                byte[] bodyResult = outResult.toByteArray();
                
                LegStarMessage msgExpected = new LegStarMessage();
                msgExpected.fromByteArray(bodyExpected, 0);
                
                LegStarMessage msgResult = new LegStarMessage();
                msgResult.fromByteArray(bodyResult, 0);
                
                assertTrue(msgExpected.getHeaderPart().equals(msgResult.getHeaderPart()));
                for (LegStarMessagePart partExpected : msgExpected.getDataParts()) {
                    boolean matched = false;
                    for (LegStarMessagePart partResult : msgResult.getDataParts()) {
                        if (partExpected.getPartID().equals(partResult.getPartID())) {
                            if (partExpected.equals(partResult)) {
                                matched = true;
                            }
                        }
                    }
                    if (!matched) {
                        return false;
                    }
                    
                }

                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        } catch (HostMessageFormatException e) {
            return false;
        } catch (HeaderPartException e) {
            return false;
        }
    }

}
