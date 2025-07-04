package com.Singapore.Singapore_Trade.Mark_Data.pages;

import com.Singapore.Singapore_Trade.Mark_Data.util.FileUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.File;
import org.openqa.selenium.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.NoSuchElementException;

@SuppressWarnings("All")
@Component
public class SG_TradeMark_SGApplicationData {

    private final FileUtil fileUtil;
    String ownerName;

    public SG_TradeMark_SGApplicationData(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }


    //Extract Application Number
    public String getApplicationNumber(WebDriver driver,WebDriverWait wait) {

        try {
            WebElement tradeMarkNoElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//b[text()='Trade Mark No.']/parent::label/following-sibling::div/p")
            ));
            String registrationNumber = tradeMarkNoElement.getText().trim();
            System.out.println("Registration Number : " + registrationNumber);
            return registrationNumber;
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Application Number not found" + e.getMessage());
            return "not found";
        }
    }

    //Extract Mark
    private static byte[] buildMultipartBody(File file, String boundary) throws IOException {
        String LINE_FEED = "\r\n";

        String fileName = file.getName();
        String mimeType = Files.probeContentType(file.toPath());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        writer.write("--" + boundary);
        writer.write(LINE_FEED);
        writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"");
        writer.write(LINE_FEED);
        writer.write("Content-Type: " + mimeType);
        writer.write(LINE_FEED);
        writer.write(LINE_FEED);
        writer.flush();

        Files.copy(file.toPath(), outputStream);

        writer.write(LINE_FEED);
        writer.write("--" + boundary + "--");
        writer.write(LINE_FEED);
        writer.flush();
        writer.close();

        return outputStream.toByteArray();
    }

    public String extractTrademarkText(String applicationNumber, WebDriver driver, WebDriverWait wait){
        String ownerAddress = null;

        try {
//            WebElement imageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//label[contains(., 'Trade Mark Image')]/following-sibling::div//img")
//            ));
//            File imgScreenshot = imageElement.getScreenshotAs(OutputType.FILE);
//
//            String relativePath = "src/main/resources/marks/" + applicationNumber + ".png";
            // extracting owner address from image
            WebElement imgElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(., 'Trade Mark Image')]/following-sibling::div//img")));
            String base64Image = imgElement.getAttribute("src").split(",")[2];
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            File imageFile = new File("extracted_image.png");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(imageBytes);
            } catch (IOException e) {
                //LOG.warn("[{}] ", executionTraceId);
            }
           // LOG.info("[{}] Address Image extracted", executionTraceId);

            //System.out.println("Image successfully saved at: " + outputImage.getAbsolutePath());

            String apiUrl = "http://RPA-LoadBalancer-OCR-43099113.eu-west-1.elb.amazonaws.com/ocr/perform-ocr-wipo";
            String boundary = UUID.randomUUID().toString();

         //   LOG.info("[{}] Extracting address from image", executionTraceId);
            try {
                HttpClient client = HttpClient.newHttpClient();

                byte[] requestBody = buildMultipartBody(imageFile, boundary);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("client-id", "52feb352-cdb5-4144-9a1d-0bb7920a8912")
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody)) // Send as form-data
                        .build();

                HttpResponse<String> ocrResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (ocrResponse.statusCode() == 200) {
                    ownerAddress = ocrResponse.body();
                    //logUtil.sendLog("Owner Address: " + ownerAddress, LogLevel.INFO, executionTraceId);
             //       LOG.info("[{}] " + "Owner Address: " + ownerAddress, executionTraceId);
                }
            } catch (IOException | InterruptedException e) {
                //LOG.warn("[{}] " + "Exception occurred" + e.getClass().getSimpleName(), executionTraceId);
            }

            //ownerName = ownerNameOnly + System.lineSeparator() + ownerAddress;
            fileUtil.deleteFile("extracted_image.png");

        } catch (WebDriverException e) {
            ownerName = "No Owner Found ";
//            logUtil.sendLog("Owner details not found", LogLevel.WARN, executionTraceId);
//            LOG.warn("[{}] Owner details not found", executionTraceId);
        }
        return "";
    }

