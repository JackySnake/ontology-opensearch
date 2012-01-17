package com.erdas.projects.epf.proxy.os.model.reader;

import com.erdas.projects.epf.proxy.os.model.SearchResult;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: fskivee
 * Date: 23-août-2011
 * Time: 15:10:01
 * To change this template use File | Settings | File Templates.
 */
public interface ModelReader {

    Collection<SearchResult> read(InputStream is, String category) throws ReaderException;

    Collection<SearchResult> read(InputStream is,String lang, String category) throws ReaderException;

    Collection<SearchResult> read(Collection<SearchResult> list, InputStream is, String category) throws ReaderException;

    Collection<SearchResult> read(Collection<SearchResult> list, InputStream is,String lang, String category) throws ReaderException;

}
