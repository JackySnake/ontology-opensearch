<%@ page import="java.util.List" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.erdas.projects.epf.proxy.os.service.OpenSearchService"%>
<%@ page import="com.erdas.projects.epf.proxy.os.service.SolrSearchService"%>
<%@ page import="com.erdas.projects.epf.proxy.os.model.*"%>
<%@ page import="java.util.Properties" %>
<%@ page import="com.erdas.projects.epf.proxy.os.util.PropertyLoader" %>
<html>
<head>
    <title>Semantic annotation Tool</title>
    <link rel="search"
          type="application/opensearchdescription+xml"
          href="opensearch.jsp"
          title="SMAAD Search engine"/>
</head>
<body>
<style type="text/css">
    table {
        border-collapse:collapse;
        width:90%;
    }
    th, td {
        border:1px solid black;
        padding:3px;
    }
    td {
        text-align:center;
    }
    caption {
        font-weight:bold
    }
</style>

<%!
    OpenSearchService  service = null;
%>

<%
    String searchItem = request.getParameter("searchItem");
    
    if (service == null) {
        service = new SolrSearchService();
    }
%>


<form action="index.jsp"  method=post>
    Search: <input type="test" name="searchItem"/>
    <input type="submit" value="Search">
</form>

<%
        if ((searchItem != null) && (searchItem.trim().length() != 0)) {
        /*
            List<Concept> concepts = indexer.search(searchItem);
         */
         SearchQuery query = new SearchQuery();
         query.setSearchTerm(searchItem);

         SearchResponse searchResponse=service.search(query);

         Collection<SearchResult> concepts = searchResponse.getSearchResults();
    %>

Search for : <%= searchItem %><br/>

Number of matching results : <%= searchResponse.getTotalResults() %><br/>

<table>
     <caption>Results<caption>
 <tr>
  <th>#</th>
  <th>URI</th>
  <th>Title</th>
  <th>Description</th>
 </tr>
    
<%
int i = 0;
         for(Iterator<SearchResult> iterator = concepts.iterator(); iterator.hasNext(); ) {
           SearchResult concept = iterator.next();
    %>

    <tr>
        <td><%= i%></td>
        <td><%= concept.getId() %></td>
        <td><%= concept.getTitle() %></td>
        <td><%= concept.getDescription() %></td>
    </tr>
    <%
    i++;
            }
    %>
</table>
<% } %>

<a href="example.jsp">Examples</a>

</body>
</html>