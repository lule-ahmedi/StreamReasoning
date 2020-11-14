# Stream Reasoning systems: C-SWRL and StreamJess
<p>
    <ul>
      <li>C-SWRL and StreamJess are Stream Reasoning systems, extending <a href="http://streamreasoning.org/resources/c-sparql">C-SPARQL</a> with non-monotonic capabilities. C-SWRL is a unique Semantic Web system for reasoning over stream data, while StreamJess is a <a href="http://www.jessrules.com/">Jess</a> system capable of expressive reasoning over stream data.
      <li>
        Systems are written in Java 1.8. The &quot;ready to go packs&quot; are NetBeans projects.
      </li>
      </li>
    </ul>
</p>
<p>For a more detailed description of the StreamReasoning project, please visit http://streamreasoning.uni-pr.edu/.</p>
<hr/>

<h1><a name="usC">Using C-SWRL</a></h1>
       <p>
            To start using C-SWRL the following steps need to be performed:
       </p>
       <ol>
            <li>Download and unzip files into your local folder</li>
            <li>Import the project into your NetBeans</li>
            <li>Download the InWaterSense ontologies from the <a href="https://github.com/lule-ahmedi/InWaterSense">InWaterSense Repository</a> on Github.</li>
            <li>Open main\CSWRL.java and replace the InWaterSense ontologies path with your local copies ones</li>
            <li>Download and import the jar libraries into your project: 
                <ul>
                    <li><a href="http://streamreasoning.org/resources/c-sparql">C-SPARQL</a> v0.9.6</li>
                    <li><a href="http://owlapi.sourceforge.net/">OWL API</a> v4.0.2</li>
                    <li><a href="https://github.com/protegeproject/swrltab">SWRLTab</a> v1.0</li>
                    <li><a href="https://github.com/protegeproject/swrlapi-drools-engine">SWRL API Drools Engine</a> v1.0 and</li>
                    <li><a href="http://junit.org/junit4/">JUnit </a> v4.10</li>
                </ul>
            </li>
            <li>Run the application</li>
        </ol>
        <p>
            &nbsp;
            Follow <a href="tutorials/CSWRL-guide.pdf">this</a> tutorial to get started with C-SWRL.</p>
        <hr />
        <h1><a name="usS">Using StreamJess</a></h1>
        <p>
            To start using StreamJess the following steps need to be performed:
        </p>
        <ol>
            <li>Download and unzip the files into your local folder</li>
            <li>Import the project into your NetBeans</li>
            <li>Download the InWaterSense ontology Protege project file <a href="http://inwatersense.uni-pr.edu/ontologies/inws-all3ontologies.owl">Link</a></li>
            <li>Open main\StreamJess.java and replace the InWaterSense ontology Protege project file path with your local copy of it</li>
            <li>Download and import the necessary jar libraries into your project: 
                <ul>
                    <li><a href="http://streamreasoning.org/resources/c-sparql">C-SPARQL</a> v0.9.6</li>
                    <li><a href="http://www.jessrules.com/">Jess</a> v7.1p2</li>
                    <li><a href="http://www.jessrules.com/jesswiki/view?JessTab">Jess Tab</a> v1.7 and</li>
                    <li><a href="http://protege.stanford.edu/">Protege</a> v3.5</li>
                </ul>
            </li>
            <li>Run the application</li>
        </ol>
        <p>A short video demonstration about the usage of StreamJess can be found <a href="http://inwatersense.uni-pr.edu/streamjess/demo.html">here</a>.</p>
        <p>
            Follow <a href="tutorials/StreamJess-guide.pdf">this</a> tutorial to get started with StreamJess.</p>
        <hr />

  <h1><a name="sup">Contributors and Contact</a></h1>
      <p>
	<ul>
		<li>Prof. Dr. Lule Ahmedi (Project Founder)</li>
		<li>Prof. Assoc. Figene Ahmedi</li>
		<li>Edmond Jajaga, PhD</li>
	</ul>
	<p>For any questions related to the systems, contact <a href="mailto:lule.ahmedi@uni-pr.edu?subject=StreamReasoning">Professor Lule Ahmedi</a></p>

<hr />
      <h1><a name="sup">Acknowledgements</a></h1>
        <p>
            This work was partially supported by the European project <a href="http://inwatersense.uni-pr.edu/">InWaterSense</a>.
        </p>
        <hr />
        <h1><a name="lit">Literature</a></h1>
        <ol>
		<li>Jajaga, E. and Ahmedi, L. 
                    <strong>C-SWRL: A Unique Semantic Web Framework for Reasoning Over Stream Data</strong>. 
                    International Journal of Semantic Computing 11(03):391-409, 2017. 
                <a href="https://www.worldscientific.com/doi/abs/10.1142/S1793351X17400165">Download</a></li>
            <li>Jajaga, E. and Ahmedi, L. 
                    <strong>C-SWRL: SWRL for Reasoning over Stream Data</strong>. 
                    2017 IEEE 11th International Conference on Semantic Computing (ICSC), San Diego, 2017. 
                <a href="https://ieeexplore.ieee.org/document/7889569/">Download</a></li>
            <li>Jajaga, E., Ahmedi, L. and Ahmedi, F. 
                    <strong>StreamJess: Stream Data Reasoning System for Water Quality Monitoring</strong>. 
                    International Journal of Metadata, Semantics and Ontologies, 2016. 
                <a href="https://www.inderscienceonline.com/doi/abs/10.1504/IJMSO.2016.083507">Download</a></li>
            <li>Jajaga, E., Ahmedi, L. and Ahmedi, F. <strong>StreamJess: Enabling Jess for Stream Data Reasoning and the Water Domain Case</strong>
                (Demo paper) 20th International Conference on Knowledge Engineering and Knowledge Management (EKAW2016), Bologna, 2016.
                <a href="http://luleahmedi.uni-pr.edu/docs/pubs/StreamJessDemoPaper2016.pdf">Download</a>
            </li>

  <li>Jajaga, E., Ahmedi, L. and Ahmedi, F. <strong>An Expert System for Water Quality Monitoring Based on Ontology</strong>, 
                  in Proc. of the 9th Metadata and Semantics Research Conference (MTSR2015), Manchester, 2015.
                <a href="http://luleahmedi.uni-pr.edu/docs/pubs/ExpertSys2015.pdf">Download</a>
            </li>

  <li>Ahmedi, L., Jajaga, E. and Ahmedi, F. <strong>An Ontology Framework for Water Quality Management</strong>,
              in Proc. of the 6th International Conference on Semantic Sensor Networks, Sydney, 2013.
                <a href="http://ceur-ws.org/Vol-1063/paper3.pdf">Download</a>
            </li>
  <li>Jajaga, E., Ahmedi, L. and Abazi-Bexheti, L.
                    <strong>Semantic Web Trends on Reasoning Over Sensor Data</strong>, 
                    in Proc. of the 8th South East European Doctoral Student Conference, Thessaloniki, 2013.
                <a href="https://www.researchgate.net/publication/255719059_Semantic_Web_Trends_on_Reasoning_Over_Sensor_Data">Download</a>
            </li>
        </ol>
    </div>
</body>
</html>
