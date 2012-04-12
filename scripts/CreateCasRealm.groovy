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

includeTargets << grailsScript("_GrailsArgParsing")
includeTargets << new File("${shiroPluginDir}/scripts/_ShiroInternal.groovy")

USAGE = """
    create-cas-realm [--prefix=PREFIX]

where
    PREFIX = The prefix to add to the name of the realm. This may include a
             package. (default: "Shiro").
"""

target (default: "Creates a CAS Shiro realm") {
    // Make sure any arguments have been parsed.
    depends(parseArguments, createCasRealm)
}
