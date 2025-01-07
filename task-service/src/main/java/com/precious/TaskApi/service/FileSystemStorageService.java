package com.precious.TaskApi.service;

import com.precious.TaskApi.config.StorageProperties;
import com.precious.TaskApi.exception.StorageException;
import com.precious.TaskApi.exception.StorageFileNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSystemStorageService implements StorageService {

    private final StorageProperties storageProperties;
    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file, UUID taskId) {
        if (file == null || file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new StorageException("Failed to store file with null filename.");
        }
        String filename = StringUtils.cleanPath(originalFilename);
        // Generate unique filename to prevent collisions
        String uniqueFilename = taskId.toString() + "_" + filename;

        try {
            if (filename.contains("..")) {
                throw new StorageException(
                    "Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(uniqueFilename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
            return uniqueFilename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Could not delete file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}