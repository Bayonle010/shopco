package com.shopco.file.util;

import com.shopco.core.exception.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class for file validation across the system.
 */
public class FileValidationUtil {

    // Max file sizes (in bytes)
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;        // 2MB
    private static final long MAX_DOCUMENT_SIZE = 5 * 1024 * 1024;     // 5MB
    private static final long MAX_VIDEO_SIZE = 20 * 1024 * 1024;       // 20MB

    // Allowed MIME types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword"
            /*
            Uncomment if needed in the future
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
             */
    );

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/mpeg", "video/webm"
    );

    /**
     * Validates an image file according to size and type constraints.
     *
     * @param file The file to validate
     * @throws InvalidFileException If validation fails
     */
    public static void validateImageFile(MultipartFile file) throws InvalidFileException {
        // Basic validation (null, empty)
        validateBasics(file);

        // Check file size for images
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new InvalidFileException("Image size exceeds the limit of 2MB");
        }

        // Validate image type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new InvalidFileException("Invalid image type. Allowed types: JPEG, PNG");
        }
    }

    /**
     * Validates a document file according to size and type constraints.
     *
     * @param file The file to validate
     * @throws InvalidFileException If validation fails
     */
    public static void validateDocumentFile(MultipartFile file) throws InvalidFileException {
        // Basic validation
        validateBasics(file);

        // Check file size for documents
        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new InvalidFileException("Document size exceeds the limit of 5MB");
        }

        // Validate document type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            throw new InvalidFileException("Invalid document type. Allowed types: PDF, DOC, DOCX, XLS, XLSX");
        }
    }

    /**
     * Validates a video file according to size and type constraints.
     *
     * @param file The file to validate
     * @throws InvalidFileException If validation fails
     */
    public static void validateVideoFile(MultipartFile file) throws InvalidFileException {
        // Basic validation
        validateBasics(file);

        // Check file size for videos
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new InvalidFileException("Video size exceeds the limit of 20MB");
        }

        // Validate video type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new InvalidFileException("Invalid video type. Allowed types: MP4, MPEG, WebM");
        }
    }

    /**
     * Validates basic file properties that are common for all file types.
     *
     * @param file The file to validate
     * @throws InvalidFileException If validation fails
     */
    private static void validateBasics(MultipartFile file) throws InvalidFileException {
        // Check if file is null or empty
        if (file == null) {
            throw new InvalidFileException("File is null");
        }

        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        // Check if filename is valid
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new InvalidFileException("Filename is missing");
        }
    }

    /**
     * Gets the file extension from a filename.
     *
     * @param filename The filename
     * @return The file extension (without the dot) or empty string if none
     */
    public static String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * Checks if the file is an image based on its content type.
     *
     * @param contentType The MIME content type
     * @return true if it's an image, false otherwise
     */
    public static boolean isImageFile(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
    }

    /**
     * Checks if the file is a document based on its content type.
     *
     * @param contentType The MIME content type
     * @return true if it's a document, false otherwise
     */
    public static boolean isDocumentFile(String contentType) {
        return contentType != null && ALLOWED_DOCUMENT_TYPES.contains(contentType);
    }
}