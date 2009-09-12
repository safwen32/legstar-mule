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
package org.mule.transport.legstar.model;

import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.mule.transport.legstar.gen.AbstractTestTemplate;
import org.mule.transport.legstar.gen.Samples;
import org.mule.transport.legstar.model.AbstractAntBuildCixsMuleModel.SampleConfigurationHostMessagingType;
import org.mule.transport.legstar.model.AbstractAntBuildCixsMuleModel.SampleConfigurationTransport;

import com.legstar.cixs.gen.ant.AbstractCixsGenerator;
import com.legstar.codegen.CodeGenUtil;

/**
 * Adapter Generator ant tests.
 * <p/>
 * Tests the velocity templates used to produce ant scripts which in
 * turn generate artifacts. These templates are usually used by IDEs,
 * they are not needed for a batch usage of the generators.
 *
 */
public class AntBuildMule2CixsModelTest extends AbstractTestTemplate {

    /** Model expected by template. */
    AntBuildMule2CixsModel mAntModel;

    /** {@inheritDoc}*/
    public void setUp() {
        super.setUp();
        mAntModel = new AntBuildMule2CixsModel();
    }

    /**
     * Adapter case for an LSFILEAE program over legstar-mule HTTP.
     * @throws Exception if generation fails
     */
    public void testLsfileaeGenerate() throws Exception {
        /* Build the model */
        CixsMuleComponent muleComponent = Samples.getLsfileaeMuleComponent();
        initCixsMuleComponent(muleComponent);
        processAnt();
    }

    /**
     * Adapter case for an LSFILEAE program over legstar-mule WMQ.
     * @throws Exception if generation fails
     */
    public void testLsfileaeWmqGenerate() throws Exception {
        /* Build the model */
        CixsMuleComponent muleComponent = Samples.getLsfileaeMuleComponent();
        muleComponent.setName(muleComponent.getName() + "-wmq");
        initCixsMuleComponent(muleComponent);

        mAntModel.setSampleConfigurationTransport(
                SampleConfigurationTransport.WMQ);
        mAntModel.setSampleConfigurationHostMessagingType(
                SampleConfigurationHostMessagingType.MQCIH);

        mAntModel.getWmqTransportParameters().setConnectionFactory("ConnectionFactory");
        mAntModel.getWmqTransportParameters().setJndiUrl(
                WmqTransportParameters.DEFAULT_JNDI_FS_DIRECTORY);
        mAntModel.getWmqTransportParameters().setJndiContextFactory(
                WmqTransportParameters.DEFAULT_JNDI_CONTEXT_FACTORY);
        mAntModel.getWmqTransportParameters().setZosQueueManager("CSQ1");
        mAntModel.getWmqTransportParameters().setRequestQueue(
                "CICS01.BRIDGE.REQUEST.QUEUE");
        mAntModel.getWmqTransportParameters().setReplyQueue(
                "CICS01.BRIDGE.REPLY.QUEUE");

        processAnt();
    }

    /**
     * Adapter case for an LSFILEAL program over legstar-mule HTTP.
     * @throws Exception if generation fails
     */
    public void testLsfilealGenerate() throws Exception {
        /* Build the model */
        CixsMuleComponent muleComponent = Samples.getLsfilealMuleComponent();
        initCixsMuleComponent(muleComponent);
        processAnt();
    }

    /**
     * Adapter case for an LSFILEAC program over legstar-mule HTTP.
     * @throws Exception if generation fails
     */
    public void testLsfileacGenerate() throws Exception {
        /* Build the model */
        CixsMuleComponent muleComponent = Samples.getLsfileacMuleComponent();
        initCixsMuleComponent(muleComponent);
        processAnt();
    }

    /**
     * Adapter case for an LSFILEAX program over legstar-mule HTTP.
     * @throws Exception if generation fails
     */
    public void testLsfileaxGenerate() throws Exception {
        /* Build the model */
        CixsMuleComponent muleComponent = Samples.getLsfileaxMuleComponent();
        initCixsMuleComponent(muleComponent);
        processAnt();
    }

    /**
     * Generate the ant script, check it and run it.
     * @throws Exception if something goes wrong
     */
    private void processAnt() throws Exception {
        AbstractCixsGenerator.generateFile("Test generator",
                "vlc/build-mule2cixs-xml.vm",
                "antModel",
                mAntModel,
                getParameters(),
                mAntModel.getTargetAntDir(),
                "generate.xml");

        compare(mAntModel.getTargetAntDir(), "generate.xml",
                mAntModel.getCixsMuleComponent().getName());
        runAnt(mAntModel.getTargetAntDir(), "generate.xml");
    }
    
    /**
     * Submit execution of an ant script.
     * @param antFilesDir where the ant script is
     * @param scriptName the ant script name
     */
    private void runAnt(final File antFilesDir, final String scriptName) {
        Project project = new Project();
        project.init();
        ProjectHelper.getProjectHelper().parse(project, new File(antFilesDir, scriptName));
        project.executeTarget(project.getDefaultTarget());
    }

    /**
     * Common initialization. Segregate output so that various tests
     * do not overwrite each other.
     * @param muleComponent the service
     */
    private void initCixsMuleComponent(final CixsMuleComponent muleComponent) {
        mAntModel.setProductLocation("..");
        mAntModel.setHostCharset("IBM01147");
        mAntModel.setCixsMuleComponent(muleComponent);
        File targetDir = new File("../legstar-mulegen-" + muleComponent.getName());
        mAntModel.setJaxbBinDir(new File(targetDir, "target/classes"));
        CodeGenUtil.checkDirectory(mAntModel.getJaxbBinDir(), true);
        mAntModel.setTargetSrcDir(new File(targetDir, "src/main/java"));
        mAntModel.setTargetBinDir(new File(targetDir, "target/classes"));
        mAntModel.setTargetDistDir(new File(targetDir, "target/dist"));
        mAntModel.setTargetMuleConfigDir(new File(targetDir, "src/main/resources"));
        mAntModel.setTargetJarDir(new File("${mule.home}/lib/user"));
        mAntModel.setTargetAntDir(new File(targetDir, "ant"));
        CodeGenUtil.checkDirectory(mAntModel.getTargetAntDir(), true);
        
        /* This is a dirty trick. This component does not need custom libraries so we
         * use the placeholder to get this generator classes to supersede the ones
         * that come from the LEGSTAR_HOME location. */
        mAntModel.setCustBinDir(new File("../legstar-mule-generator/target/classes"));
    }
}