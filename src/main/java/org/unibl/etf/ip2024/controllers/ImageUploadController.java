package org.unibl.etf.ip2024.controllers;

import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class ImageUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            ensureUploadPathExists();
            String fileExtension = getFileExtension(file.getOriginalFilename());
            BufferedImage resizedImage = resizeImage(file);
            String fileName = saveResizedImage(file, resizedImage, fileExtension);

            return ResponseEntity.ok("/uploads/" + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Greška prilikom čuvanja fajla!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private void ensureUploadPathExists() throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage srcImage = ImageIO.read(file.getInputStream());
        return Scalr.resize(srcImage, Scalr.Method.QUALITY, 800, 600);
    }

    private String saveResizedImage(MultipartFile file, BufferedImage resizedImage, String fileExtension) throws IOException {
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename(), "Neispravno ime slike.");
        String modifiedFilename = originalFilename.replaceAll("\\s+", "_");
        String fileName = UUID.randomUUID() + "_" + modifiedFilename;
        Path destFile = Paths.get(uploadPath).resolve(fileName);
        ImageIO.write(resizedImage, Objects.requireNonNull(fileExtension), destFile.toFile());
        return fileName;
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
