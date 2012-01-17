package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.exceptions.OpenSearchException;
import com.erdas.projects.epf.proxy.os.model.SearchQuery;
import com.erdas.projects.epf.proxy.os.model.SearchResponse;
import com.erdas.projects.epf.proxy.os.model.SearchResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.erdas.projects.epf.proxy.os.model.reader.SparqlResponseParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import javax.xml.xpath.XPathExpressionException;

/**
 * @author fabian.skivee@erdas.com
 */
public class OntologySearchService implements OpenSearchService {

    private String sparqlHostUrl;

    public OntologySearchService(String sparqlHostUrl) {
        this.sparqlHostUrl = sparqlHostUrl;
    }

    public SearchResponse search(SearchQuery query) throws OpenSearchException {
        String sparqlQuery = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> " +
                "SELECT DISTINCT ?id ?label ?definition " +
                "WHERE {" +
                "?id skos:prefLabel ?label ; " +
                "FILTER langMatches( lang(?label), '" + query.getLanguage() + "' ) . " +
                "OPTIONAL {?id skos:definition ?definition ; " +
                "FILTER langMatches( lang(?definition), '" + query.getLanguage()  + "' )} . " +
                "FILTER (regex(str(?label), '" + query.getSearchTerm()  + "','i')) " +
                "} LIMIT " + query.getMaxResults();

        List<NameValuePair> qparams = new ArrayList<NameValuePair>();

        qparams.add(new BasicNameValuePair("queryLn", "SPARQL"));
        qparams.add(new BasicNameValuePair("query", sparqlQuery));


        URI uri = null;
        try {
            uri = URIUtils.createURI("http",sparqlHostUrl, -1, "/openrdf-sesame/repositories/SMAAD",
                    URLEncodedUtils.format(qparams, "UTF-8"), null);
        } catch (URISyntaxException e) {
            throw new OpenSearchException("Cannot access SPARQL service",e);
        }

        HttpGet httpget = new HttpGet(uri);
        httpget.addHeader("content-type","application/x-www-form-urlencoded");
        httpget.addHeader("accept","application/sparql-results+xml");

        System.out.println(httpget.getURI());

        HttpClient httpclient = new DefaultHttpClient();

        HttpResponse sparqlResponse = null;
        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        List<SearchResult> concepts = new ArrayList<SearchResult>();
        try {
            sparqlResponse = httpclient.execute(httpget);

            HttpEntity entity = sparqlResponse.getEntity();
            if (entity != null) {
                InputStream instream = null;
                instream = entity.getContent();
                SparqlResponseParser parser = new SparqlResponseParser();
                concepts = parser.parse(instream);
                instream.close();
            }
        } catch (IOException e) {
            throw new OpenSearchException("Error from the SPARQL service",e);
        } catch (XPathExpressionException e) {
            throw new OpenSearchException("Error during SPARQL response parsing",e);
        }
        response.setSearchResults(concepts);

        return response;
    }
}
