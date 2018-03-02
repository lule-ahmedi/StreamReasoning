/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import static com.espertech.esper.util.DatabaseTypeEnum.BigDecimal;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.ResultFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.springframework.util.StopWatch;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

/**
 *
 * @author Edi
 */
public class RDFResultsFormatter extends ResultFormatter{

    SWRLRuleEngine _ruleEngine;
    OWLOntology _newOnto;
    OWLReasoner _reasoner;
    DefaultPrefixManager _prefixManager;
    OWLOntologyManager _manager;
    OWLDataFactory _owlDataFactory; 
    SQWRLQueryEngine _queryEngine;
    RDFResultsFormatter(SWRLRuleEngine ruleEngine, OWLOntology newOnto, OWLReasoner reasoner, 
            DefaultPrefixManager prefixManager, OWLOntologyManager manager, OWLDataFactory owlDataFactory, SQWRLQueryEngine queryEngine) {
        this._ruleEngine = ruleEngine;
        this._newOnto = newOnto;
        this._reasoner = reasoner;
        this._prefixManager = prefixManager;
        this._manager = manager;
        this._owlDataFactory = owlDataFactory;
        this._queryEngine = queryEngine;
    }

    @Override
    public void update(Observable o, Object arg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        RDFTable res = (RDFTable) arg;
        System.out.println();
        String regulOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl";
        String coreOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-core.owl";
        String pollOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-pollutants.owl";
        String ssnOntoStringIRI = "http://purl.oclc.org/NET/ssnx/ssn";
        String timeOntoStringIRI = "http://www.w3.org/2006/time";
        //System.out.println("C-SPARQL output");
	System.out.println("+++++++ "+ res.size() + " new result(s) at SystemTime=["+System.currentTimeMillis()+"] +++++++");
	int numrator=0;long totProcTime = 0;
        for (final RDFTuple t : res) {
            //System.out.println(t.toString());
            numrator++;
            String rdfStreamWQname = t.get(0);
            String rdfStreamWQavgVal = t.get(2);
            String rdfStreamLoc = t.get(1);
            String WQname = null;String WQnameShrt = null; String obsLoc = null;
            WQname = rdfStreamWQname.substring(rdfStreamWQname.indexOf("#") + 1);
            if(WQname.equals("BiochemicalOxygenDemand")) WQnameShrt = "BOD"; else WQnameShrt = WQname;
            String WQaverageV = null;
            WQaverageV = rdfStreamWQavgVal.substring(rdfStreamWQavgVal.indexOf("\"") + 1);
            WQaverageV = WQaverageV.substring(0, WQaverageV.indexOf("\""));
            double WQaverageDV;
            WQaverageDV = Double.parseDouble(WQaverageV);
            obsLoc = rdfStreamLoc.substring(rdfStreamLoc.indexOf("#") + 1);
            int n = (int) randomWithRange(0, 10000);    //numer i rendomte per nr.e observimit   
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = new Date();
            String obsTime = dateFormat.format(date);
            Double truncatedAVGval = new BigDecimal(WQaverageDV).setScale(3, RoundingMode.HALF_UP).doubleValue();
            //System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
            //print C-SPARQL results
            System.out.println("#" + numrator + " (C-SPARQL) WQ: " + WQnameShrt + " Value: " + truncatedAVGval + " Loc: " + obsLoc + " [" + obsTime +"]");
//            System.out.println("Observed WQ: " + WQname);
//            System.out.println("Calculated AVG value: " + WQaverageDV);
//            System.out.println("On location: " + obsLoc);
//            System.out.println();
            
            //firstly, remove the isPolluted value of the ms
            OWLObjectProperty isPolluted = _owlDataFactory.getOWLObjectProperty(IRI.create(pollOntoStringIRI + "#isPolluted"));
            OWLNamedIndividual obsLocInd = _owlDataFactory.getOWLNamedIndividual(IRI.create(rdfStreamLoc));
            
            for (OWLObjectProperty op : _newOnto.getObjectPropertiesInSignature()) {
                assert op != null;
                if(op.getIRI().equals(isPolluted.getIRI())) 
                {
                    //pollutionInd is T or F
                    NodeSet<OWLNamedIndividual> pollutionInd = _reasoner.getObjectPropertyValues(obsLocInd, op);
                    for (OWLNamedIndividual ind : pollutionInd.getFlattened()) {
                        OWLAxiom OLDisPollutedAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(isPolluted, obsLocInd, ind);

                        try
                        {
                            RemoveAxiom rem = new RemoveAxiom(_newOnto, OLDisPollutedAssertion);
                            _manager.applyChange(rem);
                        } catch (OWLOntologyChangeException e) {
                            throw new OWLRuntimeException("Failed removing axiom", e);
                        }
                    }
                }
            }
            //printIsPollutedSiteStatus(rdfStreamLoc);
            
            //add individual
            OWLIndividual newObservedValue = _owlDataFactory.getOWLNamedIndividual(IRI.create(regulOntoStringIRI + "#" + WQnameShrt + n));
            OWLClass tmpObservation = _owlDataFactory.getOWLClass("inwsc:tmpObservation", _prefixManager);
            OWLClassAssertionAxiom tmpClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(tmpObservation, newObservedValue);
            _manager.applyChange(new AddAxiom(_newOnto, tmpClassAxiom));
            
            //associate the WaterMeasurement with ObjectProperty value
            //newObservedValue - > hasElement -> WQname
            //korigjoje me ssn:qualityOfObservation
            OWLObjectProperty hasElement = _owlDataFactory.getOWLObjectProperty("ssn:qualityOfObservation", _prefixManager);
            OWLIndividual WQelement = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#" + WQname));
            OWLObjectPropertyAssertionAxiom hasElementAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(hasElement, newObservedValue, WQelement);
            _manager.applyChange(new AddAxiom(_newOnto, hasElementAssertion));    
            
            //associate the WaterMeasurement with location (ObjectProperty value)
            //newObservedValue - > observationResultLocation -> obsLoc
            OWLObjectProperty observationResultLocation = _owlDataFactory.getOWLObjectProperty("inwsc:observationResultLocation", _prefixManager);
            OWLIndividual obsLocIndividual = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#" + obsLoc));
            OWLObjectPropertyAssertionAxiom observationResultLocationAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(observationResultLocation, newObservedValue, obsLocIndividual);
            _manager.applyChange(new AddAxiom(_newOnto, observationResultLocationAssertion));
            
            //associate the AVG or individual observed value DataType property
            //create new Sensor Output ind
            OWLIndividual newSensorOutputInd = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#so" + WQnameShrt + n));
            OWLClass SensorOutput = _owlDataFactory.getOWLClass("ssn:SensorOutput", _prefixManager);
            OWLClassAssertionAxiom SensorOutputClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(SensorOutput, newSensorOutputInd);
            _manager.applyChange(new AddAxiom(_newOnto, SensorOutputClassAxiom));
            
            //newObservationInd - > ssn:observationResult -> newSensorOutputInd
            OWLObjectProperty observationResultProp = _owlDataFactory.getOWLObjectProperty("ssn:observationResult", _prefixManager);
            OWLObjectPropertyAssertionAxiom obsSensorOutputAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(observationResultProp, newObservedValue, newSensorOutputInd);
            _manager.applyChange(new AddAxiom(_newOnto, obsSensorOutputAssertion));
            
            //lidhja newSensorOutputInd -> ssn:hasValue - > newObservationValueInd
            //create new ObservationValue ind
            OWLIndividual newObservationValueInd = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#ov" + n));
            OWLClass ObservationValue = _owlDataFactory.getOWLClass("ssn:ObservationValue", _prefixManager);
            OWLClassAssertionAxiom ObservationValueClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(ObservationValue, newObservationValueInd);
            _manager.applyChange(new AddAxiom(_newOnto, ObservationValueClassAxiom));
            
            //newSensorOutputInd - > ssn:hasValue - > newObservationValueInd
            OWLObjectProperty hasValueProp = _owlDataFactory.getOWLObjectProperty("ssn:hasValue", _prefixManager);
            OWLObjectPropertyAssertionAxiom hasValueAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(hasValueProp, newSensorOutputInd, newObservationValueInd);
            _manager.applyChange(new AddAxiom(_newOnto, hasValueAssertion));
            
            //newObservationValueInd - > dul:hasDataValue - > WQAveageDV
            OWLDataProperty hasDataValue = _owlDataFactory.getOWLDataProperty("dul:hasDataValue", _prefixManager);
            OWLAxiom dataTypeAssertion = _owlDataFactory.getOWLDataPropertyAssertionAxiom(hasDataValue, newObservationValueInd, truncatedAVGval);
            _manager.applyChange(new AddAxiom(_newOnto, dataTypeAssertion));

            //create new Instant individual
            OWLIndividual newInstantValue = _owlDataFactory.getOWLNamedIndividual(IRI.create(regulOntoStringIRI + "#TmInst" + WQnameShrt + n));
            OWLClass newObservationInstant = _owlDataFactory.getOWLClass("time:Instant", _prefixManager);
            OWLClassAssertionAxiom newInstantAxiom = _owlDataFactory.getOWLClassAssertionAxiom(newObservationInstant, newInstantValue);
            _manager.applyChange(new AddAxiom(_newOnto, newInstantAxiom));
            //newInstantValue -> inXSDDateTime -> current time
            OWLDataProperty inXSDDateTime = _owlDataFactory.getOWLDataProperty("time:inXSDDateTime", _prefixManager);
            OWLAxiom curTimeAssertion = _owlDataFactory.getOWLDataPropertyAssertionAxiom(inXSDDateTime, newInstantValue, obsTime);
            _manager.applyChange(new AddAxiom(_newOnto, curTimeAssertion));
            
            //newObservedValue -> observationResultTime -> newInstantValue
            //http://purl.oclc.org/NET/ssnx/ssn#
            OWLObjectProperty observationResultTime = _owlDataFactory.getOWLObjectProperty("ssn:observationResultTime", _prefixManager);
            //OWLIndividual obsTimeIndividual = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#" + obsLoc));
            OWLObjectPropertyAssertionAxiom obsTimenAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(observationResultTime, newObservedValue, newInstantValue);
            _manager.applyChange(new AddAxiom(_newOnto, obsTimenAssertion));
            
        //print out C-SWRL RESULTS
        //System.out.println("");
        //start time
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //RUN SWRL engine
        _ruleEngine.infer();

        stopWatch.stop();
        System.out.println("(C-SWRL)" 
//                +": " + stopWatch.getTotalTimeMillis() + " ms"
        );
        totProcTime = totProcTime + stopWatch.getTotalTimeMillis();

        _reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);

        OWLClass clstmpModerateBOD = _owlDataFactory.getOWLClass("inwsr:tmpModerateBODMeasurement", _prefixManager);
        OWLClass clstmpGoodBOD = _owlDataFactory.getOWLClass("inwsr:tmpGoodBODMeasurement", _prefixManager);
        OWLClass clstmpHighBOD = _owlDataFactory.getOWLClass("inwsr:tmpHighBODMeasurement", _prefixManager);
        //ph obs classes
        OWLClass clstmpModeratePH = _owlDataFactory.getOWLClass("inwsr:tmpModeratePHMeasurement", _prefixManager);
        OWLClass clstmpGoodHighPH = _owlDataFactory.getOWLClass("inwsr:tmpGoodHighPHMeasurement", _prefixManager);
        
        OWLClass clsModeratePH = _owlDataFactory.getOWLClass("inwsr:ModeratePHMeasurement", _prefixManager);
        OWLClass clsPollutedSite = _owlDataFactory.getOWLClass("twcp:PollutedSite", _prefixManager);
        OWLClass clstmpPollutedSite = _owlDataFactory.getOWLClass("inwsc:tmpPollutedSite", _prefixManager);
        OWLClass clsObservation = _owlDataFactory.getOWLClass("ssn:Observation", _prefixManager);

        //printClassIndividuals(_newOnto, tmpObservation, _reasoner);  //direct and indirect instances
        if(WQnameShrt.equals("BOD"))
        {
            printModerateClassIndividuals(clstmpModerateBOD);  //prints also the pollution sources
            printGoodHighClassIndividuals(clstmpHighBOD, "HIGH");//direct and indirect instances
            printGoodHighClassIndividuals(clstmpGoodBOD, "GOOD");  //direct and indirect instances
            removeClassIndividuals(clstmpModerateBOD);
            removeClassIndividuals(clstmpHighBOD);
            removeClassIndividuals(clstmpGoodBOD);
        }
        //ph print and remove tmp classes
        if(WQname.equals("pH"))
        {
            printModerateClassIndividuals(clstmpModeratePH);    //prints also the pollution sources
            printGoodHighClassIndividuals(clstmpGoodHighPH, "GOOD/HIGH");
            removeClassIndividuals(clstmpModeratePH);
            removeClassIndividuals(clstmpGoodHighPH);
        }
        
        //pastro tmpObservationMeasurement - fshije instancen e observimit
        removeClassIndividuals(tmpObservation);

	}
        //System.out.println();
        //print ms states
        //for real time perception of ms statuses
        //printMSstates();
        //System.out.println("TOTAL PROC.TIME: " + totProcTime);
        
