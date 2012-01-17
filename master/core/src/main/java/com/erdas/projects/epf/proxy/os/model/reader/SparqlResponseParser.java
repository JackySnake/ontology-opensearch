package com.erdas.projects.epf.proxy.os.model.reader;

import com.erdas.projects.epf.proxy.os.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author fabian.skivee@erdas.com
 */
public class SparqlResponseParser {

	// Constants

	final static String DEFAULT_LANGUAGE	= "en";

	// Fields

	Logger logger_ = null;

	// Constructors

	public SparqlResponseParser() {
		logger_ = LoggerFactory.getLogger(this.getClass());
	}

	// Methods


     public List<SearchResult> parse(InputStream inputStream) throws XPathExpressionException {
        return parse(inputStream,DEFAULT_LANGUAGE);
     }

    public List<SearchResult> parse(InputStream inputStream, String lang) throws XPathExpressionException {
        String xpathQuery = "/*[name()='sparql']/*[name()='results']/*[name()='result'][*]"; // /*[name()='binding'][@name='conceptLabel']/*[name()='literal']/text()";

        Element element = loadFile(inputStream);

		if (logger_.isDebugEnabled()) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Transformer transformer;

				transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource domSrc = new DOMSource(element);
				transformer.transform(domSrc, new StreamResult(bos));

				//System.out.println("SPARQL response:\n" + bos.toString());
			} catch (TransformerConfigurationException e) {
				logger_.debug("Could not print SPARQL response: " + e);
			} catch (TransformerFactoryConfigurationError e) {
				logger_.debug("Could not print SPARQL response: " + e);
			} catch (TransformerException e) {
				logger_.debug("Could not print SPARQL response: " + e);
			}
		}


        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        XPathExpression expression = xpath.compile(xpathQuery);

        Object result = expression.evaluate(element, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;

        List<SearchResult> results = parseNodes(nodes, lang);

        return results;
    }


    private List<SearchResult> parseNodes(NodeList resultNodeList, String lang) {

        SearchResult concept;
        String	uri;
        Element	value;
        String	predicate;
        Map<String, SearchResult> conceptMap = new LinkedHashMap<String, SearchResult>();

        // Parse all result nodes

        for(int index = 0; index < resultNodeList.getLength(); index ++){

        	value		= null;
        	predicate	= null;

            Node resultNode = resultNodeList.item(index);

            if (resultNode.getNodeType() == Node.ELEMENT_NODE){

                NodeList resultNodeChildren = resultNode.getChildNodes();

                // First get concept ID (URI)

                uri = getUri(resultNodeChildren);

                if (uri == null) {

                	// Could not find the URI, log warning and go to next result

                	logger_.warn("Could not find concept URI for result " + (index+1));

                	continue;
                }


                // Check if concept already created, if not create it

                concept = conceptMap.get(uri);
                if (concept == null) {
                	concept = new SearchResult();
                	concept.setId(uri);
                	conceptMap.put(uri,concept);
                }

                // Then get all other bindings

                for(int resultNodeChildrenIdx = 0; resultNodeChildrenIdx < resultNodeChildren.getLength(); resultNodeChildrenIdx ++) {
                    Node bindingNode = resultNodeChildren.item(resultNodeChildrenIdx);
                    if (bindingNode.getNodeName().equals("binding")) {

                        NamedNodeMap bindingNodeAttribute = bindingNode.getAttributes();
                        Node nameAttribute = bindingNodeAttribute.getNamedItem("name");
                        String nameValue = nameAttribute.getNodeValue();

                        if (nameValue.equals("label")) {
                            // add label
                            NodeList childNodes1 = bindingNode.getChildNodes();
                            for(int index1 = 0; index1 < childNodes1.getLength(); index1 ++){
                                Node childNode = childNodes1.item(index1);
                                if (childNode.getNodeName().equals("literal")) {
                                    String label = childNode.getFirstChild().getNodeValue();
                                    //System.out.println("Label " + label);
                                    concept.setTitle(label);
                                }
                            }
                        } else if (nameValue.equals("definition")) {
                            // add definition
                            NodeList childNodes1 = bindingNode.getChildNodes();
                            for(int index1 = 0; index1 < childNodes1.getLength(); index1 ++){
                                Node childNode = childNodes1.item(index1);
                                if (childNode.getNodeName().equals("literal")) {
                                    String description = childNode.getFirstChild().getNodeValue();
                                    //System.out.println("Description " + childNode.getFirstChild().getNodeValue());
                                    concept.setDescription(description);
                                }
                            }
                        }
                    }
                }

            }
        }

        return new ArrayList<SearchResult>(conceptMap.values());
    }

	private Element getFirstElement(Node bindingNode) {

		Element elt = null;

		NodeList childNodes = bindingNode.getChildNodes();
		for(int i = 0; i < childNodes.getLength(); i ++){
		    Node childNode = childNodes.item(i);
		    if (childNode instanceof Element) {
		    	elt = (Element)childNode;
		    	break;
		    }
		}
		return elt;
	}

	private String getUri(NodeList childrenNodeList) {

		String uri = null;

		for(int resultNodeChildrenIdx = 0; resultNodeChildrenIdx < childrenNodeList.getLength(); resultNodeChildrenIdx ++) {
		    Node bindingNode = childrenNodeList.item(resultNodeChildrenIdx);
		    if (bindingNode.getNodeName().equals("binding")) {
		        NamedNodeMap bindingNodeAttribute = bindingNode.getAttributes();
		        Node nameAttribute = bindingNodeAttribute.getNamedItem("name");
		        String nameValue = nameAttribute.getNodeValue();
		        if (nameValue.equals("id") || nameValue.equals("subject")) {
		            uri = getUri(bindingNode);
		            // We have found the Id, break the loop
		            break;
		        }
		    }
		}
		return uri;
	}

	private String getUri(Node bindingNode) {

		String uri = null;

		NodeList childNodes1 = bindingNode.getChildNodes();
		for(int index1 = 0; index1 < childNodes1.getLength(); index1 ++){
		    Node childNode = childNodes1.item(index1);
		    if (childNode.getNodeName().equals("uri")) {
		        uri = childNode.getFirstChild().getNodeValue();
		    }
		}
		return uri;
	}

    private Element loadFile(InputStream inputStream) {
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