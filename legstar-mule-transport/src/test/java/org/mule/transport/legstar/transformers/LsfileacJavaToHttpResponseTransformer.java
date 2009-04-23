package org.mule.transport.legstar.transformers;
import org.mule.transport.legstar.http.transformers.AbstractObjectToHttpResponseTransformer;

/**
 * LegStar/Mule Operation LsfileacResponseHolder to Http response Transformer.
 * 
 * <code>LsfileacResponseHolderToHttpResponse</code> takes a LsfileacResponseHolder
 * object as input, as well as a series of properties to create an http
 * response with binary content in host format (EBCDIC) ready to be sent
 * to a target Mainframe.
 * 
 * This class was generated by LegStar Mule Component generator.
 */
public class LsfileacJavaToHttpResponseTransformer
        extends AbstractObjectToHttpResponseTransformer {

    /**
     * Constructs the transformer. Source is a Dfhcommarea object.
     */
    public LsfileacJavaToHttpResponseTransformer() {
        super();
        registerSourceType(LsfileacHolder.class);
    }

    /** {@inheritDoc} */
    public AbstractHostMuleTransformer getJavaToHostMuleTransformer() {
        return new LsfileacJavaToHostTransformer();
    }

}