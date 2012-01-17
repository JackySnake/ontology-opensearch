////////////////////////////////////////////////////////////////////////////////
//
// Copyright (C) 2011 ERDAS Inc.
//
////////////////////////////////////////////////////////////////////////////////
package com.erdas.projects.epf.proxy.os.model;

import java.io.PrintWriter;
import java.util.Collection;

/**
 * @author fabian.skivee@erdas.com
 */
public interface ModelWriter {

    void write(Response searchResponse,PrintWriter writer) throws Exception;


}