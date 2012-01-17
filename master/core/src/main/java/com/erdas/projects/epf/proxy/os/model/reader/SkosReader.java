package com.erdas.projects.epf.proxy.os.model.reader;

import com.erdas.projects.epf.proxy.os.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.*;
import java.io.InputStream;
import java.util.*;

/**
 * Skos reader
 */
public class SkosReader extends AbtractReader {

    protected Logger logger = LoggerFactory.getLogger(SkosReader.class);

    public Collection<SearchResult> read(InputStream is, String category) throws ReaderException {
        return read(is,DEFAULT_LANGUAGE,category);
    }

    public Collection<SearchResult> read(InputStream is,String lang, String category) throws ReaderException {
        return read(null,is,DEFAULT_LANGUAGE,category);
    }

    public Collection<SearchResult> read(Collection<SearchResult> list, InputStream is,String category) throws ReaderException {
        try {
            return parse(list,is,DEFAULT_LANGUAGE,category);
        } catch (XPathExpressionException e) {
            throw new ReaderException("Cannot read stream",e);
        }
    }

    public Collection<SearchResult> read(Collection<SearchResult> list, InputStream is,String lang,String category) throws ReaderException {
        try {
            return parse(list,is,lang,category);
        } catch (XPathExpressionException e) {
            throw new ReaderException("Cannot read stream",e);
        }
    }

    protected Collection<SearchResult> parse(Collection<SearchResult> list, InputStream inputStream, String lang, String category) throws XPathExpressionException {
        String xpathQuery = "/*[local-name()='RDF']/*[local-name()='Description' or local-name()='Concept'][*]";

        Element element = loadFile(inputStream);
        String testLang = element.getAttribute("xml:lang");
        String baseURL = element.getAttribute("xml:base");
        if (testLang != null && !testLang.equals("")) {
            lang = testLang;
            logger.info("Detect xml:lang in the root element " + lang);
        }

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        XPathExpression expression = xpath.compile(xpathQuery);

        Object result = expression.evaluate(element, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;

        Map<String,SearchResult> resultMap = new HashMap<String,SearchResult>();
        if (list != null) {
            for (Iterator<SearchResult> searchResultIterator = list.iterator(); searchResultIterator.hasNext();) {
                SearchResult searchResult =  searchResultIterator.next();
                resultMap.put(searchResult.getId(),searchResult);
            }
        }

        Collection<SearchResult> results = parseNodes(resultMap,nodes, lang, baseURL, category);

        return results;
    }


    private Collection<SearchResult> parseNodes(Map<String,SearchResult> initialMap, NodeList resultNodeList, String lang, String baseURL, String category) {
        //Collection<SearchResult> results = new ArrayList<SearchResult>(resultNodeList.getLength());
        for(int index = 0; index < resultNodeList.getLength(); index ++){
            Node resultNode = resultNodeList.item(index);
            resultNode.getNodeType();

            if (resultNode.getNodeType() == Node.ELEMENT_NODE){
                NamedNodeMap map = resultNode.getAttributes();
                Node node = map.getNamedItem("rdf:about");
                String id = node.getNodeValue();

                id = formatAbsoluteConceptId(baseURL,id);

                SearchResult result = new SearchResult();
                result.setId(id);
                if (initialMap.containsKey(id)) {
                    result = initialMap.get(id);
                } else {
                    initialMap.put(id,result);
                }

                if (category != null) {
                    result.setCategory(category);
                }

                NodeList resultNodeChildren = resultNode.getChildNodes();

                for(int resultNodeChildrenIdx = 0; resultNodeChildrenIdx < resultNodeChildren.getLength(); resultNodeChildrenIdx ++) {
                    Node children = resultNodeChildren.item(resultNodeChildrenIdx);
                    if (children.getNodeName().equals("skos:prefLabel")) {
                        String label = children.getFirstChild().getNodeValue();
                        NamedNodeMap map1 = children.getAttributes();
                        String myLang = lang;
                        if (map1 != null) {
                            Node node1 = map1.getNamedItem("xml:lang");
                            if (node1 != null && node1.getNodeValue()!=null && !node1.getNodeValue().equals("")) {
                                myLang = node1.getNodeValue();
                                logger.info("Detect xml:lang in the prefLabel relation " + myLang);
                            }
                        }
                        result.setTitle(label,myLang);
                    } else if (children.getNodeName().equals("skos:definition")) {
                        String definition = children.getFirstChild().getNodeValue();
                        // Manage xml:lang if present in the definition node
                        result.setDescription(definition,lang);
                    } else if (children.getNodeName().equals("skos:relatedMatch")) {
                        if (children.getAttributes() != null && children.getAttributes().getNamedItem("rdf:resource") != null) {
                            String value = children.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                            result.addRelatedConcept(value);
                        }
                    } else if (children.getNodeName().equals("skos:narrower")) {
                        if (children.getAttributes() != null && children.getAttributes().getNamedItem("rdf:resource") != null) {
                            String value = children.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                            value = formatAbsoluteConceptId(baseURL,value);
                            result.addNarrowerConcept(value);
                        }
                    } else if (children.getNodeName().equals("skos:broader")) {
                        if (children.getAttributes() != null && children.getAttributes().getNamedItem("rdf:resource") != null) {
                            String value = children.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                            value = formatAbsoluteConceptId(baseURL,value);
                            result.addBroaderConcept(value);
                        }
                    } else if (children.getNodeName().equals("skos:exactMatch")) {
                        if (children.getAttributes() != null && children.getAttributes().getNamedItem("rdf:resource") != null) {
                            String value = children.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                            result.addExactConcept(value);
                        }
                    } else if (children.getNodeName().equals("skos:closeMatch")) {
                        if (children.getAttributes() != null && children.getAttributes().getNamedItem("rdf:resource") != null) {
                            String value = children.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                            result.addCloseConcept(value);
                        }
                    }
                }
                //results.add(result);
            }
        }
        return initialMap.values();
    }


    protected String formatAbsoluteConceptId(String baseURL, String id) {
        String result = id;

        if (!id.startsWith("http") && (baseURL != null)) {
            result = baseURL + id;
        }

        return result;
    }


}
