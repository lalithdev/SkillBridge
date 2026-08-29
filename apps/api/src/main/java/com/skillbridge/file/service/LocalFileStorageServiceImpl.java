package com.skillbridge.file.service;

import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.exception.UnsupportedMediaTypeException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorageServiceImpl.class);

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".pdf", ".docx");
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "application/octet-stream" // for some clients sending generic binary
    );

    @Value("${app.file.upload-dir:./uploads/resumes}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
            log.info("Initialized local file storage at: {}", this.rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize file storage directory: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }

    @Override
    public String storeResume(MultipartFile file, Long studentProfileId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BadRequestException("Invalid filename");
        }

        String cleanFilename = StringUtils.cleanPath(originalFilename);
        String extension = "";
        int dotIndex = cleanFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = cleanFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedMediaTypeException(
                    "Unsupported file format: " + extension + ". Only PDF and DOCX documents are permitted.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new UnsupportedMediaTypeException(
                    "Unsupported content type: " + contentType + ". Only PDF and DOCX documents are permitted.");
        }

        try {
            String storedFileName = "resume_student_" + studentProfileId + "_" + UUID.randomUUID() + extension;
            Path destinationFile = this.rootLocation.resolve(storedFileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(this.rootLocation)) {
                throw new BadRequestException("Cannot store file outside current directory");
            }

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource loadAsResource(String relativePath) {
        try {
            if (relativePath == null || relativePath.trim().isEmpty()) {
                throw new ResourceNotFoundException("Resume file not found");
            }

            Path file = this.rootLocation.resolve(relativePath).normalize().toAbsolutePath();
            if (!file.startsWith(this.rootLocation)) {
                throw new ResourceNotFoundException("Resume file not found");
            }

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Resume file not found on disk");
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Resume file not found: " + relativePath);
        }
    }

    @Override
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return;
        }

        try {
            Path file = this.rootLocation.resolve(relativePath).normalize().toAbsolutePath();
            if (file.startsWith(this.rootLocation)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException e) {
            log.warn("Failed to delete file {}: {}", relativePath, e.getMessage());
        }
    }

    @Override
    public MediaType determineMediaType(String filePath) {
        if (filePath != null) {
            String lower = filePath.toLowerCase();
            if (lower.endsWith(".pdf")) {
                return MediaType.APPLICATION_PDF;
            } else if (lower.endsWith(".docx")) {
                return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            } else if (lower.endsWith(".doc")) {
                return MediaType.parseMediaType("application/msword");
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
