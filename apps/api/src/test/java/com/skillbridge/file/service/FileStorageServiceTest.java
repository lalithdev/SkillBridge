package com.skillbridge.file.service;

import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.exception.UnsupportedMediaTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    private LocalFileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempUploadDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new LocalFileStorageServiceImpl();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempUploadDir.toString());
        fileStorageService.init();
    }

    @Test
    @DisplayName("storeResume - successfully stores PDF resume")
    void storeResumePdfSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "my_resume.pdf",
                "application/pdf",
                "%PDF-1.4 dummy content".getBytes()
        );

        String storedPath = fileStorageService.storeResume(file, 101L);

        assertThat(storedPath).startsWith("resume_student_101_");
        assertThat(storedPath).endsWith(".pdf");

        Resource resource = fileStorageService.loadAsResource(storedPath);
        assertThat(resource.exists()).isTrue();
    }

    @Test
    @DisplayName("storeResume - successfully stores DOCX resume")
    void storeResumeDocxSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "dummy docx bytes".getBytes()
        );

        String storedPath = fileStorageService.storeResume(file, 102L);

        assertThat(storedPath).startsWith("resume_student_102_");
        assertThat(storedPath).endsWith(".docx");
    }

    @Test
    @DisplayName("storeResume - rejects unsupported media extension (e.g. .txt, .exe)")
    void storeResumeRejectsInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.exe",
                "application/octet-stream",
                "binary content".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.storeResume(file, 103L))
                .isInstanceOf(UnsupportedMediaTypeException.class)
                .hasMessageContaining("Unsupported file format");
    }

    @Test
    @DisplayName("storeResume - rejects empty file")
    void storeResumeRejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.pdf",
                "application/pdf",
                new byte[0]
        );

        assertThatThrownBy(() -> fileStorageService.storeResume(file, 104L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("cannot be empty");
    }

    @Test
    @DisplayName("storeResume - rejects file larger than 5 MB")
    void storeResumeRejectsOversizedFile() {
        byte[] largeContent = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.pdf",
                "application/pdf",
                largeContent
        );

        assertThatThrownBy(() -> fileStorageService.storeResume(file, 105L))
                .isInstanceOf(MaxUploadSizeExceededException.class);
    }

    @Test
    @DisplayName("deleteFile - deletes existing file")
    void deleteFileSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "content".getBytes()
        );
        String storedPath = fileStorageService.storeResume(file, 106L);

        fileStorageService.deleteFile(storedPath);

        assertThatThrownBy(() -> fileStorageService.loadAsResource(storedPath))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("determineMediaType - returns correct MediaType for PDF and DOCX")
    void determineMediaTypeCorrectness() {
        assertThat(fileStorageService.determineMediaType("file.pdf")).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(fileStorageService.determineMediaType("file.docx"))
                .isEqualTo(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }
}
