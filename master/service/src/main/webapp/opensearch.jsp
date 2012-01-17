<?xml version="1.0" encoding="UTF-8"?>
<%@ page import="org.w3c.dom.Document" %>
<%@ page import="com.erdas.projects.epf.proxy.os.util.ResourceLoader" %>
<%@ page import="com.erdas.projects.epf.proxy.os.util.VoidDoc" %>
<%@ page import="java.util.Properties" %>
<%@ page import="com.erdas.projects.epf.proxy.os.util.PropertyLoader" %>
<%@ page import="java.util.List" %>
<%
    String contextURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

    response.setContentType("application/xml; charset=UTF-8");
    response.setHeader("Cache-Control", "no-cache");

    String value = request.getParameter("Ontology");

    Properties properties = PropertyLoader.loadProperties("config.properties");
    String voidURL = properties.getProperty("void.url");
    Document document = ResourceLoader.parseResourceAsDocument(voidURL);
    VoidDoc voidDoc = VoidDoc.parse(document);

    String ontologyId = "GEMET";
    if (value != null) {
        if (value.equals("ALL")) {
            ontologyId = "";
        } else {
            List<VoidDoc.Dataset> datasetList = voidDoc.getDataSets();
            for (int i = 0; i < datasetList.size(); i++) {
                VoidDoc.Dataset dataset =  datasetList.get(i);
                if (value.equalsIgnoreCase(dataset.getTitle())) {
                    ontologyId = value;
                }
            }
        }
    }
%>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/"
                       xmlns:time="http://a9.com/-/opensearch/extensions/time/1.0/">

    <ShortName>SMAAD Search Engine</ShortName>
    <LongName>SMAAD Search Engine</LongName>
    <Description>Opensearch access point to the SMAAD Ontology Server</Description>
    <Tags>catalog SMAAD erdas gis service geospatial</Tags>

    <!-- Auto-update link -->
    <Url type="application/opensearchdescription+xml"
         rel="self"
         template="<%= contextURL %>/opensearch.jsp?Ontology=<%= ontologyId %>" />

    <!-- OpenSearch endpoints -->
    <Url type="text/html"
         template="<%= contextURL %>/search/<%= ontologyId %>?q={searchTerms}&amp;lang={language}&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/json"
         template="<%= contextURL %>/search/<%= ontologyId %>?q={searchTerms}&amp;lang={language}&amp;format=json&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/rdf+xml"
         template="<%= contextURL %>/search/<%= ontologyId %>?q={searchTerms}&amp;lang={language}&amp;format=rdf&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/atom+xml"
         template="<%= contextURL %>/search/<%= ontologyId %>?q={searchTerms}&amp;lang={language}&amp;format=atom&amp;count={count}&amp;startIndex={startIndex}"/>

    <Image height="64" width="64" type="image/png"><%= contextURL %>/img/erdas-alpha.png</Image>
    <Image height="16" width="16" type="image/x-icon"><%= contextURL %>/img/erdas-alpha.ico</Image>

    <Query role="example" searchTerms="land"/>
    <Developer>Erdas EMEA Service Team</Developer>
    <Attribution>Erdas Copyright 2011, erdas.com</Attribution>
    <SyndicationRight>open</SyndicationRight>
    <AdultContent>false</AdultContent>
    <Language>en</Language>
    <Language>fr</Language>
    <OutputEncoding>UTF-8</OutputEncoding>
    <InputEncoding>UTF-8</InputEncoding>
</OpenSearchDescription>