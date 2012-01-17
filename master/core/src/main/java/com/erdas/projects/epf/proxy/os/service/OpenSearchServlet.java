////////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2011 ERDAS Inc.
//
////////////////////////////////////////////////////////////////////////////////
package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.exceptions.OpenSearchException;
import com.erdas.projects.epf.proxy.os.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * An open-search servlet using a AnnotationService
 *
 * @author fabian.skivee@erdas.com
 */
public class OpenSearchServlet extends HttpServlet {

    protected static String JSON_FORMAT = "json";
    protected static String ATOM_FORMAT = "atom";
    protected static String RDF_FORMAT = "rdf";
    protected static String DEFAULT_FORMAT = "atom";

    protected Logger logger = LoggerFactory.getLogger(OpenSearchServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OpenSearchService service = new SolrSearchService();
        SearchQuery query = new SearchQuery();

        String requestURI = req.getRequestURI();
        String[] uriSplitted = requestURI.split("/");
        boolean indexOperation = false;
        if ((uriSplitted.length > 3)) {
            if (uriSplitted[3] != null) {
                query.setCategory(uriSplitted[3]);
            }
            if (uriSplitted[2] != null) {
                if (uriSplitted[2].equalsIgnoreCase("index")) {
                    indexOperation = true;
                }
            }
        }

        if (indexOperation) {
            if (service instanceof IndexerService) {
                IndexResponse response = null;
                try {
                    response = ((IndexerService) service).index(query.getCategory());
                } catch (OpenSearchException e) {
                    handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform search" + e);
                }
                sendResult(response,JSON_FORMAT,resp);
            }
        } else {
            String searchTerm = req.getParameter("q");
            if (searchTerm == null) {
                handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "The mandatory parameter q with the search term is missing in the query");
            }

            String startIndexAsString = req.getParameter("startIndex");
            if (startIndexAsString != null && !startIndexAsString.isEmpty()) {
                int startIndex = Integer.parseInt(startIndexAsString);
                if (startIndex < 1) {
                    handleError(resp, HttpServletResponse.SC_BAD_REQUEST, "startIndex parameter must be greater or equal to 1");
                }
                query.setStartIndex(startIndex);
            }

            String contextURL = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            query.setContextURL(contextURL);

            query.setSearchTerm(searchTerm);

            String langParam = req.getParameter("lang");
            if (langParam != null && !langParam.isEmpty()) {
                String[] parts = tokenizeToStringArray(langParam, "-", false, false);
                if (parts.length > 0) {
                    logger.info("language = {}",parts[0]);
                    query.setLanguage(parts[0]);
                }
            }

            String maxResultsAsString = req.getParameter("count");
            if (maxResultsAsString != null && !maxResultsAsString.isEmpty()) {
                int maxResults = Integer.parseInt(maxResultsAsString);
                query.setMaxResults(maxResults);
            }

            try {
                SearchResponse searchResults = service.search(query);
                sendResult(searchResults,req.getParameter("format"),resp);
            } catch (OpenSearchException e) {
                handleError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot perform search" + e);
            }
        }
    }

    private void sendResult(Response searchResponse,String format, HttpServletResponse response) {
        ModelWriter writer = new AtomModelWriter();
        response.setContentType("application/xml; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        if (format != null) {
            if (JSON_FORMAT.equals(format)) {
                writer = new JSONModelWriter();
                response.setContentType("application/json; charset=UTF-8");
            } else if (RDF_FORMAT.equals(format)) {
                writer = new RDFModelWriter();
                response.setContentType("application/rdf+xml; charset=UTF-8");
            } else if (ATOM_FORMAT.equals(format)) {
                writer = new AtomModelWriter();
                response.setContentType("application/xml; charset=UTF-8");
            } else {
                handleError(response, HttpServletResponse.SC_BAD_REQUEST, "Output format not supported :" + format);
            }
        }

        PrintWriter out = null;
        try {
            out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF8"), true);

            // Put result object in the response
            writer.write(searchResponse,out);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "IO exception while preparing response: " + ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    private void handleError(HttpServletResponse response, int errorCode, String errorMessage) {

        logger.error(errorCode + " " + errorMessage);

        try {
            response.sendError(errorCode, errorMessage);
        } catch (IOException e) {
            response.setStatus(errorCode);
        }

    }

    private static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
                                                  boolean ignoreEmptyTokens) {
        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List<String> tokens = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

}