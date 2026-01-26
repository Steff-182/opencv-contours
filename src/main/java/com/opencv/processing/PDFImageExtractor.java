package com.opencv.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Utility class for extracting images from PDF files using PDFBox.
 */
public class PDFImageExtractor {
    private static final Logger logger = LoggerFactory.getLogger(PDFImageExtractor.class);

    /**
     * Extracts the first page of a PDF file as a BufferedImage.
     *
     * @param pdfFilePath path to the PDF file
     * @return BufferedImage of the first page
     * @throws IOException if the file cannot be read
     */
    public static BufferedImage extractFirstPage(String pdfFilePath) throws IOException {
        return extractPageAsImage(pdfFilePath, 0);
    }

    /**
     * Extracts a specific page of a PDF file as a BufferedImage.
     *
     * @param pdfFilePath path to the PDF file
     * @param pageIndex   0-based index of the page to extract
     * @return BufferedImage of the specified page
     * @throws IOException if the file cannot be read
     */
    public static BufferedImage extractPageAsImage(String pdfFilePath, int pageIndex) throws IOException {
        logger.info("Extracting page {} from PDF: {}", pageIndex, pdfFilePath);

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            if (pageIndex >= document.getNumberOfPages()) {
                throw new IllegalArgumentException(
                        String.format("Page index %d is out of range. PDF has %d pages.",
                                pageIndex, document.getNumberOfPages())
                );
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            // Render at 300 DPI for better quality
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);

            logger.info("Successfully extracted page {} with dimensions: {}x{}",
                    pageIndex, image.getWidth(), image.getHeight());
            return image;
        }
    }

    /**
     * Gets the number of pages in a PDF file.
     *
     * @param pdfFilePath path to the PDF file
     * @return number of pages
     * @throws IOException if the file cannot be read
     */
    public static int getPageCount(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            return document.getNumberOfPages();
        }
    }
}
