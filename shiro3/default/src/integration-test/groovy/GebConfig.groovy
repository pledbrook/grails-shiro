/**
 * User: pmcneil
 * Date: 9/09/13
 *
 */
/*
This is the Geb configuration file.
See: http://www.gebish.org/manual/current/configuration.html
*/
//It's using htmlunit 
reportsDir = "build/geb-reports" 
def url = System.env['geb.build.baseUrl'] ?: "http://localhost:8080/"
baseUrl = url
