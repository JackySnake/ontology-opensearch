package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.model.*;
import junit.framework.TestCase;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author fabian.skivee@erdas.com
 */
public class OntologySearchServiceTest extends TestCase {

    public void testSearchConcept() throws Exception {
        OntologySearchService service = new OntologySearchService("lge-sgi-ans:8280");
        SearchQuery query = new SearchQuery();
        query.setSearchTerm("land");

        SearchResponse searchResponse = service.search(query);
        //displayConcepts(concepts);

        ModelWriter writer = new JSONModelWriter();
        PrintWriter printWriter = new PrintWriter(System.out,true);
        writer.write(searchResponse,printWriter);
        printWriter.close();


    }

    private void displayConcepts(Collection<SearchResult> concepts) {
        System.out.println("Display " + concepts.size() + " results");
        for (Iterator<SearchResult> iterator = concepts.iterator(); iterator.hasNext();) {
            SearchResult concept =  iterator.next();
            System.out.println(concept);
        }
    }


}