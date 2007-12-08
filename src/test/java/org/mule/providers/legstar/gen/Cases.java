/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.providers.legstar.gen;

import java.util.ArrayList;
import java.util.List;

import org.mule.providers.legstar.model.CixsMuleComponent;
import org.mule.providers.legstar.model.CixsOperation;
import org.mule.providers.legstar.model.CixsStructure;

/**
 * Helper class. Creates models ready for code generation.
 */
public final class Cases {
    
    /** Defeats instantiation.*/
    private Cases() {
        
    }

	/** Target package for generated Mule component. */
    public static final String LEGS4MULE_PKG_PREFIX =
	    "org.mule.providers.legstar.test";
    
    /** Cobol binding classes package. As generated by LegStar Coxbgen. */
	public static final String JAXB_PKG_PREFIX = "com.legstar.test.coxb";
	
	/**
	 * @return a Mule component to serve as model for velocity templates.
	 */
	public static CixsMuleComponent getLsfileaeMuleComponent() {
        CixsMuleComponent muleComponent = new CixsMuleComponent();
        muleComponent.setName("MuleLsfileae");
        muleComponent.setPackageName(Cases.LEGS4MULE_PKG_PREFIX + ".lsfileae");
        List <CixsOperation> cixsOperations = new ArrayList <CixsOperation>();
        cixsOperations.add(Cases.getLsfileaeOperation());
        muleComponent.setCixsOperations(cixsOperations);
        return muleComponent;
	}
	
	/**
	 * @return a Mule component to serve as model for velocity templates.
	 */
	public static CixsMuleComponent getLsfilealMuleComponent() {
        CixsMuleComponent muleComponent = new CixsMuleComponent();
        muleComponent.setName("MuleLsfileal");
        muleComponent.setPackageName(Cases.LEGS4MULE_PKG_PREFIX + ".lsfileal");
        List <CixsOperation> cixsOperations = new ArrayList <CixsOperation>();
        cixsOperations.add(Cases.getLsfilealOperation());
        muleComponent.setCixsOperations(cixsOperations);
        return muleComponent;
	}
	/**
	 * @return a Mule component to serve as model for velocity templates.
	 */
	public static CixsMuleComponent getLsfileacMuleComponent() {
        CixsMuleComponent muleComponent = new CixsMuleComponent();
        muleComponent.setName("MuleLsfileac");
        muleComponent.setPackageName(Cases.LEGS4MULE_PKG_PREFIX + ".lsfileac");
        List <CixsOperation> cixsOperations = new ArrayList <CixsOperation>();
        cixsOperations.add(Cases.getLsfileacOperation());
        muleComponent.setCixsOperations(cixsOperations);
        return muleComponent;
	}
	/**
	 * @return a Mule component to serve as model for velocity templates.
	 */
	public static CixsMuleComponent getLsfileaxMuleComponent() {
        CixsMuleComponent muleComponent = new CixsMuleComponent();
        muleComponent.setName("MuleLsfileax");
        muleComponent.setPackageName(Cases.LEGS4MULE_PKG_PREFIX + ".lsfileax");
        List <CixsOperation> cixsOperations = new ArrayList <CixsOperation>();
        cixsOperations.add(Cases.getLsfilealOperation());
        cixsOperations.add(Cases.getLsfileacOperation());
        muleComponent.setCixsOperations(cixsOperations);
        return muleComponent;
	}
	/**
	 * @return an operation corresponding to a simple CICS commarea-driven
	 * program case where input and output structures are identical.
	 */
	public static CixsOperation getLsfileaeOperation() {
        CixsOperation cixsOperation = new CixsOperation();
        cixsOperation.setCicsProgramName("LSFILEAE");
        cixsOperation.setName("lsfileae");
        cixsOperation.setFaultType("MuleLsfileaeException");

        List <CixsStructure> inStructures = new ArrayList <CixsStructure>();
        CixsStructure inStructure = new CixsStructure();
        inStructure.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileae");
        inStructure.setJaxbType("DfhcommareaType");
        inStructures.add(inStructure);
        cixsOperation.setInput(inStructures);
        cixsOperation.setRequestHolderType(JAXB_PKG_PREFIX + ".DfhcommareaType");
        
        List <CixsStructure> outStructures = new ArrayList <CixsStructure>();
        CixsStructure outStructure = new CixsStructure();
        outStructure.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileae");
        outStructure.setJaxbType("DfhcommareaType");
        outStructures.add(outStructure);
        cixsOperation.setOutput(outStructures);
        cixsOperation.setResponseHolderType(JAXB_PKG_PREFIX + ".DfhcommareaType");

        return cixsOperation;
	}

