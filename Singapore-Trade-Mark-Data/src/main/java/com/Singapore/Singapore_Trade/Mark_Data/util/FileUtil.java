package com.Singapore.Singapore_Trade.Mark_Data.util;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

@Component
public class FileUtil {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    public static String getDateTime(String format) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = switch (format.toLowerCase()) {
            case "date" -> DateTimeFormatter.ofPattern("dd-MM-yyyy");
            case "time" -> DateTimeFormatter.ofPattern("HH:mm:ss");
            case "datetime" -> DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            default -> throw new IllegalArgumentException("Invalid format. Use 'date', 'time', or 'datetime'.");
        };
        return now.format(formatter);
    }

    public String getContentType(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return switch (extension.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "tif" -> "image/tif";
            case "doc", "docx" -> "application/msword";
            case "xls", "xlsx" -> "application/vnd.ms-excel";
            default -> "application/octet-stream";
        };
    }

    public String convertDateFormat(String date, String currentFormat, String targetFormat,String executionTraceId) {
        try {
            if(date == null || date.isEmpty() || date.equals("N/A") || date.equals("Not given")) {
                return date;
            }

            DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern(currentFormat);
            DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(targetFormat);
            LocalDate parsedDate = LocalDate.parse(date, currentFormatter);
            return parsedDate.format(targetFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date or format provided: " + e.getMessage());
        }
    }

    public String convertDateFromMilliseconds(String date, String targetFormat,String executionTraceId) {
        try {
            if(date == null || date.isEmpty() || date.equals("N/A")) {
                return "N/A";
            }
            DateFormat formatter = new SimpleDateFormat(targetFormat);

            long milliSeconds= Long.parseLong(date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);

            return formatter.format(calendar.getTime());
        } catch (DateTimeParseException e) {
            LOG.error("[{}] Error parsing date: {}",executionTraceId, e.getMessage());
            throw new IllegalArgumentException("Invalid date or format provided: " + e.getMessage());
        }
    }

    public File convertInputStreamToFile(InputStream inputStream, String baseFileName) throws IOException {

        // Generate a unique file name by appending a timestamp
        String uniqueFileName = baseFileName.replace(".csv", "_" + System.currentTimeMillis() + ".csv");

        // Create the output file
        File convertedFile = new File(uniqueFileName);

        // Create FileOutputStream to write data to the file
        try (FileOutputStream outputStream = new FileOutputStream(convertedFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read from the InputStream and write to the file
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return convertedFile;  // Return the created file
    }

    public byte[] convertFileToByteArray(File file) {
        try {
            // Create a FileInputStream to read the file
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                // Create a byte array of the same length as the file size
                byte[] fileBytes = new byte[(int) file.length()];

                // Read the file content into the byte array
                fileInputStream.read(fileBytes);

                return fileBytes;  // Return the byte array
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);

        // Check if the file exists
        if (file.exists()) {
            // Attempt to delete the file and return the result
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("[{}] File {} deleted successfully.");
            } else {
                System.out.println("[{}] Failed to delete file {}");
            }
            return deleted;
        } else {
            // Log and handle the case when the file does not exist
            System.out.println("[{}] File {} not found for deletion.");
            return false;
        }
    }

    public void deleteFileInProjectDirectory(String regex,String executionTraceId) {
        try {
            // Get the project's root directory
            String projectDirectory = System.getProperty("user.dir");
            File directory = new File(projectDirectory);

            // Check if the directory exists
            if (!directory.exists() || !directory.isDirectory()) {
                LOG.info("[{}] The project directory is not valid.",executionTraceId);
                return;
            }

            // Get all files in the project directory
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                LOG.info("[{}] No files found in the project directory.",executionTraceId);
                return;
            }

            boolean fileDeleted = false;

            // Loop through all files and delete the ones that match the regex pattern
            for (File file : files) {
                String fileName = file.getName();

                // Check if the file name matches the regex pattern
                if (fileName.matches(regex)) {
                    if (file.delete()) {
                        fileDeleted = true;
                    }
                }
            }

            if (!fileDeleted) {
                LOG.info("[{}] No files matching the pattern found in the project directory.",executionTraceId);
            }
        }catch (Exception e){
            LOG.error("[{}] Error deleting file in project directory: {}",executionTraceId, e.getMessage());
        }
    }

    public List<String> findFile(String regex,String executionTraceId) {
        List<String> fileNames = new ArrayList<>();
        try {
            // Get the project's root directory
            String projectDirectory = System.getProperty("user.dir");
            File directory = new File(projectDirectory);

            // Check if the directory exists
            if (!directory.exists() || !directory.isDirectory()) {
                LOG.error("[{}] The project directory is not valid",executionTraceId);
                return null;

            }

            // Get all files in the project directory
            File[] files = directory.listFiles();
            if (files == null || files.length == 0) {
                LOG.error("[{}] No files found in the project directory",executionTraceId);
                return null;

            }

            // Loop through all files and delete the ones that match the regex pattern
            for (File file : files) {
                String fileName = file.getName();

                // Check if the file name matches the regex pattern
                if (fileName.matches(regex)) {
                   fileNames.add(fileName);
                }
            }
            return fileNames;

        }catch (Exception e){
            LOG.error("[{}] Error getting file in project directory: {}",executionTraceId, e.getMessage());
            return null;
        }
    }

    public String encodeToBase64(byte[] fileContent,String executionTraceId) {
        if (fileContent == null) {
            LOG.error("[{}] File content cannot be null",executionTraceId);
            throw new IllegalArgumentException("File content cannot be null");
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public byte[] decodeFromBase64(String base64String,String executionTraceId) {
        if (base64String == null || base64String.isEmpty()) {
            LOG.error("[{}] Base64 string cannot be null or empty",executionTraceId);
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }
        return Base64.getDecoder().decode(base64String);
    }

    public void moveDownloadedFile(File outputFile,String executionTraceId) throws IOException {
        // Get system's default Downloads folder
        String home = System.getProperty("user.home");
        File downloadsFolder = new File(home + "/Downloads");

        File[] pdfFiles = downloadsFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles != null && pdfFiles.length > 0) {
            File latestFile = pdfFiles[0];
            for (File file : pdfFiles) {
                if (file.lastModified() > latestFile.lastModified()) {
                    latestFile = file;
                }
            }

            // Copy the latest downloaded file to the project directory
            try (InputStream inputStream = new FileInputStream(latestFile);
                 FileOutputStream outputStream = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            LOG.warn("[{}] No PDF found in downloads folder.",executionTraceId);
        }
    }
    public String normalizeApplicationNumber(String applicationNumber, String executionTraceId) {
        try {
            if (applicationNumber == null || applicationNumber.trim().isEmpty()) {
                return "";
            }

            // Trim and try to convert if it's in scientific notation
            if (applicationNumber.toUpperCase().contains("E")) {
                BigDecimal bd = new BigDecimal(applicationNumber.trim());
                return bd.toPlainString();
            }

            return applicationNumber.trim();
        } catch (NumberFormatException e) {
            LOG.warn("[{}] Could not normalize application number: {}", executionTraceId, applicationNumber);
            return applicationNumber.trim(); // Return original if it fails to parse
        }
    }

}
