/**
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
 
class ShiroCodeGenUtils {

	static parsePrefix(argsMap) {
        def prefix = "Shiro"
        def pkg = ""
        if (argsMap.grep{it.key=='PREFIX'}) {
            if (argsMap['PREFIX'] && argsMap['PREFIX']?.indexOf('.')){
                def givenValue = argsMap["PREFIX"].split(/\./, -1)
                prefix = givenValue[-1]
                pkg = givenValue.size() > 1 ? givenValue[0..-2].join('.') : ""
            }
        }

	    return [ pkg, prefix ]
	}

	/**
	 * Converts a package name (with '.' separators) to a file path (with
	 * '/' separators). If the package is <tt>null</tt>, this returns an
	 */
	static packageToPath(String pkg) {
	    return pkg ? '/' + pkg.replace('.' as char, '/' as char) : ''
	}
}
