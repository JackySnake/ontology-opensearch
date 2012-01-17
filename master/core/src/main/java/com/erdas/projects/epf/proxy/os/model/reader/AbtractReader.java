package com.erdas.projects.epf.proxy.os.model.reader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 23-août-2011
 * Time: 15:13:20
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbtractReader implements ModelReader {

    public final static String DEFAULT_LANGUAGE = "en";
    
    protected Element loadFile(InputStream inputStream) {
        try {
            // first of all we request out
            // DOM-implementation:
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            // then we have to create document-loader:
            DocumentBuilder loader = factory.newDocumentBuilder();

            // loading a DOM-tree...
            Document document = loader.parse(inputStream); //"smaad-annotation-core/src/test/resources/sparql-response.xml");
            // at last, we get a root element:
            Element tree = document.getDocumentElement();

            // ... do something with document element ...
            return tree;
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
