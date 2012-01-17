////////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2011 ERDAS Inc.
//
////////////////////////////////////////////////////////////////////////////////
package com.erdas.projects.epf.proxy.os.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * @author fabian.skivee@erdas.com
 */
public class JSONModelWriter implements ModelWriter {

     public void write(Response searchResponse, PrintWriter writer) throws Exception {
        //Collection<SearchResult> concepts = searchResponse.getSearchResults();
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();

        String json = gson.toJson(searchResponse);
        writer.write(json);
    }
}