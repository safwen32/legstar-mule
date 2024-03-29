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
package org.mule.transport.legstar.transformer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.types.DataTypeFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.legstar.coxb.transform.AbstractTransformers;
import com.legstar.coxb.transform.AbstractXmlTransformers;
import com.legstar.coxb.transform.HostTransformException;

/**
 * This ESB transformer converts XML into host data using the regular LegStar
 * binding transformers.
 */
public abstract class AbstractXmlToHostMuleTransformer extends
        AbstractHostXmlMuleTransformer implements IObjectToHostTransformer {

    /** When XML is encoded, we assume this encoding. TODO get from Mule. */
    private static final String XML_ENCODING = "UTF-8";

    /**
     * a specialized class that knows how to get objects from a holder (a bag of
     * objects)
     */
    private HolderGetter holderGetter;

    /** JAXB Context (Used for Multi-structures). */
    private JAXBContext mJaxbContext = null;

    /** JAXB Unmarshaller (Object to XML, Used for Multi-structures). */
    private Unmarshaller mXmlUnmarshaller = null;

    /**
     * Constructor for single part transformers.
     * <p/>
     * Xml to Host transformers expect String or byte[] input and produces a
     * byte array corresponding to mainframe raw data.
     * 
     * @param xmlBindingTransformers a set of transformers for the part type.
     */
    public AbstractXmlToHostMuleTransformer(
            final AbstractXmlTransformers xmlBindingTransformers) {
        super(xmlBindingTransformers);
        registerSourceType(DataTypeFactory.STRING);
        registerSourceType(DataTypeFactory.create(InputStream.class));
        registerSourceType(DataTypeFactory.BYTE_ARRAY);
        setReturnDataType(DataTypeFactory.BYTE_ARRAY);
    }

    /**
     * Constructor for multi-part transformers.
     * <p/>
     * Xml to Host transformers expect a String or byte[] as input and produces
     * a a map of byte arrays, one for each mainframe payload part.
     * 
     * @param xmlBindingTransformersMap map of transformer sets, one for each
     *            part type.
     */
    public AbstractXmlToHostMuleTransformer(
            final Map < String, AbstractXmlTransformers > xmlBindingTransformersMap) {
        super(xmlBindingTransformersMap);
        registerSourceType(DataTypeFactory.STRING);
        registerSourceType(DataTypeFactory.create(InputStream.class));
        registerSourceType(DataTypeFactory.BYTE_ARRAY);
        setReturnDataType(DataTypeFactory.create(Map.class));
    }

    /**
     * Constructor for multiple structures transformers.
     * <p/>
     * Xml to Host transformers expect String or byte[] input and produces a
     * byte array corresponding to mainframe raw data.
     * 
     * @param bindingTransformersList ordered list of transformers to be applied
     *            in sequence.
     * @param holderGetter Provides a way to get inner objects from a Holder
     */
    public AbstractXmlToHostMuleTransformer(
            final List < AbstractTransformers > bindingTransformersList,
            HolderGetter holderGetter) {
        super(bindingTransformersList);
        registerSourceType(DataTypeFactory.STRING);
        registerSourceType(DataTypeFactory.create(InputStream.class));
        registerSourceType(DataTypeFactory.BYTE_ARRAY);
        setReturnDataType(DataTypeFactory.BYTE_ARRAY);
        this.holderGetter = holderGetter;
        try {
            mJaxbContext = JAXBContext
                    .newInstance(holderGetter.getHolderType());
            mXmlUnmarshaller = mJaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Unable to create JAXB context",
                    e);
        }
    }

    /**
     * {@inheritDoc} The nature of the binding transformers passed by inherited
     * class determines if this is a multi part transformer or not.
     * */
    public Object hostTransform(final MuleMessage esbMessage,
            final String encoding) throws TransformerException {

        try {

            String hostCharset = getHostCharset(esbMessage);

            /* Single part messages come with binding transformers */
            if (getXmlBindingTransformers() != null) {
                byte[] hostData = getXmlBindingTransformers().toHost(
                        getXmlSource(esbMessage.getPayload()), hostCharset);
                return hostData;

            }
            if (getBindingTransformersList() != null) {
                Object holder = createHolder(getXmlSource(esbMessage
                        .getPayload()));
                int index = 0;
                int hostDataSize = 0;
                byte[][] hostBytesArray = new byte[getBindingTransformersList()
                        .size()][];
                for (AbstractTransformers xf : getBindingTransformersList()) {
                    hostBytesArray[index] = xf.toHost(
                            holderGetter.get(holder, index), hostCharset);
                    hostDataSize += hostBytesArray[index].length;
                    index++;
                }
                /* Merge individual byte arrays to form the request */
                byte[] hostData = new byte[hostDataSize];
                int requestBytePos = 0;
                for (int i = 0; i < hostBytesArray.length; i++) {
                    System.arraycopy(hostBytesArray[i], 0, hostData,
                            requestBytePos, hostBytesArray[i].length);
                    requestBytePos += hostBytesArray[i].length;
                }
                return hostData;
            }
            if (getXmlBindingTransformersMap() != null) {
                Document holderDoc = getDocument(esbMessage.getPayload());
                Map < String, byte[] > hostDataMap = new HashMap < String, byte[] >();
                for (Entry < String, AbstractXmlTransformers > entry : getXmlBindingTransformersMap()
                        .entrySet()) {
                    hostDataMap.put(
                            entry.getKey(),
                            entry.getValue().toHost(
                                    getXmlFragmentFromHolder(holderDoc,
                                            entry.getKey()), hostCharset));
                }
                return hostDataMap;
            }
            return null;

        } catch (HostTransformException e) {
            throw new TransformerException(getI18NMessages()
                    .hostTransformFailure(), this, e);
        }

    }

    /**
     * Return the payload wrapped as a javax.xml.transform.Source.
     * 
     * @param payload the esb message payload
     * @return an XML source
     * @throws TransformerException if esb message payload is not an XML source
     */
    public Source getXmlSource(final Object payload)
            throws TransformerException {
        try {
            if (payload instanceof String) {
                return new StreamSource(new StringReader((String) payload));
            } else if (payload instanceof byte[]) {
                return new StreamSource(new InputStreamReader(
                        new ByteArrayInputStream((byte[]) payload),
                        XML_ENCODING));

            } else if (payload instanceof InputStream) {
                return new StreamSource(new InputStreamReader(
                        (InputStream) payload, XML_ENCODING));
            } else {
                throw new TransformerException(getI18NMessages()
                        .payloadNotXmlSource(), this);
            }
        } catch (UnsupportedEncodingException e) {
            throw new TransformerException(getI18NMessages().encodingFailure(
                    XML_ENCODING), this, e);
        }

    }

    /**
     * Return the payload wrapped as a org.xml.sax.InputSource.
     * 
     * @param payload the esb message payload
     * @return an XML InputSource
     * @throws TransformerException if esb message payload is not an XML source
     */
    public InputSource getXmlInputSource(final Object payload)
            throws TransformerException {
        if (payload instanceof String) {
            return new InputSource(new StringReader((String) payload));
        } else if (payload instanceof byte[]) {
            return new InputSource(new ByteArrayInputStream((byte[]) payload));

        } else if (payload instanceof InputStream) {
            return new InputSource((InputStream) payload);
        } else {
            throw new TransformerException(getI18NMessages()
                    .payloadNotXmlSource(), this);
        }

    }

    /**
     * @param payload the ESB message payload
     * @return the esb message payload as a DOM document
     * @throws TransformerException if payload is not XML
     */
    public Document getDocument(final Object payload)
            throws TransformerException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            docFactory.setNamespaceAware(true);
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(getXmlInputSource(payload));
        } catch (TransformerException e) {
            throw new TransformerException(getI18NMessages()
                    .payloadNotXmlSource(), this);
        } catch (ParserConfigurationException e) {
            throw new TransformerException(getI18NMessages()
                    .payloadNotXmlSource(), this);
        } catch (SAXException e) {
            throw new TransformerException(getI18NMessages()
                    .payloadNotXmlSource(), this);
        } catch (IOException e) {
            throw new TransformerException(getI18NMessages()
                    .payloadNotXmlSource(), this);
        }
    }

    /**
     * When a holder XML for multi part payload needs to be turned into host
     * data, we need to associate inner XML nodes with part IDs. TODO This
     * assumes node local names are identical to part IDs
     * 
     * @param holderDoc holder XML document
     * @param partID the part identifier or container name
     * @return a holder object
     * @throws TransformerException if creating holder fails
     */
    public Source getXmlFragmentFromHolder(final Document holderDoc,
            final String partID) throws TransformerException {
        NodeList nodeList = holderDoc.getElementsByTagName(partID);
        if (nodeList.getLength() > 0) {
            return new DOMSource(nodeList.item(0));
        } else {
            return new StreamSource(new StringReader(""));
        }
    }

    /**
     * Build a Holder object from XML the using JAXB.
     * 
     * @param xmlSource the XML source
     * @return a Holder object resulting from unmarshalling the XML
     * @throws HostTransformException if XML transformation fails
     */
    private Object createHolder(Source xmlSource) throws HostTransformException {
        try {
            return mXmlUnmarshaller.unmarshal(xmlSource);
        } catch (JAXBException e) {
            throw new HostTransformException(e);
        }
    }
}
