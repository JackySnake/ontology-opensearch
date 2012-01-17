package com.erdas.projects.epf.proxy.os.model;

import com.google.gson.annotations.Expose;

/**
 * Response ofn the IndexerService
 */
public class IndexResponse implements Response {

    @Expose
    private long totalResults;

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

}
