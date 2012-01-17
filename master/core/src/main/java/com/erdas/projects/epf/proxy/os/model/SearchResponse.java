package com.erdas.projects.epf.proxy.os.model;

import com.google.gson.annotations.Expose;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 24-août-2011
 * Time: 14:28:56
 * To change this template use File | Settings | File Templates.
 */
public class SearchResponse implements Response {

    @Expose
    private long totalResults;

    private long startIndex;

    @Expose
    private SearchQuery query;

    @Expose
    private List<SearchResult> searchResults;

    public SearchQuery getQuery() {
        return query;
    }

    public void setQuery(SearchQuery query) {
        this.query = query;
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

}
