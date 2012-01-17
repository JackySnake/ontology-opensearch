package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.exceptions.OpenSearchException;
import com.erdas.projects.epf.proxy.os.model.SearchResponse;
import com.erdas.projects.epf.proxy.os.model.SearchResult;
import com.erdas.projects.epf.proxy.os.model.SearchQuery;

import java.util.Collection;

/**
 * @author fabian.skivee@erdas.com
 */
public interface OpenSearchService {

    public SearchResponse search(SearchQuery query) throws OpenSearchException;


}
