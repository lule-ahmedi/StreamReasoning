/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import eu.larkc.csparql.cep.api.RdfStream;
import eu.larkc.csparql.core.engine.ConsoleFormatter;
import eu.larkc.csparql.core.engine.CsparqlEngine;
import eu.larkc.csparql.core.engine.CsparqlEngineImpl;
import eu.larkc.csparql.core.engine.CsparqlQueryResultProxy;
import eu.larkc.csparql.core.engine.RDFStreamFormatter;
import static main.StreamJess.initiateJessEnv;
import static main.StreamJess.inwsOntologies;
import static main.StreamJess.inwsRules;
import eu.larkc.csparql.readytogopack.streamer.BasicIntegerRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.BasicRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.CloudMonitoringRDFStreamTestGenerator;
import eu.larkc.csparql.readytogopack.streamer.DoorsTestStreamGenerator;
import eu.larkc.csparql.readytogopack.streamer.LBSMARDFStreamTestGenerator;
import java.text.ParseException;
import java.util.Random;
import java.util.logging.Level;
import javax.swing.JScrollPane;
import jess.JessException;
import jess.Rete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.liu.ida.JessTab.JTextAreaWriter;
import se.liu.ida.JessTab.JessTab;
import se.liu.ida.JessTab.JessTabFunctions;
import streamer.INWSRDFStreamTestGenerator;

/**
 *
 * @author Edi
 */
public class UI extends javax.swing.JFrame {

    private Logger logger = LoggerFactory.getLogger(UI.class);

    //set file paths
    //path to Ontology modules i.e. Protege project including all ontologies
    //public static String inwsOntologies = "\"D:\\\\Studime\\\\SEEU-PhD\\\\PAPERS\\\\4. MTSR2015 - Mancester\\\\REJECTED PAPERS\\\\ISWC 2015 - Ontology and DataSet\\\\Ontologies\\\\INWS-FULL.pprj\""; 
    public String inwsOntologies = "\"D:\\\\Personal\\\\Desktop\\\\ontologji funksionale per streamJess\\\\inws-all3.pprj\""; 

    //path to Jess rules
    public String inwsRules = "D:\\Personal\\Documents\\NetBeansProjects\\StreamJess\\src\\jessRules\\";

    public String initiateJessEnv = inwsRules + "initiateINWSrules.jess";
    
    private final Rete r = new Rete();
    Random randomGenerator = new Random();
    int randomInt= randomGenerator.nextInt(10000);
                