        //save the ontology changes
//        File output;
//        try {
//            //output = temporaryFolder.newFile("saved_pizza.owl");
//            // Output will be deleted on exit; to keep temporary file replace
//            // previous line with the following
//            output = new File("D:\\Personal\\Desktop\\CSWRLontologies\\testSaved.owl");
//            IRI documentIRI2 = IRI.create(output);
//            //documentIRI2 = IRI.create(coreOntoStringIRI);
//            // save in OWL/XML format
//            
//            OWLDocumentFormat format = _manager.getOntologyFormat(_newOnto);
//            OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
//            if (format.isPrefixOWLOntologyFormat()) { 
//              owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat()); 
//            }
//            _manager.saveOntology(_newOnto, owlxmlFormat, IRI.create(output.toURI()));
//            //OWLDocumentFormat format = _manager.getOntologyFormat(_newOnto);
//            // save in RDF/XML
//            //_manager.saveOntology(_newOnto, format);
//        } catch (OWLOntologyStorageException ex) {
//            Logger.getLogger(RDFResultsFormatter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    private double randomWithRange(double min, double max)
    {
       double range = (max - min) + 1;     
       return (double)(Math.random() * range) + min;
    }
    
    public void printClassIndividuals(OWLClass cl)
    {
        String clName = cl.getIRI().toString().substring(cl.getIRI().toString().indexOf("#") + 1);
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        for (OWLNamedIndividual ind : individuals) {
            String indName = ind.getIRI().toString().substring(ind.getIRI().toString().indexOf("#") + 1);
            System.out.println("Class: "+ clName + "\nIndividual: " + indName);    
        }
        System.out.println();
    }
    
