package com.sau.bankingmanagementgradle.servicesForUser.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileUploadService {
    private static final String UPLOAD_DIR = "uploads";

    public String uploadFile(MultipartFile file) throws IOException {
        // Dosyayı sunucuya kaydet
        byte[] bytes = file.getBytes();
        Path path = Paths.get(UPLOAD_DIR + "/" + file.getOriginalFilename());
        Files.write(path, bytes);

        // Dosya URL'sini döndür
        return path.toUri().toString();
    }
}
