package com.opencv.processing;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.*;

/**
 * Utility class for contour detection and image processing using OpenCV.
 * Specialized for document detection (ID cards, documents scanned on white background).
 */
public class ContourDetector {
    private static final Logger logger = LoggerFactory.getLogger(ContourDetector.class);

    static {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();
    }

    /**
     * Detects the main document contour in an image (typically scanned documents on white background).
     *
     * @param image the input BufferedImage
     * @return Mat containing the original image with detected contours drawn
     */
    public static Mat detectDocumentContour(BufferedImage image) {
        logger.info("Detecting document contour in image: {}x{}", image.getWidth(), image.getHeight());

        // Convert BufferedImage to Mat
        Mat original = bufferedImageToMat(image);
        Mat processing = original.clone();

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(processing, gray, Imgproc.COLOR_BGR2GRAY);

        // Increase contrast to better detect subtle differences (white card on white background)
        Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX);

        // Apply Gaussian blur to reduce noise
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Use Canny edge detection for more sensitive edge detection
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 30, 100);

        // Also use adaptive threshold for subtle differences
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(blurred, binary, 255, 
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 
                Imgproc.THRESH_BINARY_INV, 11, 2);
        
        // Combine both edge detection methods
        Core.bitwise_or(edges, binary, binary);

        // Apply morphological operations to clean up
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat morphed = new Mat();
        Imgproc.morphologyEx(binary, morphed, Imgproc.MORPH_CLOSE, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(morphed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Filter and sort contours by area (largest first)
        contours.sort((c1, c2) -> Double.compare(
                Imgproc.contourArea(c2),
                Imgproc.contourArea(c1)
        ));

        // Draw the largest contour (main document)
        Mat result = original.clone();
        if (!contours.isEmpty()) {
            MatOfPoint largestContour = contours.get(0);
            double area = Imgproc.contourArea(largestContour);
            logger.info("Largest contour area: {}", area);

            // Draw ALL detected contours in red with thick lines
            for (int i = 0; i < Math.min(contours.size(), 10); i++) {
                Imgproc.drawContours(result, contours, i,
                        new Scalar(0, 0, 255), 5);  // Red, 5px thick
            }

            // Draw the largest contour in green (even thicker)
            Imgproc.drawContours(result, List.of(largestContour), 0,
                    new Scalar(0, 255, 0), 8);  // Green, 8px thick

            // Create bounding rectangle that encompasses ALL detected contours
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            int maxX = 0, maxY = 0;
            
            for (MatOfPoint contour : contours) {
                Rect boundingRect = Imgproc.boundingRect(contour);
                minX = Math.min(minX, boundingRect.x);
                minY = Math.min(minY, boundingRect.y);
                maxX = Math.max(maxX, boundingRect.x + boundingRect.width);
                maxY = Math.max(maxY, boundingRect.y + boundingRect.height);
            }
            
            // Add 15 pixel margin around the bounding rectangle
            int margin = 15;
            minX = Math.max(0, minX - margin);
            minY = Math.max(0, minY - margin);
            maxX = Math.min(original.cols(), maxX + margin);
            maxY = Math.min(original.rows(), maxY + margin);
            
            // Draw the overall bounding rectangle
            Point topLeft = new Point(minX, minY);
            Point topRight = new Point(maxX, minY);
            Point bottomRight = new Point(maxX, maxY);
            Point bottomLeft = new Point(minX, maxY);
            
            MatOfPoint boundingPoly = new MatOfPoint(topLeft, topRight, bottomRight, bottomLeft);
            
            logger.info("Overall bounding rectangle: x={}, y={}, width={}, height={}", 
                    minX, minY, maxX - minX, maxY - minY);
            
            // Draw rectangle in blue (thick)
            Imgproc.drawContours(result, List.of(boundingPoly), 0,
                    new Scalar(255, 0, 0), 8);  // Blue, 8px thick
            logger.info("Bounding rectangle encompasses all {} detected contours", contours.size());
        }

        // Clean up
        gray.release();
        blurred.release();
        edges.release();
        binary.release();
        morphed.release();
        kernel.release();
        hierarchy.release();
        processing.release();

        return result;
    }

    /**
     * Crops an image to the detected document area (removes white borders).
     * Uses the same detection algorithm as detectDocumentContour.
     *
     * @param image the input BufferedImage
     * @return cropped BufferedImage
     */
    public static BufferedImage cropToDocument(BufferedImage image) {
        logger.info("Cropping image to document bounds");

        Mat original = bufferedImageToMat(image);
        Mat processing = original.clone();

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(processing, gray, Imgproc.COLOR_BGR2GRAY);

        // Increase contrast to better detect subtle differences
        Core.normalize(gray, gray, 0, 255, Core.NORM_MINMAX);

        // Apply Gaussian blur to reduce noise
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);

        // Use Canny edge detection for more sensitive edge detection
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 30, 100);

        // Also use adaptive threshold for subtle differences
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(blurred, binary, 255, 
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 
                Imgproc.THRESH_BINARY_INV, 11, 2);
        
        // Combine both edge detection methods
        Core.bitwise_or(edges, binary, binary);

        // Apply morphological operations to clean up
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat morphed = new Mat();
        Imgproc.morphologyEx(binary, morphed, Imgproc.MORPH_CLOSE, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(morphed, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.isEmpty()) {
            logger.warn("No contours found, returning original image");
            gray.release();
            blurred.release();
            edges.release();
            binary.release();
            morphed.release();
            kernel.release();
            hierarchy.release();
            processing.release();
            return image;
        }

        // Create bounding rectangle that encompasses ALL detected contours
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = 0, maxY = 0;
        
        for (MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);
            minX = Math.min(minX, boundingRect.x);
            minY = Math.min(minY, boundingRect.y);
            maxX = Math.max(maxX, boundingRect.x + boundingRect.width);
            maxY = Math.max(maxY, boundingRect.y + boundingRect.height);
        }
        
        // Add 15 pixel margin around the bounding rectangle
        int margin = 15;
        minX = Math.max(0, minX - margin);
        minY = Math.max(0, minY - margin);
        maxX = Math.min(original.cols(), maxX + margin);
        maxY = Math.min(original.rows(), maxY + margin);

        logger.info("Crop bounds: x={}, y={}, width={}, height={}", 
                minX, minY, maxX - minX, maxY - minY);

        // Crop the image
        Rect cropRect = new Rect(minX, minY, maxX - minX, maxY - minY);
        Mat cropped = new Mat(original, cropRect);
        BufferedImage result = matToBufferedImage(cropped);

        // Clean up
        gray.release();
        blurred.release();
        edges.release();
        binary.release();
        morphed.release();
        kernel.release();
        hierarchy.release();
        processing.release();
        original.release();
        cropped.release();

        return result;
    }

    /**
     * Converts a BufferedImage to OpenCV Mat.
     *
     * @param image the BufferedImage
     * @return Mat representation
     */
    private static Mat bufferedImageToMat(BufferedImage image) {
        // Convert to TYPE_3BYTE_BGR if needed
        BufferedImage convertedImage = image;
        if (image.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            convertedImage.getGraphics().drawImage(image, 0, 0, null);
        }
        
        Mat mat = new Mat(convertedImage.getHeight(), convertedImage.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    /**
     * Converts an OpenCV Mat to BufferedImage.
     *
     * @param mat the Mat
     * @return BufferedImage
     */
    public static BufferedImage matToBufferedImage(Mat mat) {
        byte[] data = new byte[mat.rows() * mat.cols() * (int)mat.elemSize()];
        mat.get(0, 0, data);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

        return image;
    }

    /**
     * Saves a Mat to file.
     *
     * @param mat      the Mat to save
     * @param filePath output file path
     * @return true if successful
     */
    public static boolean saveMat(Mat mat, String filePath) {
        boolean success = Imgcodecs.imwrite(filePath, mat);
        if (success) {
            logger.info("Image saved to: {}", filePath);
        } else {
            logger.error("Failed to save image to: {}", filePath);
        }
        return success;
    }

    /**
     * Saves a BufferedImage to file.
     *
     * @param image    the BufferedImage to save
     * @param filePath output file path
     * @return true if successful
     */
    public static boolean saveBufferedImage(BufferedImage image, String filePath) {
        Mat mat = bufferedImageToMat(image);
        boolean success = saveMat(mat, filePath);
        mat.release();
        return success;
    }
}
