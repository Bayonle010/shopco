package com.shopco.file.controller;

import com.shopco.core.response.ApiResponse;
import com.shopco.core.response.ResponseUtil;
import com.shopco.file.service.impl.CloudinaryServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Tag(name = "File Upload")
@RestController
@RequestMapping("/api/v1/uploads")
public class FileUploadController {

    @Autowired
    private CloudinaryServiceImpl cloudinaryService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("folder") String folderName

    ) {
        String imageUrl = cloudinaryService.uploadFile(file, folderName);
        return ResponseEntity.ok(ResponseUtil.success(200, "File uploaded successfully", imageUrl, null));

    }
}
