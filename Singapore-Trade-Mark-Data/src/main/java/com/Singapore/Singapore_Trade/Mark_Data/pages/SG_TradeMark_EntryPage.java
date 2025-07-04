package com.Singapore.Singapore_Trade.Mark_Data.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

@SuppressWarnings("All")
@Component
public class SG_TradeMark_EntryPage {

    //Visit the https://digitalhub.ipos.gov.sg/FAMN/eservice/IP4SG/MN_AdvancedSearch?OWASP_CSRFTOKEN=HJ3G-HT8R-1FY1-1SOA-44YW-2P9E-ATPL-HMCT
    public void sgTradeMarkSearch(WebDriver driver, String url) throws InterruptedException {
        driver.get(url);

        Thread.sleep(5000);
        clickAdvancedSearch(driver);
    }

    //Click Advanced Tab
    private void clickAdvancedSearch(WebDriver driver){
        WebElement advancedSearchButton = driver.findElement(By.xpath("//a[text()='Advanced Search']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", advancedSearchButton);
    }

}
