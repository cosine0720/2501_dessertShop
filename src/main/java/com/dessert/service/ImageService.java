package com.dessert.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageService {
    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public String saveImage(MultipartFile imageFile) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("圖片保存失敗", e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            Path imagePath = Paths.get(UPLOAD_DIR, imageUrl.replace("/uploads/", ""));
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            throw new RuntimeException("刪除圖片失敗", e);
        }
    }
}
