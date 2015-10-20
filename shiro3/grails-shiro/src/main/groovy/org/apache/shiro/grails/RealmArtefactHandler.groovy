/*
 * Copyright 2007 Peter Ledbrook.
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
 * Modified 2009 Bradley Beddoes, Intient Pty Ltd, Ported to Apache Ki
 * Modified 2009 Kapil Sachdeva, Gemalto Inc, Ported to Apache Shiro
 * Modified 2015 Yellowsnow, Arkilog, Migrated to Grails 3
 */
package org.apache.shiro.grails

import grails.core.ArtefactHandlerAdapter
import java.util.regex.Pattern
import org.grails.compiler.injection.GrailsASTUtils

import org.codehaus.groovy.ast.ClassNode
import static org.grails.io.support.GrailsResourceUtils.GRAILS_APP_DIR
import static org.grails.io.support.GrailsResourceUtils.REGEX_FILE_SEPARATOR

@SuppressWarnings("unchecked")
class RealmArtefactHandler extends ArtefactHandlerAdapter {
    public static final String TYPE = "Realm"
    public static Pattern REALM_PATH_PATTERN = 
    	Pattern.compile(".+" + REGEX_FILE_SEPARATOR + GRAILS_APP_DIR + REGEX_FILE_SEPARATOR + "realms" + REGEX_FILE_SEPARATOR + "(.+)\\.(groovy)")

    RealmArtefactHandler() {
        super(TYPE, RealmGrailsClass.class, DefaultRealmGrailsClass.class, null)
    }

    boolean isArtefact(ClassNode classNode) {
        if(classNode == null ||
           !isValidArtefactClassNode(classNode, classNode.getModifiers()) ||
           !classNode.getName().endsWith(TYPE)) {
            return false
        }

        URL url = GrailsASTUtils.getSourceUrl(classNode)

        url &&  REALM_PATH_PATTERN.matcher(url.getFile()).find()
    }

    boolean isArtefactClass(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz != null && clazz.name.endsWith(TYPE)
    }
}
