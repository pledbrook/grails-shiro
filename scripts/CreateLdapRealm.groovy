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

Ant.property(environment: 'env')
grailsHome = Ant.antProject.properties.'env.GRAILS_HOME'

includeTargets << new File ("${grailsHome}/scripts/Init.groovy")

target ('default': 'Creates a LDAP Shiro realm') {
    // Make sure any arguments have been parsed if the parser is available.
    def hasArgsParser = getBinding().variables.containsKey('argsMap')
    if (hasArgsParser) {
        depends(parseArguments, checkVersion)
    }
    else {
        depends(checkVersion)
    }

    // Get the prefix for the realm name. Default is "Shiro" to avoid name conflicts.
    def prefix = "Shiro"
    if (hasArgsParser && argsMap['prefix'] != null) {
        prefix = argsMap['prefix']
    }

    // Copy over the standard LDAP realm.
    def className = "${prefix}LdapRealm".toString()
    def artefactPath = 'grails-app/realms'
    def artefactFile = "${basedir}/${artefactPath}/${className}.groovy"
    if (new File(artefactFile).exists()) {
        Ant.input(
            addProperty: "${args}.${className}.overwrite",
            message: "${className} already exists. Overwrite? [y/n]")

        if (Ant.antProject.properties."${args}.${className}.overwrite" == "n") {
            return
        }
    }

    // Copy the template file to the 'grails-app/realms' directory.
    templateFile = "${kiPluginDir}/src/templates/artifacts/ShiroLdapRealm.groovy"
    if (!new File(templateFile).exists()) {
        Ant.echo("[Shiro plugin] Error: src/templates/artifacts/ShiroLdapRealm.groovy does not exist!")
        return
    }

    Ant.copy(file: templateFile, tofile: artefactFile, overwrite: true)
    Ant.replace(file: artefactFile) {
        Ant.replacefilter(token: '@realm.name@', value: className)
    }

    event("CreatedFile", [artefactFile])
    event("CreatedArtefact", ['Realm', className])
}
