package com.Singapore.Singapore_Trade.Mark_Data.service;

import com.Singapore.Singapore_Trade.Mark_Data.model.SG_TradeMark_Data;
import com.Singapore.Singapore_Trade.Mark_Data.pages.SG_TradeMark_EntryPage;
import com.Singapore.Singapore_Trade.Mark_Data.pages.SG_TradeMark_HomePage;
import com.Singapore.Singapore_Trade.Mark_Data.pages.SG_TradeMark_SGApplicationData;
import com.Singapore.Singapore_Trade.Mark_Data.pages.SG_TradeMark_SearchApplicationNumber;
import com.Singapore.Singapore_Trade.Mark_Data.util.SG_TradeMark_Util;
import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
@Service
public class SG_TradeMark_Service {

    private  WebDriver driver;
    WebDriverWait wait;

    //Pages
    SG_TradeMark_EntryPage sgTradeMarkEntryPage;
    SG_TradeMark_HomePage sgTradeMarkHomePage;
    SG_TradeMark_SearchApplicationNumber sgTradeMarkSearchApplicationNumber;
    SG_TradeMark_SGApplicationData sgTradeMarkSgApplicationData;


    private static int index=0;
    private final static String filePath = "input/RPA 2.0.xlsx";
    private final static  String url = "https://digitalhub.ipos.gov.sg/";


    public SG_TradeMark_Service(WebDriver driver ,SG_TradeMark_EntryPage sgTradeMarkEntryPage ,  SG_TradeMark_HomePage sgTradeMarkHomePage , SG_TradeMark_SearchApplicationNumber sgTradeMarkSearchApplicationNumber , SG_TradeMark_SGApplicationData sgTradeMarkSgApplicationData ) {
        this.driver = driver;
        this.sgTradeMarkEntryPage=sgTradeMarkEntryPage;
        this.sgTradeMarkHomePage = sgTradeMarkHomePage;
        this.sgTradeMarkSearchApplicationNumber = sgTradeMarkSearchApplicationNumber;
        this.sgTradeMarkSgApplicationData=sgTradeMarkSgApplicationData;
    }

    //Extracted Keywords
    String applicationNumber;
    String mark;
    String expiryDate;
    String classes;
    String ownerName;
    String ownerAddress;

    List<SG_TradeMark_Data>output;
    SG_TradeMark_Data sgTradeMark;

    @PostConstruct
    public ResponseEntity<List<SG_TradeMark_Data>> runSGTradeMark(){

        output = new ArrayList<>();
        try{
            System.out.println();

            //input is extracted from the input excel file
            List<String> applicationNumber = SG_TradeMark_Util.getApplicationNumbersForSG(filePath);

            for (String appNumber : applicationNumber) {

                System.out.println("Data Number : " + ++index);
                scrapeByApplicationnNumber("40201502203Y");
            }
        }
        catch (Exception e){
            System.out.println("Exception Occured : " + e.getMessage());
        }

        output.add(sgTradeMark);
        System.out.println(output);
        return ResponseEntity.ok(output);
    }

    private void scrapeByApplicationnNumber(String appNumber) throws Exception {

        Thread.sleep(1000);

        //Web page from where we navigate to Home Page that is sgTradeMarkHomePage
        sgTradeMarkEntryPage.sgTradeMarkSearch(driver,url);
        Thread.sleep(1000);

        //Main Page where we enter the input
        sgTradeMarkHomePage.clickTradeMarkTab(driver);
        Thread.sleep(1000);


        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        //in this page we enter Application Number
        sgTradeMarkSearchApplicationNumber.enterApplicationNumber(wait ,appNumber);
        Thread.sleep(50000);

        //here we check whether this page has Application Number Data or not
        if(sgTradeMarkSearchApplicationNumber.clickApplicationNumber(wait,appNumber)){

            Thread.sleep(1000);
            switchToNewTab(driver);
//            Thread.sleep(5000);
//            applicationNumber  = sgTradeMarkSgApplicationData.getApplicationNumber(driver ,wait);
//            Thread.sleep(2000);
//            classes = sgTradeMarkSgApplicationData.getClasses(driver ,wait);
//            Thread.sleep(1000);
//            expiryDate = sgTradeMarkSgApplicationData.getExpiryDate(driver ,wait);
//            Thread.sleep(1000);
//            ownerName = sgTradeMarkSgApplicationData.getOwnerName(driver ,wait);
//            Thread.sleep(1000);
//            ownerAddress = sgTradeMarkSgApplicationData.getOwnerAddress(driver ,wait);
//            Thread.sleep(1000);
//            Thread.sleep(1000);
            mark = sgTradeMarkSgApplicationData.extractTrademarkText(applicationNumber,driver,wait);

            //sgTradeMarkSgApplicationData.printData(driver, wait);

        }
        else{
            System.out.println(appNumber + " Data is not Present");
        }
        //driver.quit();
        System.out.println();


    }

    public static void switchToNewTab(WebDriver driver) {
        String originalHandle = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }
    }





}

