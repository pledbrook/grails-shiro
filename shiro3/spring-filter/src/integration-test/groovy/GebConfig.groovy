/**
 * User: pmcneil
 * Date: 9/09/13
 *
 */
/*
This is the Geb configuration file.
See: http://www.gebish.org/manual/current/configuration.html
*/

import org.openqa.selenium.firefox.FirefoxDriver
//import org.openqa.selenium.chrome.ChromeDriver

def url = System.env['geb.build.baseUrl'] ?: "http://localhost:8080/"
baseUrl = url
reportsDir = "build/geb-reports" 
driver = { new FirefoxDriver() }

environments {

// run as “grails -Dgeb.env=chrome test-app”
// See: http://code.google.com/p/selenium/wiki/ChromeDriver
// chrome {
//     driver = {
//         System.setProperty('webdriver.chrome.driver', '/home/pmcneil/devel/chromedriver/chromedriver')
//         new ChromeDriver()
//     }
// }

// run as “grails -Dgeb.env=firefox test-app”
// See: http://code.google.com/p/selenium/wiki/FirefoxDriver
    // firefox {
    //     driver = { new FirefoxDriver() }
    // }
}
