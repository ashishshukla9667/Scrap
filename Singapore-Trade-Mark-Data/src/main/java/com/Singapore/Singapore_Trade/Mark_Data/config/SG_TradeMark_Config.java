package com.Singapore.Singapore_Trade.Mark_Data.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("All")
@Configuration
public class SG_TradeMark_Config {

    @Value("${browser.headless:false}")
    private boolean isHeadless;

    @Bean(destroyMethod = "quit")
    public WebDriver webDriver() {
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            //options.addArguments("--headless=new");
            options.addArguments("--start-maximized");
            options.addArguments("--start-maximized");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/137.0.0.0 Safari/537.36");

            if (isHeadless) {
                options.addArguments("--headless=new");
            }

            Path tempProfile = Files.createTempDirectory("chrome-profile");
            options.addArguments("--user-data-dir=" + tempProfile.toAbsolutePath());
            options.addArguments("--disable-extensions");

            //Set download directory to src/main/resources
            String downloadDir = Path.of("src/main/resources").toAbsolutePath().toString();

            Map<String, Object> prefs = new HashMap<>();
            prefs.put("download.default_directory", "C:\\Users\\YourUser\\Downloads");  // adjust as needed
            prefs.put("download.prompt_for_download", false);
            prefs.put("plugins.always_open_pdf_externally", true);
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);

            options.setExperimentalOption("prefs", prefs);
            options.setCapability("goog:loggingPrefs", Map.of("browser", Level.ALL));

            return new ChromeDriver(options);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory for Chrome profile", e);
        }
    }

}

