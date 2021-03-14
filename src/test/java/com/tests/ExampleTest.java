package com.tests;

import com.tests.utils.Helper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;

public class ExampleTest {
    public static WebDriver driver;
    public static ChromeOptions chromeOptions = new ChromeOptions();
    public static String downloadFolderPath = System.getProperty("user.dir") + "\\testDataOutput\\";
    public static int waitTime = 45;
    public static WebDriverWait webDriverWait;
    public String filepath = "TestDataTemplate.xlsx";
    public static Helper helper = new Helper();

    @BeforeTest
    public void BeforeTest() {
        WebDriverManager.chromedriver().version("89").setup();

        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        HashMap<String, Object> chromeLocalStatePref = new HashMap<>();
        chromeLocalStatePref.put("download.default_directory", downloadFolderPath);
        chromeLocalStatePref.put("download.prompt_for_download", true);

        chromeOptions.setExperimentalOption("localState", chromeLocalStatePref);
        chromeOptions.setExperimentalOption("prefs", chromeLocalStatePref);

        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("window-size=1280x1024");
        chromeOptions.addArguments("--no-sandbox");

        driver = new ChromeDriver(chromeOptions);
        webDriverWait = new WebDriverWait(driver, waitTime);

        driver.manage().window().maximize();
        driver.get("https://cloud4.curemd.com/");
        String expectedPageTitle = "Welcome to CureMD";
        Assert.assertTrue(driver.getTitle().contains(expectedPageTitle), "Test Failed");

    }

    @Test
    public void Test() throws Exception {

        //Get login to the system application
        helper.getLoginToTheSystem(webDriverWait, driver);

        //Save all patient data to excel, includes patient name, age, gender and birth date
        helper.savePatientDataToExcel(webDriverWait, driver, filepath);

        //Download available documents for each patient
        helper.readPatientNameFromExcelDownloadDocs(webDriverWait, driver, filepath);
    }

    @AfterTest
    public void AfterTest() {
        driver.quit();
    }
}