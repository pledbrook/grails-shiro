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
import static org.apache.shiro.grails.ShiroCodeGenUtils.*

description("Creates a new database realm from a template.") {
	usage "grails create-db-realm [--prefix=PREFIX]"
	flag name:'PREFIX', description:"""The prefix to add to the names of the realm and domain classes.
             This may include a package. (default: "Shiro").
"""
}
/**
 * Creates a new database realm from a template.
 */

def (pkg, prefix) = parsePrefix(argsMap)

// First create the domain objects: ShiroUser, ShiroRole, etc.
def domainClasses = [
    'User',
    'Role',
    'Permission',
    'RolePermissionRel',
    'UserRoleRel',
    'UserPermissionRel' ]


/**
 * Creates a new database realm from a template that only works with
 * database permissions. Other types of permission are not supported.
 */

def artefactPath = "grails-app/domain${packageToPath(pkg)}"
ant.mkdir(dir: "${baseDir}/${artefactPath}")

domainClasses.each { domainClass ->
	def m = [:]
	m['packageLine'] = (pkg ? "package ${pkg}\n\n" : "")
	m['domainPrefix'] = prefix
	render  template:"artifacts/domain/Shiro${domainClass}.groovy",
	        destination: file("${artefactPath}/${packageToPath(pkg)}/${prefix}${domainClass}.groovy"),
	        model:m
}

// Copy over the standard DB realm.
def className = "${prefix}DbRealm"
def m = [:]
m['packageLine'] = (pkg ? "package ${pkg}\n\n" : "")
m['realmName'] = className
m['domainPrefix'] = prefix
render  template:"artifacts/realms/ShiroDbRealm.groovy",
        destination: file("grails-app/realms/${packageToPath(pkg)}/${className}.groovy"),
        model:m
 