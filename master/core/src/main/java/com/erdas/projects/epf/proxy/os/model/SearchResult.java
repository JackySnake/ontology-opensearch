package com.erdas.projects.epf.proxy.os.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 19-août-2011
 * Time: 11:11:42
 * To change this template use File | Settings | File Templates.
 */
public class SearchResult {

    private String DEFAULT_LANG = "en";

    @Expose
    private String id;

    @Expose
    private Map<String,String> title = new HashMap<String,String>();

    @Expose
    private Map<String,String> description = new HashMap<String,String>();

    @Expose
    private String category;

    @Expose
    private float score = -1;

    @Expose
    private List<String> closeConcepts		= new ArrayList<String>();
	@Expose
    private List<String> relatedConcepts	= new ArrayList<String>();
	@Expose
    private List<String> exactConcepts		= new ArrayList<String>();
	@Expose
    private List<String> broaderConcepts	= new ArrayList<String>();
	@Expose
    private List<String> narrowerConcepts	= new ArrayList<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return getTitle(DEFAULT_LANG);
    }

    public String getTitle(String lang) {
        return title.get(lang);
    }

    public void setTitle(String title) {
        this.title.put(DEFAULT_LANG,title);
    }

    public void setTitle(String title,String lang) {
        this.title.put(lang,title);
    }

    public String getDescription() {
        return getDescription(DEFAULT_LANG);
    }

    public void setDescription(String description) {
        setDescription(description,DEFAULT_LANG);
    }

    public String getDescription(String lang) {
        return description.get(lang);
    }

    public void setDescription(String description,String lang) {
        this.description.put(lang,description);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getCloseConcepts() {
        return closeConcepts;
    }

    public void setCloseConcepts(List<String> closeConcepts) {
        this.closeConcepts = closeConcepts;
    }

    public void addCloseConcept(String concept) {
        this.closeConcepts.add(concept);
    }

    public List<String> getRelatedConcepts() {
        return relatedConcepts;
    }

    public void setRelatedConcepts(List<String> relatedConcepts) {
        this.relatedConcepts = relatedConcepts;
    }

    public void addRelatedConcept(String concept) {
        this.relatedConcepts.add(concept);
    }

    public List<String> getExactConcepts() {
        return exactConcepts;
    }

    public void setExactConcepts(List<String> exactConcepts) {
        this.exactConcepts = exactConcepts;
    }

    public void addExactConcept(String concept) {
        this.exactConcepts.add(concept);
    }

    public List<String> getBroaderConcepts() {
        return broaderConcepts;
    }

    public void setBroaderConcepts(List<String> broaderConcepts) {
        this.broaderConcepts = broaderConcepts;
    }

    public void addBroaderConcept(String concept) {
        this.broaderConcepts.add(concept);
    }

    public List<String> getNarrowerConcepts() {
        return narrowerConcepts;
    }

    public void setNarrowerConcepts(List<String> narrowerConcepts) {
        this.narrowerConcepts = narrowerConcepts;
    }

    public void addNarrowerConcept(String concept) {
        this.narrowerConcepts.add(concept);
    }

    @Override
    public String toString() {
        return "id:" + id + ", title:" + title + ", description:"  + description;
    }
}
