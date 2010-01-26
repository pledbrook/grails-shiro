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

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * This is a servlet filter that checks whether the current request
 * should be replaced by a saved POST request. The saved request is
 * only substituted for the current one if there is a saved request
 * and there is also a request parameter called "shiroPostRedirect".
 */
public class SavedRequestFilter implements Filter {
    public void init(FilterConfig config) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // First check whether there is a saved request by looking for
        // a "shiroPostRedirect" request parameter.
        if (request.getParameter("shiroPostRedirect") != null) {
            // Replace this request with the saved one, if the session
            // contains a saved request.
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session != null) {
                Object savedRequest = session.getAttribute("shiroGrailsSavedRequest");
                if (savedRequest != null) {
                    // Found a saved request, so substitute it for the
                    // current one and then remove it from the session.
                    request = (ServletRequest) savedRequest;
                    session.removeAttribute("shiroGrailsSavedRequest");
                }
            }
        }

        // Continue on to the next filter in the chain.
        chain.doFilter(request, response);
    }
}
