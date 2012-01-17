////////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2011 ERDAS Inc.
//
////////////////////////////////////////////////////////////////////////////////
package com.erdas.projects.epf.proxy.os.model;

import com.erdas.projects.epf.proxy.os.util.OpenSearchConstants;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author fabian.skivee@erdas.com
 */
public class RDFModelWriter implements ModelWriter {

    String RDF_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:opensearch=\"http://a9.com/-/spec/opensearch/1.1/\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:relevance=\"http://a9.com/-/opensearch/extensions/relevance/1.0/\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:void=\"http://rdfs.org/ns/void#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">\n";
    String RDF_FOOTER = "</rdf:RDF>";

    public void write(Response response, PrintWriter writer) throws Exception {
        if (response instanceof SearchResponse) {
            SearchResponse searchResponse = (SearchResponse) response;
            Collection<SearchResult> concepts = searchResponse.getSearchResults();    writer.write(RDF_HEADER);

            writer.write("\t<opensearch:totalResults>"+ searchResponse.getTotalResults() + "</opensearch:totalResults>\n");
            writer.write("\t<opensearch:startIndex>"+ searchResponse.getStartIndex() + "</opensearch:startIndex>\n");
            writer.write("\t<opensearch:itemsPerPage>"+ searchResponse.getQuery().getMaxResults() + "</opensearch:itemsPerPage>\n");

            /*
            StringBuffer searchBaseUrl = new StringBuffer(searchResponse.getQuery().getContextURL());
            searchBaseUrl.append("search/");
            if (searchResponse.getQuery().getCategory() !=null) {
                searchBaseUrl.append(searchResponse.getQuery().getCategory());
            }
            searchBaseUrl.append("?q=");
            searchBaseUrl.append(searchResponse.getQuery().getSearchTerm());
            searchBaseUrl.append("&amp;format=rdf&amp;startIndex=");
            searchBaseUrl.append(searchResponse.getQuery().getStartIndex());
            searchBaseUrl.append("&amp;count" + searchResponse.getQuery().getMaxResults());
            */
            writer.write("\t<opensearch:Query role=\"request\" searchTerms=\""
                    + searchResponse.getQuery().getSearchTerm()
                    + "\" startPage=\"" + searchResponse.getQuery().getStartIndex() + "\"/>\n");

            for (Iterator<SearchResult> iterator = concepts.iterator(); iterator.hasNext();) {
                SearchResult concept = iterator.next();
                writer.write("\t<rdf:Description rdf:about=\"");
                writer.write(concept.getId());
                writer.write("\">\n");

                if (concept.getTitle() !=null) {
                    writer.write("\t\t<skos:prefLabel xml:lang=\"en\">");
                    writer.write(concept.getTitle("en"));
                    writer.write("</skos:prefLabel>\n");
                }

                if (concept.getTitle(OpenSearchConstants.FRENCH_LANGUAGE) !=null) {
                    writer.write("\t\t<skos:prefLabel xml:lang=\"" + OpenSearchConstants.FRENCH_LANGUAGE + "\">");
                    writer.write(concept.getTitle(OpenSearchConstants.FRENCH_LANGUAGE));
                    writer.write("</skos:prefLabel>\n");
                }

                if (concept.getDescription() !=null) {
                    writer.write("\t\t<skos:definition xml:lang=\"en\">");
                    writer.write(concept.getDescription());
                    writer.write("</skos:definition>\n");
                }

                if (concept.getDescription(OpenSearchConstants.FRENCH_LANGUAGE) !=null) {
                    writer.write("\t\t<skos:definition xml:lang=\"" + OpenSearchConstants.FRENCH_LANGUAGE + "\">");
                    writer.write(concept.getDescription(OpenSearchConstants.FRENCH_LANGUAGE));
                    writer.write("</skos:definition>\n");
                }

                if (concept.getScore() != -1) {
                    writer.write("\t\t<relevance:score>");
                    DecimalFormat df = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
                    String value = df.format(concept.getScore());
                    writer.write(value);
                    writer.write("</relevance:score>\n");
                }


                writer.write("\t</rdf:Description>\n");
            }

            writer.write(RDF_FOOTER);
            writer.flush();
        }
    }
}