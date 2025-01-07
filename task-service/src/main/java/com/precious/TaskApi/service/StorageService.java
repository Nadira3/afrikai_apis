package com.precious.TaskApi.service;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, UUID taskId);
    Resource loadAsResource(String filename);
    void deleteFile(String filename);

    void deleteAll();
}
