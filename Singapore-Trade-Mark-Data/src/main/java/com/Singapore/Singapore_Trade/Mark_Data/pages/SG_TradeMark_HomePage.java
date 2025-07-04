package com.Singapore.Singapore_Trade.Mark_Data.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;

@SuppressWarnings("All")
@Component
public class SG_TradeMark_HomePage {

    //click Trade Mark Tab
    public void clickTradeMarkTab(WebDriver driver){

        WebElement tradeMarkTab = driver.findElement(By.id("tmTab"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tradeMarkTab);

        removeExtraInbox(driver);
    }

    private void removeExtraInbox(WebDriver driver){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Get all <conditionrow> elements
        List<WebElement> conditionRows = driver.findElements(By.tagName("conditionrow"));

        // Proceed only if more than one exists
        if (conditionRows.size() > 1) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "const container = document.getElementById('searchCriteriaDivTm');" +
                            "if (container) {" +
                            "  const rows = container.getElementsByTagName('conditionrow');" +
                            "  const rowsArray = Array.from(rows);" +
                            "  rowsArray.slice(1).forEach(row => row.remove());" +
                            "}"
            );
            System.out.println("Extra <conditionrow> elements removed.");
        } else {
            System.out.println("Only one <conditionrow> present. No action taken.");
        }

        clickApplicationNumber(driver);
    }

    //click on Application / International Application / International Registration / Case No. Link
    private void clickApplicationNumber(WebDriver driver ){

        WebElement clickNumber = driver.findElement(By.xpath("//span[@id='appNoLnkTm']"));
        clickNumber.click();
    }

}
