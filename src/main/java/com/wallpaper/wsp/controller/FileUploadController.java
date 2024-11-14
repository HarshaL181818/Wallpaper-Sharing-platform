package com.wallpaper.wsp.controller;

import com.wallpaper.wsp.model.Wallpaper;
import com.wallpaper.wsp.service.RekognitionService;
import com.wallpaper.wsp.service.S3Service;
import com.wallpaper.wsp.service.WallpaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "hello there")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private WallpaperService wallpaperService;

    @Autowired
    private RekognitionService rekognitionService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        try {
            // Logging start of image processing
            System.out.println("Starting image processing...");

            // Use Rekognition to check if the image is safe, with progress logs
            boolean isSafe = rekognitionService.isImageSafeWithProgress(file);

            if (!isSafe) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Image contains explicit or inappropriate content.");
            }

            // Log the successful image check
            System.out.println("Safety checks passsed");

            // Upload the file to S3 and get the URL
            String imageUrl = s3Service.uploadImage(file);

            // Create a new Wallpaper instance and set properties
            Wallpaper wallpaper = new Wallpaper();
            wallpaper.setName(title);
            wallpaper.setDescription(description);
            wallpaper.setImageUrl(imageUrl); // Set the image URL

            // Save the wallpaper instance to the database
            Wallpaper savedWallpaper = wallpaperService.addWallpaper(wallpaper);

            // Log the upload success and return the saved wallpaper as a response
            System.out.println("Image uploaded successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(savedWallpaper);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        }
    }
    @PostMapping("/check-image")
    public ResponseEntity<Map<String, Object>> checkImage(@RequestParam("file") MultipartFile file) throws IOException, InterruptedException {
        boolean isSafe = rekognitionService.isImageSafeWithProgress(file);

        // Here you can get the detected moderation labels if needed.
        Map<String, Object> result = new HashMap<>();
        result.put("isSafe", isSafe);
        result.put("detectedCategories", rekognitionService.getDetectedModerationLabels(file));

        return ResponseEntity.ok(result);
    }
}
