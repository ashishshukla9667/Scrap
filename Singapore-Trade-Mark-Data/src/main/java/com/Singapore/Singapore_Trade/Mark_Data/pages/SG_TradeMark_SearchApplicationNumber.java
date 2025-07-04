package com.Singapore.Singapore_Trade.Mark_Data.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class SG_TradeMark_SearchApplicationNumber {

    //Enter Application in the inbox
    public void enterApplicationNumber(WebDriverWait wait, String appNumber){

        System.out.println();
        WebElement inbox = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='appNoTm0']")));
        inbox.clear();
        inbox.sendKeys(appNumber);

        searchApplicationNumber(wait);
    }

    public void searchApplicationNumber(WebDriverWait wait){

        WebElement searchButton = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='searchBtnTrademark']"))
        );
        searchButton.click();

        captchaAppears(wait);

    }

    //Captcha Solver
    public void captchaAppears(WebDriverWait wait){

    }

    //Click Application number if present
    public static boolean clickApplicationNumber(WebDriverWait wait, String applicationNumber) {
        if (applicationNumber == null || applicationNumber.trim().isEmpty()) {
            return false;
        }

        try {
            // Wait for the application number element to be visible
            WebElement appNoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//td[@data-title='Application No']/a")
            ));

            String actualAppNumber = appNoElement.getText().trim();

            if (actualAppNumber.equals(applicationNumber)) {
                appNoElement.click();
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            // If element not found or any error occurs
            return false;
        }
    }




}
