package com.skillbridge.file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeResume(MultipartFile file, Long studentProfileId);

    Resource loadAsResource(String relativePath);

    void deleteFile(String relativePath);

    MediaType determineMediaType(String filePath);
}
