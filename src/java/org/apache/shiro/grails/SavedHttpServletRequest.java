/*
 * Copyright 2010 Peter Ledbrook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shiro.grails;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;

/**
 * If the user posts some data from his browser while not authenticated,
 * we cannot simply redirect back to the original URL after login because
 * the POST becomes a GET. This servlet wrapper allows us to save the
 * details of the original request, including the POST data. Note it
 * currently only works with POST requests - don't use it to save GET
 * or PUT requests.
 */
public class SavedHttpServletRequest extends HttpServletRequestWrapper implements Serializable {
    private Hashtable parameterMap;
    private byte[] savedContent;

    public SavedHttpServletRequest(HttpServletRequest request) {
        super(request);

        // Copy the content from the request input stream to a byte array.
        int contentLength = request.getContentLength();
        ByteArrayOutputStream output = new ByteArrayOutputStream(contentLength == -1 ? 1024 : contentLength);
        try {
            IOUtils.copy(request.getInputStream(), output);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save the byte array (the request content) and the request parameters.
        savedContent = output.toByteArray();
        parameterMap = new Hashtable(request.getParameterMap());
    }

    /**
     * Hard-coded to always return "POST".
     */
    public String getMethod() {
        return "POST";
    }

    public String getParameter(String name) {
        Object val = parameterMap.get(name);
        if (val instanceof String[]) return ((String[]) val)[0];
        else if (val == null) return null;
        else return val.toString();
    }

    public Map getParameterMap() {
        return Collections.unmodifiableMap(parameterMap);
    }

    public Enumeration getParameterNames() {
        return parameterMap.keys();
    }

    public String[] getParameterValues(String name) {
        Object val = parameterMap.get(name);
        if (val instanceof String[]) return (String[]) val;
        else if (val == null) return new String[0];
        else return new String[] { val.toString() };
    }

    public ServletInputStream getInputStream() throws IOException {
        return new ByteArrayServletInputStream(savedContent);
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(savedContent)));
    }

    /**
     * Effectively a ServletInputStream wrapper for a ByteArrayInputStream.
     */
    private static class ByteArrayServletInputStream extends ServletInputStream {
        private InputStream input;
        
        public ByteArrayServletInputStream(byte[] data) {
            super();
            input = new ByteArrayInputStream(data);
        }

        public int read() throws IOException {
            return input.read();
        }
    }
}
