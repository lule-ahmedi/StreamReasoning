/*******************************************************************************
 * Copyright 2016 Edmond Jajaga, Lule Ahmedi, Figene Ahmedi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Acknowledgements:
 * 
 * This work was partially supported by the European project InWaterSense
 ******************************************************************************/
package main;

import streamer.INWSRDFStreamTestGenerator;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import eu.larkc.csparql.readytogopack.streamer.BasicIntegerRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.BasicRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.CloudMonitoringRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.DoorsTestStreamGenerator;
import eu.larkc.csparql.readytogopack.streamer.LBSMARDFStreamTestGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
//import static junit.framework.Assert.assertNotNull;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

public class CSWRL {

	private static Logger logger = LoggerFactory.getLogger(CSWRL.class);

	public static void main(String[] args) {
            try {
              // Get hold of an ontology manager
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                // Get a reference to a data factory from an OWLOntologyManager.
                OWLDataFactory owlDataFactory = manager.getOWLDataFactory();
                //set ontology IRI and system path
                String coreOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-core.owl";
                String coreOntoSystemPath = "D:\\Personal\\Documents\\NetBeansProjects\\CSWRL_1.0\\src\\ontologies\\inws-core.owl";
                String regulOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl";
                String regulOntoSystemPath = "D:\\Personal\\Documents\\NetBeansProjects\\CSWRL_1.0\\src\\ontologies\\inws-regulations.owl";
                String pollOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-pollutants.owl";
                String pollOntoSystemPath = "D:\\Personal\\Documents\\NetBeansProjects\\CSWRL_1.0\\src\\ontologies\\inws-pollutants.owl";
//                String staticRDFStringIRI = "file:/C:/d2rq-0.8.1/Final.owl";
//                String staticRDFSystemPath = "D:\\Personal\\Desktop\\InWaterJessSense\\Ontologies\\datasetFINAL20rows.owl";

                IRI newOntologyIRI = IRI.create( "http://inwatersense.uni-pr.edu/ontologies/inws-all.owl" );
                OWLOntology newOnto;
                newOnto = manager.createOntology(newOntologyIRI);

                //IMPORTING ONTOLOGIES
                //import CORE ontology
                InputStream inputCoreOntology = new FileInputStream(coreOntoSystemPath); 
                IRI coreOntoIRI = IRI.create(coreOntoStringIRI);
                SimpleIRIMapper iriMapper =  new SimpleIRIMapper(coreOntoIRI, IRI.create(new File(coreOntoSystemPath)));
                manager.getIRIMappers().add(iriMapper);
                //OWLImportsDeclaration importDeclaraton = factory.getOWLImportsDeclaration(IRI.create("http://inwatersense.uni-pr.edu/ontologies/inws-core.owl")); 
                OWLOntology owlCoreModel = manager.loadOntologyFromOntologyDocument(inputCoreOntology);
                DefaultPrefixManager prefixManager = new DefaultPrefixManager(null, null, coreOntoStringIRI);
                OWLDocumentFormat format = owlCoreModel.getOWLOntologyManager().getOntologyFormat(owlCoreModel);
                if (format.isPrefixOWLOntologyFormat())
                    prefixManager.copyPrefixesFrom(format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());

                OWLImportsDeclaration importDeclaration = owlDataFactory.getOWLImportsDeclaration(coreOntoIRI);
                manager.applyChange(new AddImport(newOnto, importDeclaration));

                //import Regulations ontology
                InputStream inputRegulationsOntology = new FileInputStream(regulOntoSystemPath); 
                IRI regulOntoIRI = IRI.create(regulOntoStringIRI);
                iriMapper =  new SimpleIRIMapper(regulOntoIRI, IRI.create(new File(regulOntoSystemPath)));
                manager.getIRIMappers().add(iriMapper);
                OWLOntology owlRegModel = manager.loadOntologyFromOntologyDocument(inputRegulationsOntology);
                format = owlCoreModel.getOWLOntologyManager().getOntologyFormat(owlRegModel);
                if (format.isPrefixOWLOntologyFormat())
                    prefixManager.copyPrefixesFrom(format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());
                importDeclaration = owlDataFactory.getOWLImportsDeclaration(regulOntoIRI);
                manager.applyChange(new AddImport(newOnto, importDeclaration));

                //import Pollutants ontology
                InputStream inputPollutantsOntology = new FileInputStream(pollOntoSystemPath); 
                IRI pollOntoIRI = IRI.create(pollOntoStringIRI);
                iriMapper =  new SimpleIRIMapper(pollOntoIRI, IRI.create(new File(pollOntoSystemPath)));
                manager.getIRIMappers().add(iriMapper);
                OWLOntology owlPollModel = manager.loadOntologyFromOntologyDocument(inputPollutantsOntology);
                format = owlCoreModel.getOWLOntologyManager().getOntologyFormat(owlPollModel);
                if (format.isPrefixOWLOntologyFormat())
                    prefixManager.copyPrefixesFrom(format.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap());
                importDeclaration = owlDataFactory.getOWLImportsDeclaration(pollOntoIRI);
                manager.applyChange(new AddImport(newOnto, importDeclaration));

                System.out.println("Loaded ontology: " + newOnto);   

                printOntologyAndImports(manager, newOnto);

                OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
                OWLReasoner reasoner = reasonerFactory.createReasoner(newOnto);
                reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

                //printOntologyClassStructure(reasoner, newOnto);

                //create SWRL and SQWRL engines
                SWRLRuleEngine ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(newOnto, prefixManager); //^ swrlb:lessThan(?z, 1.5)
                SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(newOnto, prefixManager);
                                
                //list of WFD SWRL rules
                //BOD class & inves.
                //1.3<x<1.5 GOOD
                ruleEngine.createSWRLRule("WFDclassifyGoodBODrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:BiochemicalOxygenDemand) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 1.3) ^ "
                        + "swrlb:lessThan(?z, 1.5) "
                        + "-> inwsr:GoodBODMeasurement(?x) ^ inwsr:tmpGoodBODMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                //<1.3 HIGH
                ruleEngine.createSWRLRule("WFDclassifyHighBODrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:BiochemicalOxygenDemand) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 1.3) "
                        + "-> inwsr:HighBODMeasurement(?x)^ inwsr:tmpHighBODMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ "
                                + "ssn:Observation(?x) "
                );
                //>1.5 Moderate
                ruleEngine.createSWRLRule("WFDclassifyModerateBODrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:BiochemicalOxygenDemand) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 1.5) "
                        + "-> inwsr:ModerateBODMeasurement(?x) ^ inwsr:tmpModerateBODMeasurement(?x)^ "
                                + "inwsp:isPolluted(?ms, inwsp:true) ^ "
                                + "ssn:Observation(?x)"
                );

                //Arsenic class & inves.
                //25<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodArsenicrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Arsenic) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 25) "
                        + "-> inwsr:GoodHighArsenicMeasurement(?x) ^ inwsr:tmpGoodHighArsenicMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //>25 Moderate
                ruleEngine.createSWRLRule("WFDclassifyModerateArsenicrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Arsenic) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 25) "
                        + "-> inwsr:ModerateArsenicMeasurement(?x) ^ inwsr:tmpModerateArsenicMeasurement(?x)^ "
                                + "inwsp:isPolluted(?ms, inwsp:true) ^ "
                                + "ssn:Observation(?x)"
                );
                
                //Benthic class & inves.
                //0.75<x<0.85 GOOD
                ruleEngine.createSWRLRule("WFDclassifyGoodBenthicrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:BenthicInvertebrateFauna) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 0.75) ^ "
                        + "swrlb:lessThan(?z, 0.85) "
                        + "-> inwsr:GoodBenthicMeasurement(?x) ^ inwsr:tmpGoodBenthicMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                                
                //ChromiumIII class & inves.
                //4.7<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodChromiumIIIrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:ChromiumIII) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighChromiumIIIMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //Cyanide class & inves.
                //4.7<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodCyaniderule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Cyanide) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighCyanideMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //Diazinon class & inves.
                //0.01<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodDiazinonrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Diazinon) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighDiazinonMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //Dimethoate class & inves.
                //0.01<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodDimethoaterule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Dimethoate) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighDimethoateMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //Fluoride class & inves.
                //0.01<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodFluoriderule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Fluoride) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighFluorideMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //Glyphosate class & inves.
                //0.01<x GOOD/HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodGlyphosaterule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:Glyphosate) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.7) "
                        + "-> inwsr:GoodHighGlyphosateMeasurement(?x) ^"
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //TotalAmmonia class & inves.
                //0.75<x<0.85 GOOD
                ruleEngine.createSWRLRule("WFDclassifyGoodTotalAmmoniarule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:TotalAmmonia) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 0.75) ^ "
                        + "swrlb:lessThan(?z, 0.85) "
                        + "-> inwsr:GoodTotalAmmoniaMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ " 
                                + "ssn:Observation(?x) "    //archive observation
                );
                
                //regjistro PollutedSite ne kohen e detektuar
                ruleEngine.createSWRLRule("findBODPollutantsRule", 
                        "inwsr:tmpModerateBODMeasurement(?x) ^ "
                        + "ssn:observationResultTime(?x, ?t) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "inwsp:hasSourcesOfPollution(?ms,?pollsrc) ^ "
                        + "inwsp:potentialPollutant(?pollsrc, inwsc:BiochemicalOxygenDemand) "
                        + "-> "
                                //per te regjistruar momentin e zones se ndotur dhe ndotesin
                                + "twcp:PollutedSite(?ms) ^ "
                                + "inwsc:pollutingElement(?ms, inwsc:BiochemicalOxygenDemand) ^ "
                                + "ssn:observationResultTime(?ms, ?t)"
                );

                //regjistro matjen ModerateBODMeasurement me ndotesit potencial
                ruleEngine.createSWRLRule("findBODPollutionSourcesRule", 
                        "inwsr:tmpModerateBODMeasurement(?x) ^ "
                        + "ssn:observationResultTime(?x, ?t) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "inwsp:hasSourcesOfPollution(?ms,?pollsrc) ^ "
                        + "inwsp:potentialPollutant(?pollsrc, inwsc:BiochemicalOxygenDemand) "
                        + "-> "
                        + "inwsr:foundPollutionSources(?x, ?pollsrc)"
                );
                
                //PH obs. class. & investigations
                //4.5<x<9 GOOD-HIGH
                ruleEngine.createSWRLRule("WFDclassifyGoodPHrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:pH) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 4.5) ^ "
                        + "swrlb:lessThan(?z, 9) "
                        + "-> inwsr:GoodHighPHMeasurement(?x) ^ inwsr:tmpGoodHighPHMeasurement(?x) ^ "
                                + "inwsp:isPolluted(?ms, inwsp:false) ^ "
                                + "ssn:Observation(?x) "    //archive observation
                );
                //>9 MODERATE
                ruleEngine.createSWRLRule("WFDclassifyModerate1PHrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:pH) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:greaterThan(?z, 9)"
                        + "-> inwsr:ModeratePHMeasurement(?x) ^ inwsr:tmpModeratePHMeasurement(?x)^ "
                                + "inwsp:isPolluted(?ms, inwsp:true) ^ "
                                + "ssn:Observation(?x)"
                );
                //<4.5 MODERATE
                ruleEngine.createSWRLRule("WFDclassifyModerate2PHrule", 
                        "inwsc:tmpObservation(?x) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "ssn:qualityOfObservation(?x, inwsc:pH) ^ "
                        + "ssn:observationResult(?x, ?y) ^ "
                        + "ssn:hasValue(?y, ?e) ^ "
                        + "dul:hasDataValue(?e,?z) ^ "
                        + "swrlb:lessThan(?z, 4.5) "
                        + "-> inwsr:ModeratePHMeasurement(?x) ^ inwsr:tmpModeratePHMeasurement(?x)^ "
                                + "inwsp:isPolluted(?ms, inwsp:true) ^ "
                                + "ssn:Observation(?x)"
                );

//                  regjistro matjen ModeratePHMeasurement me ndotesit potencial
                ruleEngine.createSWRLRule("findPHPollutionSourcesRule", 
                        "inwsr:tmpModeratePHMeasurement(?x) ^ "
                        + "ssn:observationResultTime(?x, ?t) ^ "
                        + "inwsc:observationResultLocation(?x, ?ms) ^ "
                        + "inwsp:hasSourcesOfPollution(?ms,?pollsrc) ^ "
                        + "inwsp:potentialPollutant(?pollsrc, inwsc:pH) "
                        + "-> "
                        + "inwsr:foundPollutionSources(?x, ?pollsrc)"
                );
                
                //first rule engine initiatialization
                ruleEngine.infer();
                    
		String queryAvgObs = null;
                String queryIndObs = null;
                String queryNAFObs = null;
		RdfStream tg = null;
		//RdfStream anotherTg = null;

		// Initialize C-SPARQL Engine
		CsparqlEngine engine = new CsparqlEngineImpl();
		
		/*
		 * Choose one of the the following initialize methods: 
		 * 1 - initialize() - Inactive timestamp function - Inactive injecter 
		 * 2 - initialize(int* queueDimension) - Inactive timestamp function -
		 *     Active injecter with the specified queue dimension (if 
		 *     queueDimension = 0 the injecter will be inactive) 
		 * 3 - initialize(boolean performTimestampFunction) - if
		 *     performTimestampFunction = true, the timestamp function will be
		 *     activated - Inactive injecter 
		 * 4 - initialize(int queueDimension, boolean performTimestampFunction) - 
		 *     if performTimestampFunction = true, the timestamp function will
		 *     be activated - Active injecter with the specified queue dimension
		 *     (if queueDimension = 0 the injecter will be inactive)
		 */
		engine.initialize(true);
		queryAvgObs = "REGISTER STREAM AvgObservations AS "
                                + "PREFIX inwsc: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> "
                                + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> "
                                + "SELECT ?qo ?loc (AVG(?dv) AS ?avg) "
                                //+ "(COUNT(?dv) AS ?num)"
                                + "FROM STREAM <http://inwatersense.uni-pr.edu/stream> [RANGE 20s TUMBLING] "
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/datasetFINAL.owl> "
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl> "
                                + "WHERE { "
                                + "?o ssn:qualityOfObservation ?qo . "
                                + "?o ssn:observationResult ?r . "
                                + "?r ssn:hasValue ?v . "
                                + "?v dul:hasDataValue ?dv . "
                                + "?o inwsc:observationResultLocation ?loc "
                                + "FILTER (?qo != inwsc:pH)"
                                + "} "
                                + "GROUP BY ?qo ?loc ";
		tg = new INWSRDFStreamTestGenerator("http://inwatersense.uni-pr.edu/stream");

                queryIndObs = "REGISTER STREAM IndObservations  AS "
                                + "PREFIX inwsc: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> \n"
                                + "PREFIX inwsr: <http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl#> \n"
                                + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> \n"
                                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> \n"
                                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
                                + "SELECT ?qo ?loc ?dv \n"
                                //+ "(COUNT(?dv) AS ?num)"
                                + "FROM STREAM <http://inwatersense.uni-pr.edu/stream> [RANGE 30s TUMBLING] \n"
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/datasetFINAL.owl> "
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl> "
                                + "WHERE { \n"
                                + "?o ssn:qualityOfObservation ?qo . \n"
                                + "?o ssn:observationResult ?r . \n"
                                + "?r ssn:hasValue ?v . \n"
                                + "?v dul:hasDataValue ?dv . \n"
                                + "?o inwsc:observationResultLocation ?loc \n"
                                + "FILTER (?qo = inwsc:pH) \n"
                                + "} ";
                                //+ "GROUP BY ?qo ?loc";
                //anotherTg = new INWSRDFStreamTestGenerator("http://inwatersense.uni-pr.edu/stream");
                
                //NAF query example
                //assigning the „un-determined status‟ to measurement sites to which the data are missing
                queryNAFObs = "REGISTER STREAM NAFObservations  AS "
                                + "PREFIX inwsc: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> \n"
                                + "PREFIX inwsp: <http://inwatersense.uni-pr.edu/ontologies/inws-pollutants.owl#> \n"
                                + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> \n"
                                + "PREFIX twcc: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> \n"
                                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                                + "SELECT ?ms \n"
                                //+ "(COUNT(?dv) AS ?num)"
                                + "FROM STREAM <http://inwatersense.uni-pr.edu/stream> [RANGE 20s STEP 20s] \n"
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/datasetFINAL.owl> "
//                                + "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl> "
//                                + "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl> "
//                                + "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-pollutants.owl> "
                                //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/data.rdf> "
                                + "WHERE { \n"
                                + "?o inwsc:observationResultLocation ?ms \n"
                                + "OPTIONAL { ?ms inwsp:isPolluted ?tf } . \n"
                                + "FILTER (!BOUND(?tf)) \n"
                                + "} ";
                
		// Register an RDF Stream
		engine.registerStream(tg);

		// Start Streaming (this is only needed for the example, normally streams are external
		// C-SPARQL Engine users are supposed to write their own adapters to create RDF streams

		final Thread t = new Thread((Runnable) tg);
		t.start();
                //nese duam te regjistrojme streamer tjeter
//		if (anotherTg != null) {
//			engine.registerStream(anotherTg);
//			if (key != COMPOSABILITY) {
//				final Thread t2 = new Thread((Runnable) anotherTg);
//				t2.start();
//			}
//		}

		// Register a C-SPARQL query

		CsparqlQueryResultProxy c1 = null;
		CsparqlQueryResultProxy c2 = null;
                CsparqlQueryResultProxy c3 = null;

                
                try {
                    c1 = engine.registerQuery(queryAvgObs, false);
                    logger.debug("Query: {}", queryAvgObs);
                    logger.debug("Query Start Time : {}", System.currentTimeMillis());

                    c2 = engine.registerQuery(queryIndObs, false);
                    logger.debug("Query: {}", queryIndObs);
                    logger.debug("Query Start Time : {}", System.currentTimeMillis());
                    
//                    c3 = engine.registerQuery(queryNAFObs, false);
//                    logger.debug("Query: {}", queryNAFObs);
//                    logger.debug("Query Start Time : {}", System.currentTimeMillis());
                } catch (final ParseException ex) {
                    logger.error(ex.getMessage(), ex);
                }
			
                // Attach a Result Formatter to the query result proxy
                if (c1 != null) {
                        //c1.addObserver(new ConsoleFormatter());
                    //dergoi variablat e nevojshme    
                    c1.addObserver(new RDFResultsFormatter(ruleEngine, newOnto, reasoner, prefixManager, manager, owlDataFactory, queryEngine));
                }
                if (c2 != null) {
                    c2.addObserver(new RDFResultsFormatter(ruleEngine, newOnto, reasoner, prefixManager, manager, owlDataFactory, queryEngine));
                }
                if (c3 != null) {
                    c3.addObserver(new NAFRDFResultsFormatter(ruleEngine, newOnto, reasoner, prefixManager, manager, owlDataFactory, queryEngine));
                }


                // leave the system running for a while
                // normally the C-SPARQL Engine should be left running
                // the following code shows how to stop the C-SPARQL Engine gracefully
                // per ta ndal C-SPARQL
                try {
                        Thread.sleep(6000000);
                } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                }

                    //if (key != COMPOSABILITY) {
                            // clean up (i.e., unregister query and stream)
                if (c1 != null) {
                engine.unregisterQuery(c1.getId());
                }
                if (c2 != null) {
                engine.unregisterQuery(c2.getId());
                }
                if (c3 != null) {
                engine.unregisterStream(c3.getId());
                }

                engine.unregisterStream(tg.getIRI());
                            //if (anotherTg != null) {
        //				engine.unregisterStream(anotherTg.getIRI());
        //			}
                ((INWSRDFStreamTestGenerator) tg).pleaseStop();

                engine.unregisterStream(tg.getIRI());


                System.exit(0);

                } catch (OWLOntologyCreationException | FileNotFoundException | SWRLParseException ex) {
                    java.util.logging.Logger.getLogger(CSWRL.class.getName()).log(Level.SEVERE, null, ex);
                }
	}
              
        public static void printOntologyClassIndividuals(OWLOntology ontology, OWLClass cl, OWLReasoner reasoner)
        {
            // Look up and print all direct subclasses for all classes
            // a NodeSet represents a set of Nodes.
            // a Node represents a set of equival  ent classes
            for (OWLClass c : ontology.getClassesInSignature()) {
                assert c != null;
                //if(c.getIRI().getFragment().equals("tempObservationMeasurement") || c.getIRI().getFragment().equals("HighBODMeasurement") || c.getIRI().getFragment().equals("GoodBODMeasurement"))
                //if(c.getIRI().getFragment().equals("WaterMeasurement"))
                //{
                    // the boolean argument specifies direct subclasses; false would
                    // specify all subclasses
                    // a NodeSet represents a set of Nodes.
                    // a Node represents a set of equivalent classes/or sameAs
                    // individuals
                    NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(c, false);//perdor false per instancat indirekte te nenklasave
                    for (OWLNamedIndividual i : instances.getFlattened()) {
                        assert i != null;
                        System.out.println("Class: "+ c + "\nIndividual: " + i);                    
                        // look up all property assertions
        //                for (OWLObjectProperty op : ontology.getObjectPropertiesInSignature()) {
        //                    assert op != null;
        //                    NodeSet<OWLNamedIndividual> petValuesNodeSet = reasoner.getObjectPropertyValues(i, op);
        //                    for (OWLNamedIndividual value : petValuesNodeSet.getFlattened()) {
        //                        //assertNotNull(value);
        //                        // use the value individuals
        //                        System.out.println(" " + value);
        //                    }
        //                }
                    }
                //}
            }
        }
        
        
        
        private static void printOntologyAndImports(@Nonnull OWLOntologyManager manager, @Nonnull OWLOntology ontology) {
        // Print ontology IRI and where it was loaded from (they will be the
        // same)
        printOntology(manager, ontology);
        // List the imported ontologies
        for (OWLOntology o : ontology.getImports()) {
            printOntology(manager, o);
        }
        
        
        }
        private static void printOntology(@Nonnull OWLOntologyManager manager, @Nonnull OWLOntology ontology) {
        IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI().get();
        IRI documentIRI = manager.getOntologyDocumentIRI(ontology);
         System.out.println(ontologyIRI == null ? "anonymous" : ontologyIRI
         .toQuotedString());
         System.out.println(" from " + documentIRI.toQuotedString());
        }

                            //print ontology classes
        private static void printOntologyClassStructure(@Nonnull OWLReasoner reasoner, @Nonnull OWLOntology ontology) {
            for (OWLOntology o : ontology.getImports()) {
                for (OWLClass c : o.getClassesInSignature()) {
                    assert c != null;
                    // the boolean argument specifies direct subclasses; false would
                    // specify all subclasses
                    // a NodeSet represents a set of Nodes.
                    // a Node represents a set of equivalent classes
                    NodeSet<OWLClass> subClasses = reasoner.getSubClasses(c, true);
                    for (OWLClass subClass : subClasses.getFlattened()) {
//                        assertNotNull(subClass);
                        // process all subclasses no matter what node they're in
                        System.out.println(subClass);
                    }
                }
            }
        }
}
