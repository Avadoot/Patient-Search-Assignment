package com.tests;

import com.tests.utils.BaseExcel;
import com.tests.utils.Wait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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
    public String[][] requestData;

    @BeforeTest
    public  void BeforeTest(){
        WebDriverManager.chromedriver().version("87").setup();

        System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");

        HashMap<String, Object> chromeLocalStatePref = new HashMap<>();
        chromeLocalStatePref.put("download.default_directory", downloadFolderPath);
        chromeLocalStatePref.put("download.prompt_for_download", false);

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
        waitAndClick(webDriverWait, driver, By.xpath("//*[contains(@onclick,'validateSearchClicked')]"));
        int i = 1;

        for (int j = 0; j < 1; j++) {
            Thread.sleep(1000);
            String names;
            Actions actions = new Actions(driver);
            for (WebElement element : driver.findElements(By.xpath("//*[@id='patientList']//*/span[2]"))) {
                System.out.println(element.getText());
                actions.moveToElement(element).click().build().perform();
                driver.switchTo().defaultContent();
                driver.switchTo().frame("fraCureMD_Body");
                Thread.sleep(800);
                try {
                    names = wait.waitAndGetText(webDriverWait, driver, By.cssSelector(".patientDetails"));
                } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                    names = wait.waitAndGetText(webDriverWait, driver, By.cssSelector(".patientDetails"));
                }
                driver.switchTo().defaultContent();
                String[] namesList = names.split(",");
                System.out.println(names);
                driver.switchTo().frame("fraCureMD_Patient_Menu");
                driver.switchTo().frame("iFrameList");
                excel.writeExcel(filepath, 0, 0, i, element.getText());
                excel.writeExcel(filepath, 0, 1, i, namesList[2].split(":")[1]);
                excel.writeExcel(filepath, 0, 2, i, namesList[0]);
                excel.writeExcel(filepath, 0, 3, i, namesList[1]);
                i++;

            }
            webDriverWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.cssSelector(".paginate_button.next"))));
            waitAndClick(webDriverWait, driver, By.cssSelector(".paginate_button.next"));
            j++;
        }
        driver.switchTo().defaultContent();

        driver.switchTo().frame("fraCureMD_Menu");
        waitAndClick(webDriverWait, driver, By.id("patientBtn"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame("fraCureMD_Body");
        waitAndClick(webDriverWait, driver, By.xpath("//*[@title='Search Patient']"));
        requestData = BaseExcel.readExcel(filepath, 0);
        waitAndSendKeys(webDriverWait, driver, By.xpath("//*[@name='BaseIntelliSenseControl1$txtField']"),
               requestData[6][0] );
        driver.findElement(By.xpath("//*[@name='BaseIntelliSenseControl1$txtField']")).sendKeys(Keys.ENTER);
        Thread.sleep(1000);
        waitAndClick(webDriverWait,driver,By.xpath("//*[contains(@id,'anchorPatientName')]"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame("fraCureMD_Patient_Menu");
        waitAndClick(webDriverWait,driver,By.id("webfx-tree-object-43-anchor"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame("fraCureMD_Body");
        driver.switchTo().frame("CustomFolders");
        waitAndClick(webDriverWait,driver,By.xpath("//*[text()='1Old Records (0)']"));

        driver.switchTo().defaultContent();
        driver.switchTo().frame("fraCureMD_Body");
        driver.switchTo().frame("Thumbs");
        waitAndClick(webDriverWait,driver,By.xpath("//*[text()='All Documents']"));
        waitAndClick(webDriverWait,driver,By.xpath("//*[@id='chkAll']"));
        waitAndClick(webDriverWait,driver,By.xpath("//*[@id='downloadBtn']/a"));
        wait.moveToNewFrame(driver);
        Thread.sleep(7000);
        System.out.println("Success");

    }

    @AfterTest
    public void AfterTest() {
        driver.quit();
    }
}