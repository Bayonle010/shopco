package com.shopco.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String folderName);

    void deleteImageByUrl(String fileUrl);
}
