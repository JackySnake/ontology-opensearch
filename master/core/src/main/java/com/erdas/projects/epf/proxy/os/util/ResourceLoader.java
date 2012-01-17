package com.erdas.projects.epf.proxy.os.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Load a ressource
 */
public class ResourceLoader {

    protected static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);

    public static InputStream loadResource(String resourcePath) throws IOException {
        long time = System.currentTimeMillis();
        ClassLoader loader = ResourceLoader.class.getClassLoader();
        URL resource = loader.getResource(resourcePath);

       InputStream is = resource.openConnection().getInputStream();

        time = System.currentTimeMillis() - time;
        logger.info("Load ressource {} in  {} ms", resourcePath,time);

        return is;
    }

    public static Document parseResourceAsDocument(String resourcePath) throws IOException {

        InputStream is = null;
        Document doc = null;
        try {
           is = loadResource(resourcePath);
           doc = parseStreamAsDocument(is);
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e) { }
            }
        }
        return doc;
    }

    public static Document parseStreamAsDocument(InputStream inputStream) {
        try {
            // first of all we request out
            // DOM-implementation:
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            // then we have to create document-loader:
            DocumentBuilder loader = factory.newDocumentBuilder();

            // loading a DOM-tree...
            Document document = loader.parse(inputStream);

            return document;
        } catch (IOException ex) {
            // any IO errors occur:
            ex.printStackTrace();
        } catch (SAXException ex) {
            // parse errors occur:
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            // document-loader cannot be created which,
            // satisfies the configuration requested
            ex.printStackTrace();
        } catch (FactoryConfigurationError ex) {
            // DOM-implementation is not available
            // or cannot be instantiated:
            ex.printStackTrace();
        }
        return null;
    }

}
