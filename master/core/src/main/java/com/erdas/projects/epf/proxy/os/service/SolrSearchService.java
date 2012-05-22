package com.erdas.projects.epf.proxy.os.service;

import com.erdas.projects.epf.proxy.os.exceptions.OpenSearchException;
import com.erdas.projects.epf.proxy.os.index.SolrIndexer;
import com.erdas.projects.epf.proxy.os.model.IndexResponse;
import com.erdas.projects.epf.proxy.os.model.SearchQuery;
import com.erdas.projects.epf.proxy.os.model.SearchResponse;
import com.erdas.projects.epf.proxy.os.model.SearchResult;
import com.erdas.projects.epf.proxy.os.model.reader.ModelReader;
import com.erdas.projects.epf.proxy.os.model.reader.ReaderException;
import com.erdas.projects.epf.proxy.os.model.reader.SkosReader;
import com.erdas.projects.epf.proxy.os.util.OpenSearchConstants;
import com.erdas.projects.epf.proxy.os.util.PropertyLoader;
import com.erdas.projects.epf.proxy.os.util.ResourceLoader;
import com.erdas.projects.epf.proxy.os.util.VoidDoc;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * SOLR service implementing OpenSearchService and IndexerService interface
 */
public class SolrSearchService implements OpenSearchService, IndexerService {

    protected String solrURL;
    protected String voidURL;
    protected SolrServer server = null;
    protected VoidDoc voidDoc = null;

    protected Logger logger = LoggerFactory.getLogger(OpenSearchServlet.class);

    public SolrSearchService() {
        Properties properties = PropertyLoader.loadProperties("config.properties");
        solrURL = properties.getProperty("solr.url");
        voidURL = properties.getProperty("void.url");
        logger.info("Create SolrService on url:" + solrURL);
    }

    public SolrSearchService(String solrURL) {
        this.solrURL = solrURL;
    }

    private void init() throws OpenSearchException {
        /*
          CommonsHttpSolrServer is thread-safe and if you are using the following constructor,
          you *MUST* re-use the same instance for all requests.  If instances are created on
          the fly, it can cause a connection leak. The recommended practice is to keep a
          static instance of CommonsHttpSolrServer per solr server url and share it for all requests.
          See https://issues.apache.org/jira/browse/SOLR-861 for more details
        */
        try {
            server = new CommonsHttpSolrServer(solrURL);
            Document document = ResourceLoader.parseResourceAsDocument(voidURL);
            voidDoc = VoidDoc.parse(document);

        } catch (MalformedURLException e) {
            logger.error("Cannot access SOLR service " + solrURL,e);
            throw new OpenSearchException("Cannot initialize service",e);
        } catch (IOException e) {
            logger.error("Cannot read config file " + voidURL,e);
            throw new OpenSearchException("Cannot initialize service",e);
        } catch (Exception e) {
            logger.error("Cannot initialize service",e);
            throw new OpenSearchException("Cannot initialize service",e);
        }
    }

    public SearchResponse search(SearchQuery query) throws OpenSearchException {
        if (server == null) {
            init();
        }

        String searchTerm = query.getSearchTerm();
        searchTerm = searchTerm.trim();
        if ((searchTerm == null) || (searchTerm.length() == 0)) {
            throw new OpenSearchException("The search term is mandatory");
        }

        String[] tokens = searchTerm.split(" ");
        StringBuffer processedTerm = new StringBuffer();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            String replacedToken = token;
            if (token.startsWith("http://") || token.startsWith("urn:")) {
                replacedToken = "\"" + token + "\"";
                logger.info("Replace HTTP or URN token {} by {}",token,replacedToken);
            }
            replacedToken = replacedToken.replace(":","\\:");
            processedTerm.append(replacedToken);
            if (i < tokens.length-1) {
                processedTerm.append(" ");
            }
        }
        searchTerm = processedTerm.toString();

        StringBuffer searchQuery = new StringBuffer();
        if (OpenSearchConstants.FRENCH_LANGUAGE.equals(query.getLanguage())) {
            searchQuery.append("name_fr:(");
            searchQuery.append(searchTerm);
            searchQuery.append(") ");
            searchQuery.append("text_fr:(");
            searchQuery.append(searchTerm);
            searchQuery.append(")");
        } else {
            searchQuery.append("name:(");
            searchQuery.append(searchTerm);
            searchQuery.append(") ");
            searchQuery.append("text:(");
            searchQuery.append(searchTerm);
            searchQuery.append(")");
        }

