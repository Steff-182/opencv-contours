package com.opencv.demo;

import com.opencv.processing.ContourDetector;
import com.opencv.processing.PDFImageExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;

/**
 * Main demo class for PDF image extraction and contour detection.
 * 
 * This demo:
 * 1. Loads a PDF from src/test/resources
 * 2. Extracts the first page as an image
 * 3. Detects document contours (useful for scanned ID cards, documents on white background)
 * 4. Crops the image to the detected document area
 * 5. Saves the results to the target folder
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("OpenCV Contour Detection - PDF Processing");
        logger.info("========================================");

        try {
            // Locate the test PDF file
            String testResourcesPath = Paths.get("src", "test", "resources").toString();
            File resourceDir = new File(testResourcesPath);

            if (!resourceDir.exists()) {
                logger.error("Test resources directory does not exist: {}", testResourcesPath);
                logger.info("Please create the directory and add a PDF file (e.g., document.pdf)");
                return;
            }

            // Find the first PDF file in the resources folder
            File[] pdfFiles = resourceDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (pdfFiles == null || pdfFiles.length == 0) {
                logger.warn("No PDF files found in {}", testResourcesPath);
                logger.info("Please add a PDF file to: {}", resourceDir.getAbsolutePath());
                logger.info("Example use case: scanned ID card or document on white background");
                return;
            }

            File pdfFile = pdfFiles[0];
            String pdfPath = pdfFile.getAbsolutePath();

            logger.info("Processing PDF: {}", pdfFile.getName());
            logger.info("Full path: {}", pdfPath);

            // Extract page count
            int pageCount = PDFImageExtractor.getPageCount(pdfPath);
            logger.info("PDF has {} page(s)", pageCount);

            // Extract first page as image
            logger.info("Extracting first page from PDF...");
            BufferedImage extractedImage = PDFImageExtractor.extractFirstPage(pdfPath);
            logger.info("Successfully extracted image: {}x{} pixels",
                    extractedImage.getWidth(), extractedImage.getHeight());

            // Save the extracted image
            String outputDir = "target/output";
            new File(outputDir).mkdirs();

            String extractedImagePath = Paths.get(outputDir, "01_extracted.png").toString();
            ContourDetector.saveBufferedImage(extractedImage, extractedImagePath);
            logger.info("Saved extracted image to: {}", extractedImagePath);

            // Detect document contour
            logger.info("Detecting document contour...");
            org.opencv.core.Mat contourImage = ContourDetector.detectDocumentContour(extractedImage);
            String contourImagePath = Paths.get(outputDir, "02_contours_detected.png").toString();
            ContourDetector.saveMat(contourImage, contourImagePath);
            logger.info("Saved contour detection image to: {}", contourImagePath);

            // Crop to document area
            logger.info("Cropping image to document bounds...");
            BufferedImage croppedImage = ContourDetector.cropToDocument(extractedImage);
            String croppedImagePath = Paths.get(outputDir, "03_cropped.png").toString();
            ContourDetector.saveBufferedImage(croppedImage, croppedImagePath);
            logger.info("Saved cropped image to: {}", croppedImagePath);
            logger.info("Cropped image dimensions: {}x{} pixels",
                    croppedImage.getWidth(), croppedImage.getHeight());

            // Clean up
            contourImage.release();

            logger.info("========================================");
            logger.info("Processing completed successfully!");
            logger.info("Output files saved to: {}", new File(outputDir).getAbsolutePath());
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("Error during processing", e);
            System.exit(1);
        }
    }
}
