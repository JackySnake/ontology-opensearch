package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.exceptions.OpenSearchException;
import com.erdas.projects.epf.proxy.os.model.IndexResponse;

/**
 * Interface of the indexer service
 */
public interface IndexerService {

     public IndexResponse index(String datasetName) throws OpenSearchException;

}
