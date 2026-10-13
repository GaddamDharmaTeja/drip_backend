package com.dripgrow.api.controller;

import com.dripgrow.api.service.FileStorageService;
import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/api/admin/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        return fileStorageService.storeImage(file);
    }

    @GetMapping("/api/files/{id}")
    public ResponseEntity<InputStreamResource> get(@PathVariable String id) throws IOException {
        FileStorageService.LoadedFile loaded = fileStorageService.load(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000")
                .contentType(MediaType.parseMediaType(loaded.contentType()))
                .contentLength(loaded.length())
                .body(new InputStreamResource(loaded.resource().getInputStream()));
    }

    @DeleteMapping("/api/admin/uploads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        fileStorageService.delete(id);
    }
}
