package com.erdas.projects.epf.proxy.os.index;

import com.erdas.projects.epf.proxy.os.TestConstant;
import com.erdas.projects.epf.proxy.os.model.SearchResult;
import com.erdas.projects.epf.proxy.os.model.reader.ModelReader;
import com.erdas.projects.epf.proxy.os.model.reader.ReaderException;
import com.erdas.projects.epf.proxy.os.model.reader.SkosReader;
import com.erdas.projects.epf.proxy.os.model.reader.SparqlResponseParser;
import junit.framework.TestCase;

import javax.xml.xpath.XPathExpressionException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 22-août-2011
 * Time: 17:03:30
 * To change this template use File | Settings | File Templates.
 */
public class SolrIndexerTest extends TestCase {

    public void testIndexGemetFromSparql() throws XPathExpressionException, FileNotFoundException {

        SparqlResponseParser parser = new SparqlResponseParser();
        Collection<SearchResult> collectionList = parser.parse(new FileInputStream("src/test/resources/sparql-response.xml"));

        SolrIndexer indexer = new SolrIndexer();
        indexer.index("GEMET",collectionList);
    }

    public void testIndexGemetFromRDF() throws XPathExpressionException, FileNotFoundException, ReaderException {

        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream(TestConstant.GEMET_CORE),"GEMET");
        collectionList = reader.read(collectionList,new FileInputStream(TestConstant.GEMET_EN),"en","GEMET");
        collectionList = reader.read(collectionList,new FileInputStream(TestConstant.GEMET_FR),"fr","GEMET");

        SolrIndexer indexer = new SolrIndexer();
        indexer.index("GEMET",collectionList);
    }

    public void testIndexOTEGFromRDF() throws XPathExpressionException, FileNotFoundException, ReaderException {

        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream(TestConstant.OTEG_EN),"OTEG");

        SolrIndexer indexer = new SolrIndexer();
        //indexer.index(collectionList);

        indexer.index("OTEG",collectionList);
    }

    public void testIndexMultiDomainFromRDF() throws XPathExpressionException, FileNotFoundException, ReaderException {

        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream(TestConstant.MULTIDOMAIN_EN),"MULTIDOMAIN");

        SolrIndexer indexer = new SolrIndexer();
        //indexer.index(collectionList);

        indexer.index("MULTIDOMAIN",collectionList);
    }
}
