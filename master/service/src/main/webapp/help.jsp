<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title></title>
    <link rel="stylesheet" href="css/style.css" type="text/css" media="screen,projection"/>
</head>
<body>
<%
    String contextURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>

<h1>Service interface documentation</h1>

<p>
    This page documents the search service interface. The search service allows to search for concepts
    in an ontology. The ontology must be provided in the SKOS format. The service is described by an
    OpenSearch description document. This description allows OpenSearch Client to connect to the search
    service.
</p>

The service supports:
<ul>
    <li>Search by term</li>
    <li>Search on a specific ontology or all ontology</li>
    <li>Search in a given language</li>
    <li>Pagination</li>
    <li>OutputFormat : ATOM, RDF, JSON</li>
    <li>ReIndex a configured ontology</li>
</ul>

<h2>Opensearch description document</h2>

<p>
    OpenSearch is a collection of technologies that allow publishing of search results in a format suitable
    for syndication and aggregation. It is a way for websites and search engines to publish search results
    in a standard and accessible format. OpenSearch was developed by Amazon.com subsidiary A9.
</p>

<p>
    OpenSearch specification is available at <a href="http://www.opensearch.org">http://www.opensearch.org</a>
</p>
<p>
    An OpenSearch description document can be used to describe the web interface of a search engine.
    Default Opensearch description is available at <a href="<%= contextURL %>/opensearch.jsp"><%= contextURL %>/opensearch.jsp</a>
</p>

<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/"
                       xmlns:time="http://a9.com/-/opensearch/extensions/time/1.0/">

    <ShortName>SMAAD Search Engine</ShortName>
    <LongName>SMAAD Search Engine</LongName>
    <Description>Opensearch access point to the SMAAD Ontology Server</Description>

    <Tags>catalog SMAAD erdas gis service geospatial</Tags>

    <!-- Auto-update link -->
    <Url type="application/opensearchdescription+xml"
         rel="self"
         template="http://localhost:8181/epf-proxy/opensearch.jsp?Ontology=GEMET" />

    <!-- OpenSearch endpoints -->
    <Url type="text/html"
         template="http://localhost:8181/epf-proxy/search/GEMET?q={searchTerms}&amp;lang={language}&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/json"
         template="http://localhost:8181/epf-proxy/search/GEMET?q={searchTerms}&amp;lang={language}&amp;format=json&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/rdf+xml"
         template="http://localhost:8181/epf-proxy/search/GEMET?q={searchTerms}&amp;lang={language}&amp;format=rdf&amp;count={count}&amp;startIndex={startIndex}"/>

    <Url type="application/atom+xml"
         template="http://localhost:8181/epf-proxy/search/GEMET?q={searchTerms}&amp;lang={language}&amp;format=atom&amp;count={count}&amp;startIndex={startIndex}"/>

    <Image height="64" width="64" type="image/png">http://localhost:8181/epf-proxy/img/erdas-alpha.png</Image>
    <Image height="16" width="16" type="image/x-icon">http://localhost:8181/epf-proxy/img/erdas-alpha.ico</Image>

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
<p>
    Each ontology exposes a different OpenSearch description. This OpenSearch description can be retreive
    by adding the "Ontology" parameter.</p>

<p>
    For instance, the GEMET Opensearch description is available at <a href="<%= contextURL %>/opensearch.jsp?Ontology=GEMET"><%= contextURL %>/opensearch.jsp?Ontology=GEMET</a>
</p>

<h2>Search interface</h2>

The search interface supports the following parameters

<table>
<thead>
<tr>
    <th></th>
</tr>
</thead>
    <tr>
        <td></td>
    q
    lang
    format
    count
    startIndex
</p>


<h2>Index interface</h2>

<h1>Service configuration documentation</h1>

<h2>Architecture</h2>

<h2>Service configuration</h2>

</body>
</html>