    public void printModerateClassIndividuals(OWLClass cl)
    {
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        for (OWLNamedIndividual ind : individuals) {
            String indName = ind.getIRI().toString().substring(ind.getIRI().toString().indexOf("#") + 1);
            System.out.println("MODERATE status detected: " + indName);  
            // look up all property assertions
            OWLObjectProperty foundPollutionSourcesOP = _owlDataFactory.getOWLObjectProperty("inwsr:foundPollutionSources", _prefixManager);
            for (OWLObjectProperty op : _newOnto.getObjectPropertiesInSignature()) {
                assert op != null;
                NodeSet<OWLNamedIndividual> foundPollSourcesNodeSet = _reasoner.getObjectPropertyValues(ind, op);
                for (OWLNamedIndividual pollSourcesInd : foundPollSourcesNodeSet.getFlattened()) {
                    if(op.getIRI().equals(foundPollutionSourcesOP.getIRI())) 
                    {                    
                        //assertNotNull(value);
                        // use the value individuals
                        System.out.println("Pollution source: " + ReturnIRIResourceName(pollSourcesInd.getIRI().toString()).replace('_', ' '));
                    }
                }
            }
        }
    }
    
    public void printIsPollutedSiteStatus(String obsLoc)
    {
        OWLObjectProperty isPolluted = _owlDataFactory.getOWLObjectProperty(IRI.create( "http://inwatersense.uni-pr.edu/ontologies/inws-pollutants.owl#isPolluted"));
        OWLNamedIndividual obsLocInd = _owlDataFactory.getOWLNamedIndividual(IRI.create(obsLoc));
        String txt;
        for (OWLObjectProperty op : _newOnto.getObjectPropertiesInSignature()) {
            assert op != null;
            if(op.getIRI().equals(isPolluted.getIRI())) 
            {
                //pollutionVal is T or F
                NodeSet<OWLNamedIndividual> pollutionInd = _reasoner.getObjectPropertyValues(obsLocInd, op);
                for (OWLNamedIndividual ind : pollutionInd.getFlattened()) {
                    //OWLAxiom isPollutedAssertion = _owlDataFactory.getOWLDataPropertyAssertionAxiom(isPolluted, obsLocInd, indVal);
                    //_manager.removeAxiom(_newOnto, isPollutedAssertion);
                    if(ReturnIRIResourceName(ind.getIRI().toString()).equals("true"))
                        txt = "POLLUTED";
                    else
                        txt = "CLEAN";
                    System.out.println(ReturnIRIResourceName(obsLoc) + " is " + txt);
                }

            }
        }        
    }
    