                /**
     * Creates new form UI
     */
    public UI() throws JessException {
        initComponents();
        //INWSJess initiation
                //final Rete r = new Rete();
                
                   //int randomInt = 19;
                //import JessTab functions
                r.addUserpackage(new JessTabFunctions());

                //load the INWS ontology
                r.eval("(load-project " + inwsOntologies +")");

                //obtain the Protégé project
                JessTab.getProtegeKB().getProject();
                
                //redirect Jess console messages into the text area
                JScrollPane js = new JScrollPane();
                JTextAreaWriter taw = new JTextAreaWriter(outputTxt, js);
                r.addOutputRouter("t", taw);

                //initiate INWS Jess constructs
                r.batch(initiateJessEnv); 
                r.run();

                //create new interval instance in ontology
                //r.eval("(make-instance http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#ObservationInterval_" + randomInt +" of http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl#ObservationInterval map)");
                //r.eval("(make-instance (str-cat \"begObservationInstant\""+ randomInt +") of http://inwatersense.uni-pr.edu/ontologies/inws-regulations.owl#ObservationInstant map)");
                //r.eval("(slot-insert$ (str-cat \"http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#begObservationInstant\""+ randomInt +") http://www.w3.org/2006/time#inXSDDateTime 1 ((new Date) toString))");
                //create a Jess fact representing the new interval
                //Value v = r.eval("(assert(CurrentInterval (v ObservationInterval_"+ randomInt + ")))");
                //link interval beginning and end date
                //r.batch(inwsRules + "setIntervalBegEnd.jess");

                r.run();

                //inwsjesssense rules
                r.batch(inwsRules + "WFDmonitorBOD.jess");
                //r.batch(inwsRules + "WFDmonitorWaterHardness.jess");
                r.batch(inwsRules + "findWFDBODpollutants.jess");         

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        outputTxt = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        wfdRadioBtn = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        parametersLs = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        classifyParams = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        outputTxt.setColumns(20);
        outputTxt.setRows(5);
        jScrollPane2.setViewportView(outputTxt);

        jLabel5.setText("Output");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("- StreamJess -");

        jLabel2.setText("1. Regulation");

        wfdRadioBtn.setText("WFD");

        jLabel3.setText("2. Water Quality");

        parametersLs.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "pH", "Temperature", "Total Ammonia", "Water Hardness", "BOD", "MRP", "Arsenic", "Phytobenthos", "Chromium III", "Chromium VI", "Diazinon", "Dimethoate", "Linuron", "Mancozeb", "Phenol", "Cyanide", "Fluoride", "Glyphosate", "Monochlorobenzene", "Toluene", "Xylenes", "Zinc", "Benthic Invertebrate Fauna", "Copper" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(parametersLs);

        jLabel4.setText("3. Action");

        classifyParams.setText("Start");
        classifyParams.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                classifyParamsMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel6.setText("le te shifet dallimi me console");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(203, 203, 203)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)))
                .addContainerGap(213, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(61, 61, 61)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addComponent(wfdRadioBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(47, 47, 47)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addGap(37, 37, 37)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel4)
                        .addComponent(classifyParams))
                    .addContainerGap(110, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(125, 125, 125)
                .addComponent(jLabel5)
                .addContainerGap(187, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(215, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(88, 88, 88)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(wfdRadioBtn)
                        .addComponent(classifyParams)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(191, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void classifyParamsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_classifyParamsMouseClicked
                
		// examples of streams and queries

		final int WHO_LIKES_WHAT = 0;
		final int HOW_MANY_USERS_LIKE_THE_SAME_OBJ = 1;
		final int MULTI_STREAM = 2;
		final int FIND_OPINION_MAKERS = 3;
		final int STREAMING_AND_EXTERNAL_STATIC_RDF_GRAPH = 4;
		final int DOOR_TEST = 5;
		final int CLOUD_MONITORING_TEST = 6;
		final int COMPOSABILITY = 7;
		final int PERCENTILE = 8;
                //INWSJess
                final int OBSERVATIONS_AVERAGE_GROUPBY_WQ = 10;

		// put here the example you want to run

		int key = OBSERVATIONS_AVERAGE_GROUPBY_WQ;

		// initializations

		//		String streamURI = "http://myexample.org/stream";
		String query = null;
		String queryDownStream = null;
		RdfStream tg = null;
		RdfStream anotherTg = null;

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

		switch (key) {

                    case OBSERVATIONS_AVERAGE_GROUPBY_WQ:
                        logger.debug("COUNT_OBSERVATIONS example");
			query = "REGISTER QUERY CopperObservations AS "
					+ "PREFIX inws: <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl#> "
                                        + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                                        + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> "
					+ "SELECT ?qo ?loc (AVG(?dv) AS ?avg) "
                                        //+ "(COUNT(?dv) AS ?num)"
					+ "FROM STREAM <http://inwatersense.uni-pr.edu/stream> [RANGE 20s STEP 20s] "
                                        //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/datasetFINAL.owl> "
                                        //+ "FROM <http://inwatersense.uni-pr.edu/ontologies/inws-core.owl> "
					+ "WHERE { "
                                        + "?o ssn:qualityOfObservation ?qo . "
                                        + "?o ssn:observationResult ?r . "
                                        + "?r ssn:hasValue ?v . "
                                        + "?v dul:hasDataValue ?dv . "
                                        + "?o inws:observationResultLocation ?loc . "
                                        + "} "
                                        + "GROUP BY ?qo ?loc";
			tg = new INWSRDFStreamTestGenerator("http://inwatersense.uni-pr.edu/stream");
			break;
		default:
			System.exit(0);
			break;
		}

		// Register an RDF Stream

		engine.registerStream(tg);

		// Start Streaming (this is only needed for the example, normally streams are external
		// C-SPARQL Engine users are supposed to write their own adapters to create RDF streams

		final Thread t = new Thread((Runnable) tg);
		t.start();
		if (anotherTg != null) {
			engine.registerStream(anotherTg);
			if (key != COMPOSABILITY) {
				final Thread t2 = new Thread((Runnable) anotherTg);
				t2.start();
			}
		}

		// Register a C-SPARQL query

		CsparqlQueryResultProxy c1 = null;
		CsparqlQueryResultProxy c2 = null;

		if (key != COMPOSABILITY) {

			try {
				c1 = engine.registerQuery(query, false);
				logger.debug("Query: {}", query);
				logger.debug("Query Start Time : {}", System.currentTimeMillis());
			} catch (final ParseException ex) {
				logger.error(ex.getMessage(), ex);
			}

			// Attach a Result Formatter to the query result proxy

			if (c1 != null) {
				//c1.addObserver(new ConsoleFormatter());
                            //dergoi variablat e nevojshme    
                            c1.addObserver(new RDFResultsFormatter(r));
			}

		} else {
			try {
				c1 = engine.registerQuery(query, false);
				logger.debug("Query: {}", query);
				logger.debug("Query Start Time : {}", System.currentTimeMillis());
			} catch (final ParseException ex) {
				logger.error(ex.getMessage(), ex);
			}

			// Attach a Result Formatter to the query result proxy

			if (c1 != null) {
				c1.addObserver((RDFStreamFormatter) anotherTg);

				try {
					c2 = engine.registerQuery(queryDownStream, false);
					logger.debug("Query: {}", query);
					logger.debug("Query Start Time : {}", System.currentTimeMillis());
				} catch (final ParseException ex) {
					logger.error(ex.getMessage(), ex);
				}

				if (c2 != null) {
					c2.addObserver(new ConsoleFormatter());

				}

			}
		}

		// leave the system running for a while
		// normally the C-SPARQL Engine should be left running
		// the following code shows how to stop the C-SPARQL Engine gracefully
		try {
			Thread.sleep(200000);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}

		if (key != COMPOSABILITY) {
			// clean up (i.e., unregister query and stream)
			engine.unregisterQuery(c1.getId());

			((LBSMARDFStreamTestGenerator) tg).pleaseStop();

			engine.unregisterStream(tg.getIRI());

			if (anotherTg != null) {
				engine.unregisterStream(anotherTg.getIRI());
			}
		} else {
			// clean up (i.e., unregister query and stream) 
			engine.unregisterQuery(c1.getId());
			engine.unregisterQuery(c2.getId());

			((LBSMARDFStreamTestGenerator) tg).pleaseStop();

			engine.unregisterStream(tg.getIRI());
			engine.unregisterStream(anotherTg.getIRI());
		}

		System.exit(0);
    }//GEN-LAST:event_classifyParamsMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new UI().setVisible(true);
                } catch (JessException ex) {
                    java.util.logging.Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton classifyParams;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextArea outputTxt;
    private javax.swing.JList parametersLs;
    private javax.swing.JRadioButton wfdRadioBtn;
    // End of variables declaration//GEN-END:variables
}
