<html>
<head>
    <title>Opensearch request samples</title>
    <link rel="search"
          type="application/opensearchdescription+xml"
          href="opensearch.jsp"
          title="SMAAD Search engine"/>
</head>
<body>
<%
    String contextURL = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>

<h1>Opensearch description documents</h1>

Default Opensearch description : <a href="<%= contextURL %>/opensearch.jsp"><%= contextURL %>/opensearch.jsp</a>
<p/>
GEMET Opensearch description : <a href="<%= contextURL %>/opensearch.jsp?Ontology=GEMET"><%= contextURL %>/opensearch.jsp?Ontology=GEMET</a>
<p/>
OTEG Opensearch description : <a href="<%= contextURL %>/opensearch.jsp?Ontology=OTEG"><%= contextURL %>/opensearch.jsp?Ontology=OTEG</a>
<p/>
MULTIDOMAIN Opensearch description : <a href="<%= contextURL %>/opensearch.jsp?Ontology=MULTIDOMAIN"><%= contextURL %>/opensearch.jsp?Ontology=MULTIDOMAIN</a>
<p/>
ALL Thesaurus Opensearch description : <a href="<%= contextURL %>/opensearch.jsp?Ontology=ALL"><%= contextURL %>/opensearch.jsp?Ontology=ALL</a>
<p/>

<h1>Search request samples</h1>

Search for "country": <a href="<%= contextURL %>/search/GEMET?q=country"><%= contextURL %>/search/GEMET?q=country</a>
<p/>
Search for "pays" in french: <a href="<%= contextURL %>/search/GEMET?q=pays&amp;lang=fr"><%= contextURL %>/search/GEMET?q=pays&amp;lang=fr</a>
<p/>
Search for "country" with maximum 12 results : <a href="<%= contextURL %>/search/GEMET?q=country&amp;count=12"><%= contextURL %>/search/GEMET?q=country&amp;count=12</a>
<p/>
Search for "country" with maximum 12 results and start at index 3 : <a href="<%= contextURL %>/search/GEMET?q=country&amp;count=12&amp;startIndex=3"><%= contextURL %>/search/GEMET?q=country&amp;count=12&amp;startIndex=3</a>
<p/>
Search for "country" with RDF output format  : <a href="<%= contextURL %>/search/GEMET?q=country&amp;format=rdf"><%= contextURL %>/search/GEMET?q=country&amp;format=rdf</a>
<p/>
Search for "country" with JSON output format  : <a href="<%= contextURL %>/search/GEMET?q=country&amp;format=json"><%= contextURL %>/search/GEMET?q=country&amp;format=json</a>

<h1>Index request samples</h1>

Index GEMET ontology : <a href="<%= contextURL %>/index/GEMET"><%= contextURL %>/index/GEMET</a>
<p/>
Index OTEG ontology : <a href="<%= contextURL %>/index/OTEG"><%= contextURL %>/index/OTEG</a>
<p/>
Index MULTIDOMAIN ontology : <a href="<%= contextURL %>/index/MULTIDOMAIN"><%= contextURL %>/index/MULTIDOMAIN</a>

</body>
</html>