    public void printGoodHighClassIndividuals(OWLClass cl, String statusName )
    {
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        for (OWLNamedIndividual ind : individuals) {
            String indName = ind.getIRI().toString().substring(ind.getIRI().toString().indexOf("#") + 1);
            System.out.println(statusName + " status detected: " + indName);  
            // look up all property assertions
//            OWLObjectProperty foundPollutionSourcesOP = _owlDataFactory.getOWLObjectProperty("inwsr:foundPollutionSources", _prefixManager);
//            for (OWLObjectProperty op : _newOnto.getObjectPropertiesInSignature()) {
//                assert op != null;
//                NodeSet<OWLNamedIndividual> foundPollSourcesNodeSet = _reasoner.getObjectPropertyValues(ind, op);
//                for (OWLNamedIndividual pollSourcesInd : foundPollSourcesNodeSet.getFlattened()) {
//                    if(op.getIRI().equals(foundPollutionSourcesOP.getIRI())) 
//                    {                    
//                        //assertNotNull(value);
//                        // use the value individuals
//                        System.out.println("Pollution source: " + pollSourcesInd);
//                    }
//                }
//            }
        }
    }
    
    //nuk shfrytezohet
    //shembull se si fshihet nje instance bshke me te gjtha vetite e saja
    public void removePollutedClassIndividuals(OWLClass cl)
    {
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        for (OWLNamedIndividual ind : individuals) {           
            OWLObjectProperty pollutingElementOP = _owlDataFactory.getOWLObjectProperty("inwsc:pollutingElement", _prefixManager);
            OWLObjectProperty observationResultTimeOP = _owlDataFactory.getOWLObjectProperty("ssn:observationResultTime", _prefixManager);
            for (OWLObjectProperty op : _newOnto.getObjectPropertiesInSignature()) {
                assert op != null;
                NodeSet<OWLNamedIndividual> pollutingElementNodeSet = _reasoner.getObjectPropertyValues(ind, op);
                for (OWLNamedIndividual rangeValue : pollutingElementNodeSet.getFlattened()) {
                    //pse e ndryshme
                    if(op.getIRI().equals(pollutingElementOP.getIRI())) 
                    {
                        //remove inwsc:pollutingElement(?ms, inwsc:BiochemicalOxygenDemand) 
                        OWLAxiom pollElemAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(pollutingElementOP, ind, rangeValue);
                        _manager.removeAxiom(_newOnto, pollElemAssertion);
                    } else if(op.getIRI().equals(observationResultTimeOP.getIRI()))
                    {//remove ssn:observationResultTime(?ms, ?t)
                        OWLAxiom obsResTmAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(observationResultTimeOP, ind, rangeValue);
                        _manager.removeAxiom(_newOnto, obsResTmAssertion);
                    }
                }
            }
            
            //remove from class
            OWLClassAssertionAxiom newClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(cl, ind);
            _manager.removeAxiom(_newOnto, newClassAxiom);
               
//            for (OWLDataProperty dp : ontology.getDataPropertiesInSignature()) {
//                assert dp != null;
//                Set<OWLLiteral> rangeValue = reasoner.getDataPropertyValues(ind, dp);
//                if(rangeValue.size() > 0){
//                    String nameInd = rangeValue.iterator().next().getLiteral();
//                    
//                }
//            }
        }
    }
    