        if (query.getCategory() != null) {
            searchQuery.append(" AND cat:");
            searchQuery.append(query.getCategory());
        }

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(searchQuery.toString());
        solrQuery.setRows(query.getMaxResults());
        solrQuery.setStart(query.getStartIndex()-1);
        solrQuery.setFields("*,score");

        SearchResponse response = new SearchResponse();
        response.setQuery(query);
        List<SearchResult> results = new ArrayList<SearchResult>();
        try {

            System.out.println("SOLR query " + solrQuery);
            QueryResponse rsp = server.query(solrQuery);

            response.setTotalResults(rsp.getResults().getNumFound());
            response.setStartIndex(rsp.getResults().getStart()+1);
            float maxScore = rsp.getResults().getMaxScore();
            Iterator<SolrDocument> iter = rsp.getResults().iterator();
            while (iter.hasNext()) {
                SolrDocument resultDoc = iter.next();

                String id = (String) resultDoc.getFieldValue("id"); //id is the uniqueKey field
                String title = (String) resultDoc.getFieldValue("name");
                String description = (String) resultDoc.getFieldValue("description");
                Float score = (Float) resultDoc.getFieldValue("score");
                String title_fr = (String) resultDoc.getFieldValue("name_fr");
                String description_fr = (String) resultDoc.getFieldValue("description_fr");

                List<String> broader = convertList(resultDoc.getFieldValues("broader"));
                List<String> narrower = convertList(resultDoc.getFieldValues("narrower"));
                List<String> relatedMatch = convertList(resultDoc.getFieldValues("relatedMatch"));
                List<String> exactMatch = convertList(resultDoc.getFieldValues("exactMatch"));
                List<String> closeMatch = convertList(resultDoc.getFieldValues("closeMatch"));

                SearchResult result = new SearchResult();
                if (id != null) result.setId(id);
                if (title != null) result.setTitle(title);
                if (title_fr != null) result.setTitle(title_fr,"fr");
                if (description != null) result.setDescription(description);
                if (description_fr != null) result.setDescription(description_fr,"fr");
                if (score != null) result.setScore(score / maxScore);
                if (broader != null) result.setBroaderConcepts(broader);
                if (narrower != null) result.setNarrowerConcepts(narrower);
                if (relatedMatch != null) result.setRelatedConcepts(relatedMatch);
                if (exactMatch != null) result.setExactConcepts(exactMatch);
                if (closeMatch != null) result.setCloseConcepts(closeMatch);

                results.add(result);
            }


        } catch (SolrServerException e) {
            throw new OpenSearchException("Error from the server",e);
        }
        response.setSearchResults(results);

        return response;
    }

    private List<String> convertList(Collection<Object> col) {
        if (col == null) {
            return null;
        }

        List<String> results = new ArrayList<String>(col.size());
        for (Iterator<Object> it = col.iterator(); it.hasNext();) {
            Object value =  it.next();
            results.add(value.toString());
        }
        return results;
    }

    public IndexResponse index(String datasetName) throws OpenSearchException {
        IndexResponse response = new IndexResponse();

        if (server == null) {
            init();
        }

        logger.info("Index dataset {} ", datasetName);

        List<VoidDoc.Dataset> datasetList = voidDoc.getDataSets();
        VoidDoc.Dataset dataset = null;
        boolean found = false;
        for (int i = 0; i < datasetList.size(); i++) {
            dataset =  datasetList.get(i);
            if (dataset.getTitle().equals(datasetName)) {
                found=true;
                break;
            }
        }
        if (!found) {
            throw new OpenSearchException("Cannot find datasetName " + datasetName + " in the config");
        } else {
            ModelReader reader = new SkosReader();

            List<String> dumps = dataset.getDataDumps();
            Collection<SearchResult> itemList = new ArrayList<SearchResult>();
            for (int i = 0; i < dumps.size(); i++) {
                String dataDumpURL =  dumps.get(i);
                InputStream is = null;
                try {
                    URL url = new URL(dataDumpURL);
                    is = url.openStream();
                    itemList = reader.read(itemList,is,datasetName);
                } catch (IOException e) {
                    logger.error("Cannot load resource " + dataDumpURL + " for datasetName " + datasetName,e);
                    throw new OpenSearchException("Cannot load resource " + dataDumpURL + " for datasetName " + datasetName);
                } catch (ReaderException e) {
                    logger.error("Cannot parse the resource " + dataDumpURL + " for datasetName " + datasetName,e);
                    throw new OpenSearchException("Cannot parse the resource " + dataDumpURL + " for datasetName " + datasetName);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }

            SolrIndexer indexer = new SolrIndexer(server);
            indexer.index(datasetName,itemList);
            response.setTotalResults(itemList.size());

            return response;
        }
    }


}