//    public String extractTrademarkText(String applicationNumber, WebDriver driver, WebDriverWait wait)
//            throws IOException, InterruptedException, URISyntaxException {
//
//        String mark = "LOGO/DEVICE";
//
//        try {
//            //  Step 1: Locate the <img> under "Trade Mark Image" label
//            WebElement imageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.xpath("//label[contains(., 'Trade Mark Image')]/following-sibling::div//img")
//            ));
//
//            //  Step 2: Capture screenshot of the actual <img> element
//            File imgScreenshot = imageElement.getScreenshotAs(OutputType.FILE);
//
//            //  Step 3: Save the screenshot to resources folder
//            String relativePath = "src/main/resources/marks/" + applicationNumber + ".png";
//            File outputImage = new File(relativePath);
//            outputImage.getParentFile().mkdirs();
//            Files.copy(imgScreenshot.toPath(), outputImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            if (!outputImage.exists()) {
//                System.out.println("ERROR: Image file was not saved.");
//                return "No Mark Title";
//            }
//
//            System.out.println("Image successfully saved at: " + outputImage.getAbsolutePath());
//
//            //  Step 4: Send image to OCR API
//            String apiUrl = "http://RPA-LoadBalancer-OCR-43099113.eu-west-1.elb.amazonaws.com/ocr/perform-ocr-wipo";
//            String boundary = UUID.randomUUID().toString();
//            byte[] requestBody = buildMultipartBody(outputImage, boundary);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(new URI(apiUrl))
//                    .header("client-id", "52feb352-cdb5-4144-9a1d-0bb7920a8912")
//                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
//                    .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody))
//                    .build();
//
//            HttpClient client = HttpClient.newHttpClient();
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println("OCR API response status: " + response.statusCode());
//            System.out.println("OCR response body: " + response.body());
//
//            if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
//                mark = response.body().trim();
//            } else {
//                System.out.println("OCR failed or returned empty text. Defaulting to LOGO/DEVICE");
//            }
//
//        } catch (IOException | InterruptedException | URISyntaxException e) {
//            System.out.println("EXCEPTION: " + e.getClass().getSimpleName() + " - " + e.getMessage());
//            return "No Mark Title";
//        }
//
//        System.out.println("Final extracted trademark title: " + mark);
//        return mark;
//    }


    //Extract Expiry Date
    public String getExpiryDate(WebDriver driver, WebDriverWait wait) {

        try {
            String expiryDate = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[contains(@class, 'default-table')]//tbody/tr[1]/td[@data-title='Class Expiry Date']")
            )).getText().trim();

            System.out.println("Expiry Date : " + expiryDate);
            return expiryDate;
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Expiry Date not found");
            return "not found";
        }
    }


    //Extract Classes
    public String getClasses(WebDriver driver,WebDriverWait wait) {

        try {
            String classes = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[@data-title='Class No.']"))).getText().trim();
            System.out.println("Class : " + classes);
            return classes;
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Classes not found");
            return "not found";
        }
    }

    //Extract Owner Name
    public String getOwnerName(WebDriver driver,WebDriverWait wait) {

        try {
            String ownerName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[@data-title='Name']"))).getText().trim();
            System.out.println("Owner Name : " + ownerName);
            return ownerName;
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Owner Name not found");
            return "not found";
        }
    }

    //Extract Owner Address
    public String getOwnerAddress(WebDriver driver,WebDriverWait wait) {

        try {
            // Locate the <td> with data-title='Address'
            WebElement addressElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//td[@data-title='Address']")
            ));

            // Get text (line breaks <br> will be converted to space or may collapse depending on driver)
            String rawAddress = addressElement.getAttribute("innerHTML");

            //Replace <br> tags with newlines
            String ownerAddress = rawAddress.replaceAll("(?i)<br\\s*/?>", "\n").replaceAll("\\s+", " ").trim();

            System.out.println("Address:\n" + ownerAddress);
            System.out.println();
            return ownerAddress;
        } catch (NoSuchElementException | TimeoutException e) {
            System.out.println("Error extracting Owner Address : " + e);
            System.out.println("Owner Address not found");
            return "not found";
        }
    }

    public void printData(WebDriver driver, WebDriverWait wait) {
        String filePath = "/src/resource/output/";

        WebElement printPage = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"printIpDetailsBtn\"]")));
        printPage.click();

    }
}
