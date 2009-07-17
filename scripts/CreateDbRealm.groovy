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
 */
import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

Ant.property(environment: 'env')
grailsHome = Ant.antProject.properties.'env.GRAILS_HOME'

includeTargets << new File ("${grailsHome}/scripts/Init.groovy")
includeTargets << new File ("${shiroPluginDir}/scripts/_ShiroInternal.groovy")

target ('default': 'Creates a database Shiro realm') {
    // Make sure any arguments have been parsed if the parser is available.
    hasArgsParser = getBinding().variables.containsKey('argsMap')
    if (hasArgsParser) {
        depends(parseArguments, checkVersion)
    }
    else {
        depends(checkVersion)
    }

    createDbRealm()
}

target('createDbRealm': 'Creates a basic, but flexible database-backed realm.') {
    // Get the prefix for the realm name. Default is "Shiro" to avoid name conflicts.
    def prefix = "Shiro"
    if (hasArgsParser && argsMap['prefix'] != null) {
        prefix = argsMap['prefix']
    }

    // First create the domain objects: ShiroUser, ShiroRole, etc.
    def domainClasses = [
        'User',
        'Role',
        'Permission',
        'RolePermissionRel',
        'UserRoleRel',
        'UserPermissionRel' ]

    def artefactPath = "grails-app/domain"
    Ant.mkdir(dir:"${basedir}/${artefactPath}")

    domainClasses.each { domainClass ->
        installTemplateEx("${prefix}${domainClass}.groovy", artefactPath, "domain", "Shiro${domainClass}.groovy") {
            Ant.replace(file: artefactFile) {
                Ant.replacefilter(token: '@domain.prefix@', value: prefix)
            }
        }
        event("CreatedArtefact", ['', domainClass])
    }

    // Copy over the standard DB realm.
    def className = "${prefix}DbRealm"
    installTemplateEx("${className}.groovy", "grails-app/realms", "", "ShiroDbRealm.groovy") {
        Ant.replace(file: artefactFile) {
            Ant.replacefilter(token: '@realm.name@', value: className)
            Ant.replacefilter(token: '@domain.prefix@', value: prefix)
        }
    }

    event("CreatedArtefact", ['Realm', className])
}
