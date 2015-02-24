/*
 * Copyright 2015 Yellowsnow.
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
 *
 *
 *
 */
package org.apache.shiro.grails

import org.apache.shiro.SecurityUtils
import org.apache.shiro.subject.Subject
import org.apache.shiro.util.ThreadContext

/**
 * 
 * Sometimes we need to mock an authenticated principal in our grails tests, this class serves that exact purpose.
 *
 */

class MockUtils {
    /**
    * Mock an authenticated principal
    * 
    * @principal The shiro principal object, defaults to 'testUser'
    */
    static mockAuthenticatedPrincipal(principal = 'testUser'){
        def subject = [ getPrincipal: { principal },
                        isAuthenticated: { true }
                      ] as Subject
        ThreadContext.put( ThreadContext.SECURITY_MANAGER_KEY,
                            [ getSubject: { subject } ] as SecurityManager )
        SecurityUtils.metaClass.static.getSubject = { subject }
    }
}
