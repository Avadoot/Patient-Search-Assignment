package com.tests.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wait {

    public static void waitAndSendKeys(WebDriverWait webDriverWait, WebDriver driver, By by, String keys) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        driver.findElement(by).sendKeys(keys);
    }

    public static void waitAndClick(WebDriverWait webDriverWait, WebDriver driver, By by) {
        webDriverWait.until(ExpectedConditions.elementToBeClickable(by));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(by).click();
    }
    public static void waitAndClickElement(WebDriverWait webDriverWait, WebElement element) {
        webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        element.click();
    }
    public void moveToNewFrame(WebDriver driver) {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    public WebElement longWaitAndReturnElement(WebDriverWait webDriverWait, WebDriver driver, By by) {
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(by));
        WebElement element = driver.findElement(by);
        return element;
    }

    public Boolean waitUntilIsPresent(WebDriverWait webDriverWait, By by) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return null;
    }

    public void getTheHandleOf(WebDriver driver) {
        driver.switchTo().activeElement();
    }

    public void safeJavaScriptClick(WebElement element, WebDriver driver) throws Exception {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public String waitAndGetText(WebDriverWait webDriverWait, WebDriver driver, By by) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        String text = driver.findElement(by).getText();
        return text;
    }
}