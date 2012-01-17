package com.erdas.projects.epf.proxy.os.index;

import com.erdas.projects.epf.proxy.os.model.SearchResult;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.*;

/**
 * @author fabian.skivee@erdas.com
 */
public class LuceneIndexer {

    private StandardAnalyzer analyzer;
    private Directory index;
    private Map<String, SearchResult> conceptsMap;

    public void init() throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        analyzer = new StandardAnalyzer(Version.LUCENE_30);

        // 1. create the index
        index = new RAMDirectory();

        conceptsMap = new HashMap<String,SearchResult>();
    }

    public void index(Collection<SearchResult> items) throws IOException {
        long time = System.currentTimeMillis();

        // the boolean arg in the IndexWriter ctor means to
        // create a new index, overwriting any existing index
        IndexWriter writer = new IndexWriter(index, analyzer, true,IndexWriter.MaxFieldLength.UNLIMITED);


        for (Iterator<SearchResult> stringIterator = items.iterator(); stringIterator.hasNext();) {
            SearchResult concept =  stringIterator.next();
            addDoc(writer,concept);
            conceptsMap.put(concept.getId(),concept);
        }
        writer.close();

        time = System.currentTimeMillis() - time;
        System.out.println("Index " + items.size() + " items in " + time + " ms");
    }

    public List<SearchResult> search(String queryString) throws ParseException, IOException {
        return search(queryString,false);
    }

    public List<SearchResult> search(String queryString,boolean deepSearch) throws ParseException, IOException {
        System.out.println("Search for item: " + queryString + " [deep: " + deepSearch + "]");
        long time = System.currentTimeMillis();

        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        //Query query = new QueryParser(Version.LUCENE_30, "title", analyzer).parse(queryString);

        /*
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                                                new string[] {"bodytext", "title"},
                                                analyzer);
        */


        //query.add(new TermQuery(new Term("contents","java")), true, false);

        BooleanQuery booleanQuery = new BooleanQuery();
        Query query1 = new TermQuery(new Term("title",queryString));
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);

        if (deepSearch) {
            Query query2 = new TermQuery(new Term("description",queryString));
            booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        }


        // Use BooleanClause.Occur.MUST instead of BooleanClause.Occur.SHOULD
        // for AND queries
        //Hits hits = searcher.Search(booleanQuery);

        // search
        int hitsPerPage = 10;
        IndexSearcher searcher = new IndexSearcher(index, true);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(booleanQuery, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        time = System.currentTimeMillis() - time;

        // 4. display results
        System.out.println("Found " + hits.length + " hits in " + time + " ms");
        List<SearchResult> results = new ArrayList<SearchResult>();
        for (int i=0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            String id = d.get("id");
            SearchResult result = conceptsMap.get(id);
            results.add(result);
        }

        // searcher can only be closed when there
        // is no need to access the documents any more.
        searcher.close();

        return results;
    }


//    private static void addDoc(IndexWriter w, String value) throws IOException {
//        Document doc = new Document();
//        doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
//        w.addDocument(doc);
//    }

    private static void addDoc(IndexWriter w, SearchResult value) throws IOException {
        Document doc = new Document();
        doc.add(new Field("id", value.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("title", value.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("description", value.getDescription(), Field.Store.YES, Field.Index.ANALYZED));
        w.addDocument(doc);
    }

}