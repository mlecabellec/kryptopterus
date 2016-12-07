/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.booleanworks.kryptopterus.selenium.testsuite001;

import java.util.concurrent.TimeUnit;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author vortigern
 */
public class BaseWelcomePageTest {
    
    @Test
    @Ignore
    public void testSimple() throws Exception {
        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        
       DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("phantomjs.page.settings.userAgent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/53.0.2785.143 Chrome/53.0.2785.143 Safari/537.36");
        capabilities.setCapability("phantomjs.page.settings.localToRemoteUrlAccessEnabled", true);
        capabilities.setCapability("phantomjs.page.settings.browserConnectionEnabled", true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/usr/local/bin/phantomjs");
        capabilities.setCapability("takesScreenshot", true);

        WebDriver driver= new PhantomJSDriver((Capabilities) capabilities);
        driver.manage().window().setSize(new Dimension(1200,800));
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
        
        
        

        // And now use this to visit NetBeans
        driver.get("http://localhost:8084/kryptopterus/");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("http://www.netbeans.org");

        // Check the title of the page
        // Wait for the page to load, timeout after 10 seconds
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver d) {
                return d.getTitle().contains("NetBeans");
            }
        });

        //Close the browser
        driver.quit();
    }
    
}
