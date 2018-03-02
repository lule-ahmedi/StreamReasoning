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
import jess.JessException;
import jess.Rete;
import org.springframework.util.StopWatch;

/**
 *
 * @author Edi
 */
public class RDFResultsFormatter extends ResultFormatter{

    Rete _r;
    RDFResultsFormatter(Rete r) {
        this._r = r;
    }

    @Override
    public void update(Observable o, Object arg) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        RDFTable res = (RDFTable) arg;
        System.out.println();
        String regulOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl";
        String coreOntoStringIRI = "http://inwatersense.uni-pr.edu/ontologies/inws-core.owl";
        String ssnOntoStringIRI = "http://purl.oclc.org/NET/ssnx/ssn";
        String timeOntoStringIRI = "http://www.w3.org/2006/time";
        String dulOntoStringIRI = "http://www.loa-cnr.it/ontologies/DUL.owl";
        //String inwsOntologies = "\"D:\\\\Personal\\\\Desktop\\\\ontologji funksionale per streamJess\\\\inws-all3.pprj\""; 

        //System.out.println("C-SPARQL output");
	System.out.println("+++++++ "+ res.size() + " new C-SPARQL result(s) at SystemTime=["+System.currentTimeMillis()+"] +++++++");
	int numrator=0; long totProcTime = 0;
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
            //print C-SPARQL results
            //System.out.println("#" + numrator + " (C-SPARQL) WQ: " + WQnameShrt + " Value: " + truncatedAVGval + " Loc: " + obsLoc + " [" + obsTime +"]");
            //System.out.println("On location: " + obsLoc);
            //System.out.println();
            try {
                //shto nje individ ne tmpObservation
                _r.eval("(make-instance " + regulOntoStringIRI + "#o" + WQnameShrt + n +" of "+ coreOntoStringIRI +"#tmpObservation map)");
                
                //associate the WaterMeasurement with ObjectProperty value
                //newObservedValue - > hasElement -> WQname
                _r.eval("(slot-insert$ " + regulOntoStringIRI + "#o" + WQnameShrt + n + " " + ssnOntoStringIRI + "#qualityOfObservation 1 "+ coreOntoStringIRI + "#" + WQname +")");
            
                //associate the Location (ObjectProperty value)
                _r.eval("(slot-insert$ " + regulOntoStringIRI + "#o" + WQnameShrt + n + " " + coreOntoStringIRI + "#observationResultLocation 1 "+ coreOntoStringIRI + "#" + obsLoc +")");                
            
                //associate the AVG or individual value with the Observation instant
                //newObservedValue - > ssn:observationResult -> soN
                _r.eval("(make-instance " + coreOntoStringIRI + "#so" + n +" of "+ ssnOntoStringIRI + "#SensorOutput map)");
                _r.eval("(slot-insert$ " + regulOntoStringIRI + "#o" + WQnameShrt + n + " " + ssnOntoStringIRI + "#observationResult 1 "+ coreOntoStringIRI + "#so" + n +")");
                //soN - > ssn:hasValue - > ovN
                _r.eval("(make-instance " + coreOntoStringIRI + "#ov" + n +" of "+ ssnOntoStringIRI + "#ObservationValue map)");
                _r.eval("(slot-insert$ " + coreOntoStringIRI + "#so" + n + " " + ssnOntoStringIRI + "#hasValue 1 "+ coreOntoStringIRI + "#ov" + n +")");
                //ovN - > dul:hasDataValue - > WQAveageDV
                _r.eval("(slot-insert$ " + coreOntoStringIRI + "#ov" + n + " " + dulOntoStringIRI + "#hasDataValue 1 " + truncatedAVGval +")");
                
                //create new Time Instant individual
                _r.eval("(make-instance " + regulOntoStringIRI + "#TmInst" + WQnameShrt + n +" of " + timeOntoStringIRI + "#Instant map)");
            
                //newInstantValue -> inXSDDateTime -> current time
                _r.eval("(slot-insert$ " + regulOntoStringIRI + "#TmInst" + WQnameShrt + n + " " + timeOntoStringIRI + "#inXSDDateTime 1 ((new Date) toString))");
            
                //newObservedValue -> observationResultTime -> newInstantValue
                _r.eval("(slot-insert$ " + regulOntoStringIRI + "#o" + WQnameShrt + n + " " + ssnOntoStringIRI + "#observationResultTime 1 " + regulOntoStringIRI + "#TmInst"+ WQnameShrt + n +" )");
            
                                
                //print out StreamJess RESULTS
                //start time
                StopWatch stopWatch = new StopWatch();
                try {
                    //RUN Jess engine
                    //stopWatch.start();
                    _r.run();
                    //stopWatch.stop();
                    //System.out.println("LATENCY:" + stopWatch.getTotalTimeMillis());
                    //totProcTime = totProcTime + stopWatch.getTotalTimeMillis();
                } catch (JessException ex) {
                    Logger.getLogger(RDFResultsFormatter.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (JessException ex) {
                Logger.getLogger(RDFResultsFormatter.class.getName()).log(Level.SEVERE, null, ex);
            }
            
	} 
        //save KB changes back to ontology
//        try {
//            _r.eval("(save-project " + inwsOntologies +")");
//        } catch (JessException ex) {
//            Logger.getLogger(RDFResultsFormatter.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        //System.out.println("TOTAL PROC.TIME: " + totProcTime);
    }
    
    private double randomWithRange(double min, double max)
    {
       double range = (max - min) + 1;     
       return (double)(Math.random() * range) + min;
    }
}
