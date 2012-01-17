package com.erdas.projects.epf.proxy.os.model;

import com.google.gson.annotations.Expose;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 19-août-2011
 * Time: 11:10:53
 * To change this template use File | Settings | File Templates.
 */
public class SearchQuery {

    final static String DEFAULT_LANGUAGE	= "en";
    final static int DEFAULT_MAX_RESULTS	= 10;

    @Expose
    private String searchTerm;

    @Expose
    private String language = DEFAULT_LANGUAGE;

    @Expose
    private int maxResults = DEFAULT_MAX_RESULTS;

    @Expose
    private int startIndex = 1;
    
    private String contextURL;

    private String category = null;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getContextURL() {
        return contextURL;
    }

    public void setContextURL(String contextURL) {
        this.contextURL = contextURL;
    }
}
