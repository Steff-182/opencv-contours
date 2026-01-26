package com.opencv.demo;

import com.opencv.processing.ContourDetector;
import com.opencv.processing.PDFImageExtractor;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;

public class ProcessingTest {

    private static final String TEST_RESOURCES_DIR = "src/test/resources";

    @Before
    public void setup() {
        // Ensure test resources directory exists
        new File(TEST_RESOURCES_DIR).mkdirs();
    }

    @Test
    public void testPDFExtractionWorkflow() throws Exception {
        // Find any PDF in test resources
        File[] pdfFiles = new File(TEST_RESOURCES_DIR)
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            System.out.println("No PDF files found in " + TEST_RESOURCES_DIR);
            System.out.println("Place a PDF file there to test the extraction");
            return;
        }

        File pdfFile = pdfFiles[0];
        String pdfPath = pdfFile.getAbsolutePath();

        System.out.println("Testing with PDF: " + pdfFile.getName());

        // Extract image
        BufferedImage extracted = PDFImageExtractor.extractFirstPage(pdfPath);
        assert extracted != null;
        assert extracted.getWidth() > 0;
        assert extracted.getHeight() > 0;

        System.out.println("✓ Image extraction successful: " + extracted.getWidth() + "x" + extracted.getHeight());

        // Detect contours
        org.opencv.core.Mat contours = ContourDetector.detectDocumentContour(extracted);
        assert !contours.empty();

        // Save image with contours
        String contoursPath = TEST_RESOURCES_DIR + "/test_contours_detected.png";
        ContourDetector.saveMat(contours, contoursPath);
        System.out.println("✓ Contour detection successful");
        System.out.println("✓ Saved contours image to: " + contoursPath);

        // Crop
        BufferedImage cropped = ContourDetector.cropToDocument(extracted);
        assert cropped != null;
        assert cropped.getWidth() > 0;
        assert cropped.getHeight() > 0;

        // Save cropped image
        String croppedPath = TEST_RESOURCES_DIR + "/test_cropped.png";
        ContourDetector.saveBufferedImage(cropped, croppedPath);
        System.out.println("✓ Cropping successful: " + cropped.getWidth() + "x" + cropped.getHeight());
        System.out.println("✓ Saved cropped image to: " + croppedPath);

        contours.release();
    }
}
