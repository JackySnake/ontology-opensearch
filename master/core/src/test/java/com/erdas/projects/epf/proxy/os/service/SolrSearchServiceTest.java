package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.TestConstant;
import com.erdas.projects.epf.proxy.os.model.*;
import junit.framework.TestCase;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

public class SolrSearchServiceTest extends TestCase {

    public void testIndexGEMET() throws Exception {
        IndexerService service = new SolrSearchService();
        service.index("GEMET");
    }

    public void testIndexOTEG() throws Exception {
        IndexerService service = new SolrSearchService();
        service.index("OTEG");
    }

    public void testIndexMULTIDOMAIN() throws Exception {
        IndexerService service = new SolrSearchService();
        service.index("MULTIDOMAIN");
    }

    public void testSearchConcept() throws Exception {
        OpenSearchService service = new SolrSearchService();
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
