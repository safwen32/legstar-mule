package org.mule.transport.legstar.test.lsfileae;
import org.mule.transport.legstar.transformer.AbstractJavaToHostMuleTransformer;
import com.legstar.test.coxb.lsfileae.Dfhcommarea;
import com.legstar.test.coxb.lsfileae.bind.DfhcommareaTransformers;

/**
 * Dfhcommarea to Host Byte Array Mule Transformer.
 * <p/>
 * <code>DfhcommareaToHostMuleTransformer</code> takes a Dfhcommarea object as input
 * and turns it into a byte array in host format (EBCDIC), ready
 * to be sent to a target Mainframe.
 * <p/> 
 * Class generated by LegStar Mule Component generator.
 */
public class DfhcommareaToHostMuleTransformer extends AbstractJavaToHostMuleTransformer {

    /**
     * Constructs the transformer. Source is a Dfhcommarea object and result
     * will be a byte array.
     */
    public DfhcommareaToHostMuleTransformer() {
        super(new DfhcommareaTransformers());
        registerSourceType(Dfhcommarea.class);
    }


}
