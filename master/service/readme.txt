Readme
------

Compilation
-----------
mvn package to build the war file.

Deployment
----------
Copy the war file in your tomcat webapps folder.

You also need to configure a SOLR server. See the SOLR doc for more information.

The SOLR document must contains the following fields
- id
- name
- name_fr
- cat : containing the ontology ID
- description
- description_fr
- broader
- narrower
- exactMatch
- relatedMatch
- closeMatch

You need to configure the SOLR schema to index these fields. You can take inspiration from the file src/main/dist/schema.xml 

Configuration
-------------
The src/main/resources/config.properties contains two properties :

solr.url = the URL of the SOLR server
void.url = the URL of the VOID configuration file

The src/main/resources/void.rdf contains the ontology configuration:

For each ontology, you need to configure one or several dataDump pointing to the RDF to index. The dataDump value
must be a URL accessible from the server.

For instance for GEMET, here is the data dumps configuration

<void:dataDump rdf:resource="http://www.eionet.europa.eu/gemet/gemet-skoscore.rdf"/>
<void:dataDump rdf:resource="http://www.eionet.europa.eu/gemet/gemet-definitions.rdf?langcode=en"/>
<void:dataDump rdf:resource="http://www.eionet.europa.eu/gemet/gemet-definitions.rdf?langcode=fr"/>
