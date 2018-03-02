/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import static main.CSWRL.printOntologyClassIndividuals;
import eu.larkc.csparql.common.RDFTable;
import eu.larkc.csparql.common.RDFTuple;
import eu.larkc.csparql.core.ResultFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import org.semanticweb.owlapi.apibinding.OWLManager;
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
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.swrlapi.core.SWRLAPIRule;
import org.swrlapi.core.SWRLRuleEngine;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;

/**
 *
 * @author Edi
 */
public class CopperMonitoring extends ResultFormatter{

    SWRLRuleEngine _ruleEngine;
    OWLOntology _newOnto;
    OWLReasoner _reasoner;
    DefaultPrefixManager _prefixManager;
    OWLOntologyManager _manager;
    OWLDataFactory _owlDataFactory; 
    CopperMonitoring(SWRLRuleEngine ruleEngine, OWLOntology newOnto, OWLReasoner reasoner, 
            DefaultPrefixManager prefixManager, OWLOntologyManager manager, OWLDataFactory owlDataFactory) {
        this._ruleEngine = ruleEngine;
        this._newOnto = newOnto;
        this._reasoner = reasoner;
        this._prefixManager = prefixManager;
        this._manager = manager;
        this._owlDataFactory = owlDataFactory;
    }

    @Override
    public void update(Observable o, Object arg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        RDFTable res = (RDFTable) arg;
        System.out.println();
        //System.out.println("C-SPARQL output");
	System.out.println("+++++++ "+ res.size() + " new result(s) at SystemTime=["+System.currentTimeMillis()+"]+++++++");
	for (final RDFTuple t : res) {
            //System.out.println(t.toString());
            String rdfStreamWQname = t.get(0);
            String rdfStreamWQavgVal = t.get(1);
            String WQname = null;
            WQname = rdfStreamWQname.substring(rdfStreamWQname.indexOf("#") + 1);
            String WQaverageV = null;
            WQaverageV = rdfStreamWQavgVal.substring(rdfStreamWQavgVal.indexOf("\"") + 1);
            WQaverageV = WQaverageV.substring(0, WQaverageV.indexOf("\""));
            double WQaverageDV;
            WQaverageDV = Double.parseDouble(WQaverageV);
            //print C-SPARQL results
            //System.out.println("Observed WQ: " + WQname+ " with AVG value: " + WQaverageDV);
            String regulOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl";
            String coreOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-core.owl";          
            
            //C-SPARQL delivers the RDF stream of results
            //OWLAPI and SWRLAPI application starts here
            //based on the computed average value for each WQ parameter the SWRL will do assertions on the ontology
            //vjen vlera mesatare me parametrin
            //shto nje individ ne WaterMeasurement
            //aktivizo rule engine qe WFD rregulla perkatese te klasifikoje ate individ ne klasen perkatese
            
            //add individual
            int n = (int) randomWithRange(0, 10000);    //numer i rendomte per nr.e observimit
            OWLIndividual newObservedValue = _owlDataFactory.getOWLNamedIndividual(IRI.create(regulOntoStringIRI + "#" + WQname + n));
            OWLClass tmpObservation = _owlDataFactory.getOWLClass("inwsr:tempObservationMeasurement", _prefixManager);
            OWLClassAssertionAxiom tmpClassAxiom = _owlDataFactory.getOWLClassAssertionAxiom(tmpObservation, newObservedValue);
            _manager.applyChange(new AddAxiom(_newOnto, tmpClassAxiom));
            
            //associate the WaterMeasurement with ObjectProperty value
            //newObservedValue - > hasElement -> WQname
            OWLObjectProperty hasElement = _owlDataFactory.getOWLObjectProperty(IRI.create("http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#hasElement"));
            OWLIndividual WQelement = _owlDataFactory.getOWLNamedIndividual(IRI.create(coreOntoStringIRI + "#" + WQname));
            OWLObjectPropertyAssertionAxiom hasElementAssertion = _owlDataFactory.getOWLObjectPropertyAssertionAxiom(hasElement, newObservedValue, WQelement);
            _manager.applyChange(new AddAxiom(_newOnto, hasElementAssertion));    
            
            //associate the AVG value DataType property
            //newObservedValue - > hasObservedAVGvalue -> WQaverageDV
            OWLDataProperty hasObservedAVGvalue = _owlDataFactory.getOWLDataProperty(IRI.create(regulOntoStringIRI + "#hasObservedAVGvalue"));
            OWLAxiom dataTypeAssertion = _owlDataFactory.getOWLDataPropertyAssertionAxiom(hasObservedAVGvalue, newObservedValue, WQaverageDV);
            _manager.applyChange(new AddAxiom(_newOnto, dataTypeAssertion));

            //RUN SWRL engine
            _ruleEngine.infer();

            //print out C-SWRL RESULTS
            System.out.println("C-SWRL output results");
            _reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
            //printOntologyClassesIndividuals(_newOnto, _reasoner);
            OWLClass clsWaterMeasurement = _owlDataFactory.getOWLClass("twcc:WaterMeasurement", _prefixManager);
            //NodeSet<OWLClass> subClses = _reasoner.getSubClasses(clsWaterMeasurement, true);
//            NodeSet<OWLNamedIndividual> individualsNodeSet = _reasoner.getInstances(clsWaterMeasurement, false);
//            Set<OWLNamedIndividual> individuals = individualsNodeSet.getFlattened();
//            for (OWLNamedIndividual ind : individuals) {
//                System.out.println(" " + ind);
//            }
            _manager.removeAxiom(_newOnto, tmpClassAxiom);
            printOntologyClassIndividuals(_newOnto, tmpObservation, _reasoner);  //direct and indirect instances
            //printSubclassIndividuals(_newOnto, clsWaterMeasurement, _reasoner);
//            //Remove temporary class instances from twcc:tempObservationMeasurement            
            
//            Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
//            for (OWLOntology on : _newOnto.getImportsClosure()) {
//                axiomsToRemove = new HashSet<OWLAxiom>();
//                for (OWLAxiom ax : on.getAxioms()) {
//                    if (ax.getSignature().contains(tmpObservation)) {
//                        axiomsToRemove.add(ax);
//                        System.out.println("to remove from " + on.getOntologyID().getOntologyIRI() + ": " + ax);
//                    }
//                }
//                System.out.println("Before: " + on.getAxiomCount());
//                _manager.removeAxioms(on, axiomsToRemove);
//                System.out.println("After: " + on.getAxiomCount());
//            }
            

//            Model m;
//            m = ModelFactory.createDefaultModel();
//            m = (Model) _newOnto;

//            String input = "D:\\Personal\\Documents\\NetBeansProjects\\CSPARQL096\\src\\csparql096\\removeA5.txt";  //rules filepath
//            File f = new File(input);
//            if (f.exists()) {
//                List<Rule> rules = Rule.rulesFromURL("file:" + input);
//                GenericRuleReasoner r = new GenericRuleReasoner(rules);
//                r.setOWLTranslation(true);           
//                r.setTransitiveClosureCaching(true);
//                
//                InfModel infmodel = ModelFactory.createInfModel(r, m);
//                m.add(infmodel.getDeductionsModel());
//            }
//            else
//                System.out.println("That rules file does not exist.");
	}
        System.out.println();
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

    private double randomWithRange(double min, double max)
    {
       double range = (max - min) + 1;     
       return (double)(Math.random() * range) + min;
    }

    private void printSubclassIndividuals(OWLOntology _newOnto, OWLClass clsWaterMeasurement, OWLReasoner _reasoner) {
        NodeSet<OWLClass> subClses = _reasoner.getSubClasses(clsWaterMeasurement, false);//provo true
        for(Node<OWLClass> c: subClses)
        {
            assert c != null;
            NodeSet<OWLNamedIndividual> instances = _reasoner.getInstances((OWLClassExpression) c, false);
            for (OWLNamedIndividual i : instances.getFlattened()) {
                        assert i != null;
                        System.out.println("Class: "+ c + " Individual: " + i);                    
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
        }
    }
    
}