	/**
	 * @return an operation corresponding to a CICS commarea-driven program 
	 * case where input and output structures are different.
	 */
	public static CixsOperation getLsfilealOperation() {
        CixsOperation cixsOperation = new CixsOperation();
        cixsOperation.setCicsProgramName("LSFILEAL");
        cixsOperation.setName("lsfileal");
        cixsOperation.setFaultType("MuleLsfilealException");

        List <CixsStructure> inStructures = new ArrayList <CixsStructure>();
        CixsStructure inStructure = new CixsStructure();
        inStructure.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileal");
        inStructure.setJaxbType("RequestParmsType");
        inStructures.add(inStructure);
        cixsOperation.setInput(inStructures);
        cixsOperation.setRequestHolderType(JAXB_PKG_PREFIX + ".RequestParmsType");
        
        List <CixsStructure> outStructures = new ArrayList <CixsStructure>();
        CixsStructure outStructure = new CixsStructure();
        outStructure.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileal");
        outStructure.setJaxbType("ReplyDataType");
        outStructures.add(outStructure);
        cixsOperation.setOutput(outStructures);
        cixsOperation.setResponseHolderType(JAXB_PKG_PREFIX + ".ReplyDataType");

       return cixsOperation;
	}

	/**
	 * @return an operation corresponding to a CICS container-driven program.
	 */
	public static CixsOperation getLsfileacOperation() {
        CixsOperation cixsOperation = new CixsOperation();
        cixsOperation.setCicsProgramName("LSFILEAC");
        cixsOperation.setName("lsfileac");
        cixsOperation.setCicsChannel("LSFILEAC-CHANNEL");
        cixsOperation.setFaultType("MuleLsfileacException");

        List <CixsStructure> inStructures = new ArrayList <CixsStructure>();
        CixsStructure inStructure1 = new CixsStructure();
        inStructure1.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileac");
        inStructure1.setJaxbType("QueryDataType");
        inStructure1.setCicsContainer("QueryData");
        inStructures.add(inStructure1);

        CixsStructure inStructure2 = new CixsStructure();
        inStructure2.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileac");
        inStructure2.setJaxbType("QueryLimitType");
        inStructure2.setCicsContainer("QueryLimit");
        inStructures.add(inStructure2);
        
        cixsOperation.setInput(inStructures);
        cixsOperation.setRequestHolderType("LsfileacRequestHolder");

        List <CixsStructure> outStructures = new ArrayList <CixsStructure>();
        CixsStructure outStructure1 = new CixsStructure();
        outStructure1.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileac");
        outStructure1.setJaxbType("ReplyDataType");
        outStructure1.setCicsContainer("ReplyData");
        outStructures.add(outStructure1);

        CixsStructure outStructure2 = new CixsStructure();
        outStructure2.setJaxbPackageName(JAXB_PKG_PREFIX + ".lsfileac");
        outStructure2.setJaxbType("ReplyStatusType");
        outStructure2.setCicsContainer("ReplyStatus");
        outStructures.add(outStructure2);
        
        cixsOperation.setOutput(outStructures);
        cixsOperation.setResponseHolderType("LsfileacResponseHolder");

        return cixsOperation;
	}

}
