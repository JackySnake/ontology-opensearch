package com.erdas.projects.epf.proxy.os.util;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 22-nov.-2011
 * Time: 14:16:50
 * To change this template use File | Settings | File Templates.
 */
public class VoidDocTest {

    @Test
    public void testParseVoid() throws Exception {

        Document document = ResourceLoader.parseResourceAsDocument("void.rdf");

        VoidDoc voidDoc = VoidDoc.parse(document);
        System.out.println(voidDoc);
    }



}
