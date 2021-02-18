package com.tests;

import com.tests.utils.BaseExcel;
import com.tests.utils.Wait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.security.PublicKey;
import java.util.HashMap;

import static com.tests.utils.Wait.waitAndClick;
import static com.tests.utils.Wait.waitAndSendKeys;

public class ExampleTest {
    public static WebDriver driver;
    public static ChromeOptions chromeOptions = new ChromeOptions();
    public static String downloadFolderPath = System.getProperty("user.dir") + "\\testDataOutput";
    public static int waitTime = 45;
    public static WebDriverWait webDriverWait;
    static Wait wait = new Wait();
    public String filepath = "TestDataTemplate.xlsx";
    public BaseExcel excel = new BaseExcel();

    @Test
    public void Test() throws Exception {
        WebDriverManager.chromedriver().version("87").setup();

        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
        chromeOptions.addArguments("window-size=1280x1024");

        HashMap<String, Object> chromeLocalStatePref = new HashMap<>();
        chromeLocalStatePref.put("download.default_directory", downloadFolderPath);

        driver = new ChromeDriver(chromeOptions);
        webDriverWait = new WebDriverWait(driver, waitTime);

        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("window-size=1280x1024");
        chromeOptions.addArguments("--no-sandbox");

        driver.manage().window().maximize();
        driver.get("https://cloud4.curemd.com/");
        String expectedPageTitle = "Welcome to CureMD";
        Assert.assertTrue(driver.getTitle().contains(expectedPageTitle), "Test Failed");

        waitAndSendKeys(webDriverWait, driver, By.id("vchLogin_Name"), "Absharma");
        waitAndSendKeys(webDriverWait, driver, By.id("vchPassword"), "Sharmaabb");
        waitAndClick(webDriverWait, driver, By.cssSelector(".btn-danger"));
        Thread.sleep(1000);
        wait.moveToNewFrame(driver);
        Thread.sleep(4000);
        wait.waitUntilIsPresent(webDriverWait, By.xpath("//*[text()='Not now']"));
        waitAndClick(webDriverWait, driver, By.xpath("//*[text()='Not now']"));

        Thread.sleep(3000);
        driver.switchTo().frame(0);
        wait.safeJavaScriptClick(driver.findElement(By.xpath("//*[@title='Document Manager']")), driver);

        driver.switchTo().defaultContent();
        Thread.sleep(5000);
        wait.waitUntilIsPresent(webDriverWait, By.id("divCureMDPatientMenu"));
        driver.switchTo().frame("fraCureMD_Patient_Menu");
        driver.switchTo().frame("iFrameList");

        for (int i = 0; i < driver.findElements(By.xpath("//*[@id='patientList']//*/span[2]")).size(); i++) {
            for (WebElement element : driver.findElements(By.xpath("//*[@id='patientList']//*/span[2]"))) {
                System.out.println(element.getText());

                excel.writeExcel(filepath, 0, 0, i, element.getText());
            }
        }

        driver.switchTo().defaultContent();
        System.out.println("Success");

    }

    @AfterTest
    public void AfterTest() {
        driver.quit();
    }
}