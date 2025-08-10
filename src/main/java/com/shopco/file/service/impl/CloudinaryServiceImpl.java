package com.shopco.file.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.shopco.core.exception.NetworkConnectivityException;
import com.shopco.file.service.CloudinaryService;
import com.shopco.file.util.FileValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service("CloudinaryService")
public class CloudinaryServiceImpl implements CloudinaryService {
    /**
     * @param file 
     * @param folderName
     * @return
     */

    @Resource
    private Cloudinary cloudinary;
    @Override
    public String uploadFile(MultipartFile file, String folderName) {
        try {

            FileValidationUtil.validateImageFile(file);

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folderName,
                    "resource_type", "image"
            ));
            return (String) uploadResult.get("secure_url");

        } catch (NetworkConnectivityException e)  {
            throw new NetworkConnectivityException(e.getMessage());
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param imageUrl
     */
    @Override
    public void deleteImageByUrl(String imageUrl) {
        try {
            String publicId = extractPublicIdFromUrl(imageUrl);
            log.info("Attempting to delete Cloudinary image with public ID: {}", publicId);

            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Cloudinary delete result: {}", result);

            if (!"ok".equals(result.get("result"))) {
                log.warn("Cloudinary deletion failed or image not found: {}", result);
            }
        } catch (Exception e) {
            log.error("Failed to delete image from Cloudinary: {}", e.getMessage(), e);
        }

    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // Example: https://res.cloudinary.com/your-cloud-name/image/upload/v123456789/folder/image-name.jpg

            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid Cloudinary URL: " + imageUrl);
            }

            // Get the part after "/upload/"
            String path = parts[1];

            // Remove version if present (starts with "v" followed by digits and a slash)
            if (path.matches("^v\\d+/.+")) {
                path = path.replaceFirst("^v\\d+/", "");
            }

            // Remove the file extension
            int dotIndex = path.lastIndexOf('.');
            return dotIndex != -1 ? path.substring(0, dotIndex) : path;

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Cloudinary URL: " + imageUrl, e);
        }
    }
}
