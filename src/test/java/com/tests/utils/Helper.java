package com.tests.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.tests.ExampleTest.downloadFolderPath;
import static com.tests.utils.Wait.waitAndClick;
import static com.tests.utils.Wait.waitAndSendKeys;

public class Helper {

    public Wait wait = new Wait();
    public BaseExcel excel = new BaseExcel();
    public String[][] requestData;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
    private static String startTime;

    public void getLoginToTheSystem(WebDriverWait webDriverWait, WebDriver driver) throws Exception {
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
    }

    public void savePatientDataToExcel(WebDriverWait webDriverWait, WebDriver driver, String filepath) throws InterruptedException {
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
    }

    public void readPatientNameFromExcelDownloadDocs(WebDriverWait webDriverWait, WebDriver driver, String filepath) throws InterruptedException, IOException {

        requestData = BaseExcel.readExcel(filepath, 0);
        startTime = getCurrentTime();

        for (int k = 1; k < requestData.length; k++) {
            driver.switchTo().defaultContent();

            driver.switchTo().frame("fraCureMD_Menu");
            waitAndClick(webDriverWait, driver, By.id("patientBtn"));
            driver.switchTo().defaultContent();
            driver.switchTo().frame("fraCureMD_Body");
            waitAndClick(webDriverWait, driver, By.xpath("//*[@title='Search Patient']"));
            waitAndSendKeys(webDriverWait, driver, By.xpath("//*[@name='BaseIntelliSenseControl1$txtField']"),
                    requestData[k][0]);
            driver.findElement(By.xpath("//*[@name='BaseIntelliSenseControl1$txtField']")).sendKeys(Keys.ENTER);
            Thread.sleep(1000);
            waitAndClick(webDriverWait, driver, By.xpath("//*[contains(@id,'anchorPatientName')]"));
            driver.switchTo().defaultContent();
            driver.switchTo().frame("fraCureMD_Patient_Menu");
            waitAndClick(webDriverWait, driver, By.id("webfx-tree-object-43-anchor"));
            driver.switchTo().defaultContent();
            driver.switchTo().frame("fraCureMD_Body");
            driver.switchTo().frame("CustomFolders");
            waitAndClick(webDriverWait, driver, By.xpath("//*[contains(text(),'1Old Records')]"));

            driver.switchTo().defaultContent();
            driver.switchTo().frame("fraCureMD_Body");
            driver.switchTo().frame("Thumbs");
            waitAndClick(webDriverWait, driver, By.xpath("//*[text()='All Documents']"));
            Thread.sleep(1000);

            String path = "DataOutput\\" + startTime;

            if (!driver.findElement(By.className("no_record_p")).isDisplayed()) {
                waitAndClick(webDriverWait, driver, By.xpath("//*[@id='chkAll']"));
                waitAndClick(webDriverWait, driver, By.xpath("//*[@id='downloadBtn']/a"));
                Thread.sleep(7000);
                downloadFiles(path, requestData[k][0], requestData[k][1]);
                System.out.println("Record found for " + requestData[k][0] + " and downloaded successfully");
            } else
                System.out.println("No document found for " + requestData[k][0]);
        }
    }

    public static String getCurrentTime() {
        return simpleDateFormat.format(Calendar.getInstance().getTimeInMillis());
    }

    public static void downloadFiles(String pathName, String patientName, String patientBirthDate) throws IOException, InterruptedException {
        File newFile = new File(pathName + "\\" + patientName + "-" + patientBirthDate.replace("/", "-") + "\\");
        newFile.mkdirs();
        Thread.sleep(2000);
        FileUtils.moveFileToDirectory(getLastModified(downloadFolderPath),
                new File(String.valueOf(newFile)),false);

    }

    public static File getLastModified(String directoryFilePath)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;

        if (files != null)
        {
            for (File file : files)
            {
                if (file.lastModified() > lastModifiedTime)
                {
                    chosenFile = file;
                    lastModifiedTime = file.lastModified();
                }
            }
        }

        return chosenFile;
    }
}
