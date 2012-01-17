package com.erdas.projects.epf.proxy.os.index;

import com.erdas.projects.epf.proxy.os.model.SearchResult;
import com.erdas.projects.epf.proxy.os.util.PropertyLoader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 22-août-2011
 * Time: 16:22:05
 * To change this template use File | Settings | File Templates.
 */
public class SolrIndexer {

    String url = null;
    SolrServer server = null;

    protected Logger logger = LoggerFactory.getLogger(SolrIndexer.class);

    public SolrIndexer() {
        Properties properties = PropertyLoader.loadProperties("config.properties");
        url = properties.getProperty("solr.url");
        logger.info("Create SolrService on url:" + url);
    }

    public SolrIndexer(String url) {
        this.url = url;
    }

    public SolrIndexer(SolrServer server) {
        this.server = server;
    }

    private void init() throws MalformedURLException {
        /*
          CommonsHttpSolrServer is thread-safe and if you are using the following constructor,
          you *MUST* re-use the same instance for all requests.  If instances are created on
          the fly, it can cause a connection leak. The recommended practice is to keep a
          static instance of CommonsHttpSolrServer per solr server url and share it for all requests.
          See https://issues.apache.org/jira/browse/SOLR-861 for more details
        */
        server = new CommonsHttpSolrServer(url);
    }

    public void index(String datasetName, Collection<SearchResult> items)  {
        long time = System.currentTimeMillis();
        if (server == null) {
            try {
                init();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        if ((items == null) || (items.size() == 0)) {
            logger.info("No items to index for dataset {}", datasetName);
            return;
        }
        try {

            // Delete old items
            server.deleteByQuery("cat:" + datasetName);
            server.commit();

            Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
            for (Iterator<SearchResult> stringIterator = items.iterator(); stringIterator.hasNext();) {
                SearchResult concept =  stringIterator.next();
                SolrInputDocument document = createDocument(concept);
                docs.add(document);
                logger.info("Add doc " + document.getFieldValue("id"));
            }

            UpdateResponse response = server.add( docs );
            logger.info("Response status " + response.getStatus());
            logger.info("Response " + response);
            server.commit();
        } catch (IOException e) {
            try {
                server.rollback();
            } catch (SolrServerException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (SolrServerException e) {
            try {
                server.rollback();
            } catch (SolrServerException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        time = System.currentTimeMillis() - time;
        logger.info("Index " + items.size() + " items in " + time + " ms");

    }

    private SolrInputDocument createDocument(SearchResult value) throws IOException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", value.getId(),1.0f);
        //doc.addField("name", value.getTitle(), 1.0f);
        if (value.getTitle() != null)
            doc.addField("name", value.getTitle(), 2.0f);

        if (value.getTitle("fr") != null)
            doc.addField("name_fr", value.getTitle("fr"), 2.0f);

        if (value.getCategory() != null)
            doc.addField("cat",value.getCategory(),1.0f);

        if (value.getDescription() != null)
            doc.addField("description", value.getDescription(), 1.0f);

        if ( value.getDescription("fr") != null)
            doc.addField("description_fr", value.getDescription("fr"), 1.0f);

        if ( value.getBroaderConcepts() != null && value.getBroaderConcepts().size() > 0)
            doc.addField("broader", value.getBroaderConcepts(), 1.0f);

        if ( value.getNarrowerConcepts() != null && value.getNarrowerConcepts().size() > 0)
            doc.addField("narrower", value.getNarrowerConcepts(), 1.0f);

        if ( value.getExactConcepts() != null && value.getExactConcepts().size() > 0)
            doc.addField("exactMatch", value.getExactConcepts(), 1.0f);

        if ( value.getRelatedConcepts() != null && value.getRelatedConcepts().size() > 0)
            doc.addField("relatedMatch", value.getRelatedConcepts(), 1.0f);

        if ( value.getCloseConcepts() != null && value.getCloseConcepts().size() > 0)
            doc.addField("closeMatch", value.getCloseConcepts(), 1.0f);

        return doc;
    }

}
