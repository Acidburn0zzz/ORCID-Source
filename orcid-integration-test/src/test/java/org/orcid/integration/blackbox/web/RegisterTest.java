/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.blackbox.web;

import static org.junit.Assert.assertEquals;
import static org.orcid.integration.blackbox.api.BBBUtil.executeJavaScript;
import static org.orcid.integration.blackbox.api.BBBUtil.findElement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.orcid.core.togglz.Features;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class RegisterTest extends BlackBoxBase {

    @Value("${org.orcid.web.baseUri}")
    public String baseUri;

    @Test
    public void testActivitiesVisibilityOnSigninPage() {
        boolean previousState = getTogglzFeatureState(getAdminUserName(), getAdminPassword(), Features.GDPR_UI); // TODO remove after GDPR UI is live
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, false);
        
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        By element = By.xpath("//ul[@class='privacyToggle']");
        WebElement we = findElement(element);
        assertEquals("PUBLIC", executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we).toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, true);
        
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        element = By.xpath("//div[@class='visibilityDefault']");
        we = findElement(element);
        assertEquals(null, executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we));
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, previousState); // TODO remove after GDPR UI is live
    }

    @Test
    public void testActivitiesVisibilityOnRegisterPage() {
        boolean previousState = getTogglzFeatureState(getAdminUserName(), getAdminPassword(), Features.GDPR_UI); // TODO remove after GDPR UI is live
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, false);
        webDriver.get(baseUri + "/register");
        By element = By.xpath("//ul[@class='privacyToggle']");
        WebElement we = findElement(element);
        assertEquals("PUBLIC", executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we).toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, true);
        webDriver.get(baseUri + "/register");
        element = By.xpath("//div[@class='visibilityDefault']");
        we = findElement(element);
        assertEquals(null, executeJavaScript("return angular.element(arguments[0]).scope().registrationForm.activitiesVisibilityDefault.visibility", we));
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, previousState); // TODO remove after GDPR UI is live
    }

    @Test
    public void testSubmitRegistrationFormWithNullDefaultVisibility() {
        // turn recaptcha off
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.SHOW_RECAPTCHA, false);
        
        boolean previousState = getTogglzFeatureState(getAdminUserName(), getAdminPassword(), Features.GDPR_UI);
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, true); // TODO remove after GDPR UI is live
        webDriver.get(baseUri + "/register");
        
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.id("register-form-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-confirm-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));
        
        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        assertEquals(baseUri + "/register", webDriver.getCurrentUrl());
        assertEquals("Please choose a default visibility setting.", ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.errors[0]").toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, false);
        webDriver.get(baseUri + "/register");
       
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.id("register-form-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-confirm-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));

        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        // still on same page
        assertEquals(baseUri + "/register", webDriver.getCurrentUrl());
        
        assertEquals("Please choose a default visibility setting.", ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.errors[0]").toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, previousState); // TODO remove after GDPR UI is live
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.SHOW_RECAPTCHA, true);
    }
    
    @Test
    public void testSubmitRegistrationFormWithNullDefaultVisibilityFromSigninPage() {
        // turn recaptcha off
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.SHOW_RECAPTCHA, false);
        
        boolean previousState = getTogglzFeatureState(getAdminUserName(), getAdminPassword(), Features.GDPR_UI);
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, true); // TODO remove after GDPR UI is live
       
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.id("register-form-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-confirm-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));
        
        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        assertEquals(baseUri + "/signin", webDriver.getCurrentUrl());
        assertEquals("Please choose a default visibility setting.", ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.errors[0]").toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, false);
        
        webDriver.get(baseUri + "/signin");
        webDriver.findElement(By.id("switch-to-register-form")).click();
        
        findElement(By.id("register-form-given-names")).sendKeys("test-first-name");
        findElement(By.id("register-form-family-name")).sendKeys("test-last-name");
        findElement(By.id("register-form-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-confirm-email")).sendKeys("test-null-default-visibility@orcid.org");
        findElement(By.id("register-form-password")).sendKeys("password123");
        findElement(By.id("register-form-confirm-password")).sendKeys("password123");

        ((JavascriptExecutor) webDriver).executeScript("angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility=null;");
        assertEquals(null, ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.activitiesVisibilityDefault.visibility"));

        findElement(By.id("register-form-term-box")).click();
        findElement(By.id("register-authorize-button")).click();
        
        // still on same page
        assertEquals(baseUri + "/signin", webDriver.getCurrentUrl());
        
        assertEquals("Please choose a default visibility setting.", ((JavascriptExecutor) webDriver).executeScript("return angular.element('[ng-controller=OauthAuthorizationController]').scope().registrationForm.errors[0]").toString());

        toggleFeature(getAdminUserName(), getAdminPassword(), Features.GDPR_UI, previousState); // TODO remove after GDPR UI is live
        toggleFeature(getAdminUserName(), getAdminPassword(), Features.SHOW_RECAPTCHA, true);
    }

}
