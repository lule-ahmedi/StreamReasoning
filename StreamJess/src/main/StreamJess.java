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
 * This work was partially supported by the European project LarKC (FP7-215535) 
 * and by the European project MODAClouds (FP7-318484)
 ******************************************************************************/
package main;

import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import eu.larkc.csparql.readytogopack.streamer.LBSMARDFStreamTestGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import jess.JessException;
import jess.Rete;
import se.liu.ida.JessTab.JessTab;
import se.liu.ida.JessTab.JessTabFunctions;
import streamer.INWSRDFStreamTestGenerator;

public class StreamJess {

    private static Logger logger = LoggerFactory.getLogger(StreamJess.class);

    //set file paths
    //path to Ontology modules i.e. Protege project including all ontologies
    public static String inwsOntologies = "\"D:\\\\Personal\\\\Documents\\\\NetBeansProjects\\\\StreamJess\\\\src\\\\ontologies\\\\inws-all3.pprj\""; 

    //path to Jess rules
    public static String inwsRules = "D:\\Personal\\Documents\\NetBeansProjects\\StreamJess\\src\\jessRules\\";

    public static String initiateJessEnv = inwsRules + "initiateINWSrules.jess";


    public static void main(String[] args) throws IOException {             

        try {   
        
        //INWSJess initiation
        final Rete r = new Rete();

        //import JessTab functions
        r.addUserpackage(new JessTabFunctions());

        //load the INWS ontology
        r.eval("(load-project " + inwsOntologies +")");

        //obtain the Protégé project
        JessTab.getProtegeKB().getProject();

        //initiate INWS Jess constructs
        r.batch(initiateJessEnv); 
        r.run();

        //inwsjesssense rules
        r.batch(inwsRules + "WFDmonitorBOD.jess");
        r.batch(inwsRules + "WFDmonitorPH.jess");
        r.batch(inwsRules + "WFDmonitorArsenic.jess");
//        r.batch(inwsRules + "WFDmonitorBenthic.jess");
//        r.batch(inwsRules + "WFDmonitorCyanide.jess");
//        r.batch(inwsRules + "WFDmonitorChromiumIIIAA.jess");
//        r.batch(inwsRules + "WFDmonitorDiazinonAA.jess");
//        r.batch(inwsRules + "WFDmonitorDimethoateAA.jess");
//        r.batch(inwsRules + "WFDmonitorFluoride.jess");        
//        r.batch(inwsRules + "WFDmonitorGlyphosate.jess");      
//        r.batch(inwsRules + "WFDmonitorTotalAmmonia.jess");
        r.batch(inwsRules + "findWFDBODpollutants.jess");
        r.batch(inwsRules + "findWFDPHpollutants.jess");
        r.run();

        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(System.in));
        String queryAvgObs = null;String AVGwindowString = null; String INDwindowString = null; 
        String numTriples = null; String queryIndObs = null;
        String avgQwin = null; String indQwin = null;
        
        System.out.println("== Average value observations ==");        
        System.out.print("Use time-based (i) or tuple-based (u) windows:");
        String windowType = br.readLine();
        if(windowType.equals("u"))
        {
            numTriples = br.readLine();
            AVGwindowString = "[RANGE TRIPLES " + numTriples + "] ";
        } else 
        {
            System.out.print("Enter window size (s): ");
            avgQwin = br.readLine();
            AVGwindowString = "[RANGE "+ avgQwin +"s TUMBLING] ";
        }        
        
        System.out.println("== Individual value observations ==");        
        System.out.print("Use time-based (i) or tuple-based (u) windows:");
        windowType = br.readLine();
        if(windowType.equals("u"))
        {
            numTriples = br.readLine();
            INDwindowString = "[RANGE TRIPLES " + numTriples + "] ";
        } else 
        {
            System.out.print("Enter window size (s): ");
            indQwin = br.readLine();
            INDwindowString = "[RANGE "+ indQwin +"s TUMBLING] ";
        }
                        
        int NUM_STREAMS = 1;
        RdfStream tg[] = new RdfStream[NUM_STREAMS];

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
                        + "PREFIX inws: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> "
                        + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                        + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> "
                        + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "
                        + "SELECT ?qo ?loc (AVG(?dv) AS ?avg) "
                        + "FROM STREAM <http://inwatersense.uni-pr.edu/stream> " + AVGwindowString
                        + "WHERE { "
                        + "?o ssn:qualityOfObservation ?qo . "
                        + "?o ssn:observationResult ?r . "
                        + "?r ssn:hasValue ?v . "
                        + "?v dul:hasDataValue ?dv . "
                        + "?o inws:observationResultLocation ?loc "
                        + "FILTER (?qo != inws:pH)"
                        + "} "
                        + "GROUP BY ?qo ?loc ";
        

        queryIndObs = "REGISTER STREAM IndObservations  AS "
                        + "PREFIX inws: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> \n"
                        + "PREFIX inwsr: <http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl#> \n"
                        + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> \n"
                        + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> \n"
                        + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n"
                        + "SELECT ?qo ?loc ?dv \n"
                        + "FROM STREAM <http://inwatersense.uni-pr.edu/stream> "+ INDwindowString +" \n"
                        + "WHERE { \n"
                        + "?o ssn:qualityOfObservation ?qo . \n"
                        + "?o ssn:observationResult ?r . \n"
                        + "?r ssn:hasValue ?v . \n"
                        + "?v dul:hasDataValue ?dv . \n"
                        + "?o inws:observationResultLocation ?loc \n"
                        + "FILTER (?qo = inws:pH) \n"
                        + "} ";

            // Start Streaming (this is only needed for the example, normally streams are external
            // C-SPARQL Engine users are supposed to write their own adapters to create RDF streams

        for(int i=0;i<NUM_STREAMS;i++)
        {
            tg[i] = new INWSRDFStreamTestGenerator("http://inwatersense.uni-pr.edu/stream");
            engine.registerStream(tg[i]);
            final Thread t = new Thread((Runnable) tg[i]);
            t.start();
        }

        // Register a C-SPARQL query
        CsparqlQueryResultProxy c1 = null;
        CsparqlQueryResultProxy c2 = null;

        try {
            c1 = engine.registerQuery(queryAvgObs, false);

            logger.debug("Query: {}", queryAvgObs);
            logger.debug("Query Start Time : {}", System.currentTimeMillis());

            c2 = engine.registerQuery(queryIndObs, false);
            logger.debug("Query: {}", queryIndObs);
            logger.debug("Query Start Time : {}", System.currentTimeMillis());
        } catch (final ParseException ex) {
            logger.error(ex.getMessage(), ex);
        }

        // Attach a Result Formatter to the query result proxy
        if (c1 != null) {
            c1.addObserver(new RDFResultsFormatter(r));
        }
        if (c2 != null) {
            c2.addObserver(new RDFResultsFormatter(r));
        }

        // leave the system running for a while
        // normally the C-SPARQL Engine should be left running
        // the following code shows how to stop the C-SPARQL Engine gracefully
        // per ta ndal C-SPARQL
        try {
                Thread.sleep(200000);
        } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
        }

        //unregister queries
        engine.unregisterQuery(c1.getId());
        engine.unregisterQuery(c2.getId());

        //unregister streamers
        for(int i=0;i<NUM_STREAMS;i++)
        {
            tg[i] = new INWSRDFStreamTestGenerator("http://inwatersense.uni-pr.edu/stream");
            engine.unregisterStream(tg[i].getIRI());
        }
       
        System.exit(0);

       } catch (JessException ex) {
           java.util.logging.Logger.getLogger(StreamJess.class.getName()).log(Level.SEVERE, null, ex);
       } 
    }
}
