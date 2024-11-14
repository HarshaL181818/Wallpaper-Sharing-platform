package com.wallpaper.wsp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
public class S3Service {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // Prepare metadata
        System.out.println("Upload request received");

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // Upload file to S3 bucket
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));

        // Return the URL of the uploaded file
        return s3Client.getUrl(bucketName, fileName).toString();
    }
}
