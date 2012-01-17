////////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2011 ERDAS Inc.
//
////////////////////////////////////////////////////////////////////////////////
package com.erdas.projects.epf.proxy.os.model;

import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.entity.OSQuery;
import com.sun.syndication.feed.module.opensearch.impl.OpenSearchModuleImpl;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author fabian.skivee@erdas.com
 */
public class AtomModelWriter implements ModelWriter {

    public void write(Response response, PrintWriter writer) throws Exception {
        if (response instanceof SearchResponse) {
        try {

            SearchResponse searchResponse = (SearchResponse) response;

            Collection<SearchResult> concepts = searchResponse.getSearchResults();

            String feedType = "atom_1.0";
            String fileName = "feed.xml";

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType(feedType);
            feed.setTitle("Ontology search");
            feed.setLink("http://www.erdas.com");
            feed.setAuthor("ERDAS EMEA Service team");
            // Add the opensearch module, you would get information like totalResults from the
            // return results of your search
            List mods = feed.getModules();
            OpenSearchModule osm = new OpenSearchModuleImpl();
            osm.setItemsPerPage(searchResponse.getQuery().getMaxResults());
            osm.setStartIndex((int) searchResponse.getStartIndex()+1);
            osm.setTotalResults((int) searchResponse.getTotalResults());

            OSQuery query = new OSQuery();
            query.setRole("request");
            query.setSearchTerms(searchResponse.getQuery().getSearchTerm());
            query.setStartPage(searchResponse.getQuery().getStartIndex());
            osm.addQuery(query);

            Link link = new Link();
            link.setHref(searchResponse.getQuery().getContextURL() +  "/opensearch.jsp");
            link.setType("application/opensearchdescription+xml");
            osm.setLink(link);

            mods.add(osm);
            feed.setModules(mods);
            
            List entries = new ArrayList();
            SyndEntry entry;
            SyndContent description;
            for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
                SearchResult searchResult = (SearchResult) iterator.next();

                entry = new SyndEntryImpl();
                entry.setUri(searchResult.getId());
                entry.setTitle(searchResult.getTitle(searchResponse.getQuery().getLanguage()));
                entry.setLink(searchResult.getId());
                description = new SyndContentImpl();
                description.setType("text/plain");
                description.setValue(searchResult.getDescription(searchResponse.getQuery().getLanguage()));
                entry.setDescription(description);
                entries.add(entry);
            }
            feed.setEntries(entries);

            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,writer);
            writer.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        }
    }

}