    public void removeClassIndividuals(OWLClass cl)
    {
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
        for (OWLNamedIndividual ind : individuals) {
            //System.out.println("Class: "+ cl + "\nIndividual: " + ind);
            OWLClassAssertionAxiom newClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(cl, ind);
            _manager.removeAxiom(_newOnto, newClassAxiom);
        }
    }
    
    //nuk shfrytezohet me
    public void printPollutedSiteIndividuals(OWLClass cl)
    {
        String clName = cl.getIRI().toString().substring(cl.getIRI().toString().indexOf("#") + 1);
        NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(cl, false);
        Set<OWLNamedIndividual> measurementSites = individualsNodeSet.getFlattened();
        
        SQWRLResult result;
        for (OWLNamedIndividual ind : measurementSites) {
            String indName = ind.getIRI().toString().substring(ind.getIRI().toString().indexOf("#") + 1);
            System.out.println(clName + ": " + indName);
                        
            //find pollutingElement
            //map pollutingElement property
            OWLObjectProperty pollutingElement = _owlDataFactory.getOWLObjectProperty(IRI.create("http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#pollutingElement"));
            //get MS property values
            NodeSet<OWLNamedIndividual> pollutingElValuesNodeSet = _reasoner.getObjectPropertyValues(ind, pollutingElement);
            //per cdo polluting element te MS nxjerri (sqwrl) ndotesit e mundshem  
            
            for (OWLNamedIndividual polEl : pollutingElValuesNodeSet.getFlattened()) {
                    //assertNotNull(value);
                    // use the value individuals
                    String polElName = polEl.getIRI().toString().substring(polEl.getIRI().toString().indexOf("#") + 1);
                    System.out.println("Polluting element: " + polElName);
                    System.out.println();

                    // Create and execute a SQWRL query using the SQWRLAPI
                    try {
                        result = _queryEngine.runSQWRLQuery("q1","twcc:MeasurementSite(inwsc:" + indName
                                + ") ^ inwsp:hasSourcesOfPollution(inwsc:" + indName+ ", ?pollsrc) ^ " + 
                                "inwsp:potentialPollutant(?pollsrc, inwsc:"+ polElName +") ^ ssn:observationResultTime(inwsc:" + indName + 
                                ", ?t) ^ time:inXSDDateTime(?t, ?time) -> sqwrl:select(?pollsrc, ?time)");
                        // Process the SQWRL result
                        while (result.next()){ 
                            String polSrcInd = result.getNamedIndividual("pollsrc").getIRI().toString();
                            String polSrcName = polSrcInd.substring(polSrcInd.indexOf("#") + 1);
                            String timeStamp = result.getValue("time").toString();
                            System.out.println("Potential pollutant:");
                            System.out.println(polSrcName + " [" + timeStamp + "]");
                        }
                    } catch (SWRLParseException | SQWRLException ex) {
                        Logger.getLogger(RDFResultsFormatter.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }
        }
    }
    public String ReturnIRIResourceName(String iri)
    {
        return iri.substring(iri.indexOf("#") + 1);
    }

    //printon gjendjet aktuale te ms
    private void printMSstates() {
        OWLClass msClass = _owlDataFactory.getOWLClass("twcc:MeasurementSite", _prefixManager);
        NodeSet<OWLNamedIndividual> msNodeSet = _reasoner.getInstances(msClass, false);
        Set<OWLNamedIndividual> measurementSites = msNodeSet.getFlattened();
        for (OWLNamedIndividual ind : measurementSites) {
            printIsPollutedSiteStatus(ind.getIRI().toString());
        }
    }
}
