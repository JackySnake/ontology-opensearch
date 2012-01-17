package com.erdas.projects.epf.proxy.os.model.reader;

import com.erdas.projects.epf.proxy.os.model.SearchResult;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 23-août-2011
 * Time: 15:18:26
 * To change this template use File | Settings | File Templates.
 */
public class SkosReaderTest extends TestCase {


    public void testReadGemetEN() throws ReaderException, FileNotFoundException {
        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream("src/test/resources/samples/ontology/gemet/gemet-definitions-en.rdf"),"GEMET");

        System.out.println(collectionList.size() + " items found");
    }

    public void testReadGemetFR() throws ReaderException, FileNotFoundException {
        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream("src/test/resources/samples/ontology/gemet/gemet-definitions-fr.rdf"),"GEMET");

        System.out.println(collectionList.size() + " items found");
    }

    public void testReadSesameExport() throws ReaderException, FileNotFoundException {
        ModelReader reader = new SkosReader();
        Collection<SearchResult> collectionList = reader.read(new FileInputStream("src/test/resources/samples/ontology/sesame-export.rdf"),"GEMET");

        System.out.println(collectionList.size() + " items found");
    }

}
