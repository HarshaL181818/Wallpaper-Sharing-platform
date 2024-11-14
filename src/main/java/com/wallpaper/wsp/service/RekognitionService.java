package com.wallpaper.wsp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.IOException;
import java.util.*;

@Service
public class RekognitionService {

    private final RekognitionClient rekognitionClient;

    // Define categories and corresponding labels
    private static final Map<String, List<String>> CATEGORIES = new HashMap<>();

    static {
        CATEGORIES.put("Nudity", List.of("Explicit Nudity", "Suggestive", "Partial Nudity", "Female Swimwear or Underwear", "Partially Exposed Female Breast"));
        CATEGORIES.put("Violence", List.of("Graphic Violence", "Blood & Gore", "Violence"));
        CATEGORIES.put("Drugs", List.of("Drugs", "Tobacco", "Alcohol"));
        CATEGORIES.put("Weapons", List.of("Weapons"));
        CATEGORIES.put("Hate", List.of("Hate Symbols"));
        // Add other categories as needed
    }

    public RekognitionService() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create();
//access key and secret access key
        this.rekognitionClient = RekognitionClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    public boolean isImageSafeWithProgress(MultipartFile file) throws IOException, InterruptedException {
        // Convert MultipartFile to SdkBytes
        SdkBytes imageBytes = SdkBytes.fromInputStream(file.getInputStream());

        // Prepare the image request
        Image image = Image.builder()
                .bytes(imageBytes)
                .build();

        // Call Rekognition's detectModerationLabels API
        DetectModerationLabelsRequest request = DetectModerationLabelsRequest.builder()
                .image(image)
                .minConfidence(80F)
                .build();

        // Print the processing progress every second
        for (int i = 1; i <= 3; i++) {
            System.out.println("Processing Rekognition (Step " + i + "/3)...");
            Thread.sleep(1000);  // Simulate progress delay (for demonstration)
        }

        DetectModerationLabelsResponse response = rekognitionClient.detectModerationLabels(request);
        List<ModerationLabel> labels = response.moderationLabels();

        // Aggregate the detected labels into broader categories
        Set<String> detectedCategories = new HashSet<>();
        for (ModerationLabel label : labels) {
            String labelName = label.name();
            System.out.println("Moderation Label detected: " + labelName);

            // Map the label to its category
            CATEGORIES.forEach((category, categoryLabels) -> {
                if (categoryLabels.contains(labelName)) {
                    detectedCategories.add(category);
                }
            });
        }

        // Check if any unsafe categories were detected
        if (!detectedCategories.isEmpty()) {
            System.out.println("Detected unsafe categories: " + detectedCategories);
            return false; // Image is unsafe due to detected categories
        }

        // Log completion of Rekognition process
        System.out.println("Rekognition processing completed. Image passed safety checks.");
        return true; // Image is safe
    }
    public List<String> getDetectedModerationLabels(MultipartFile file) throws IOException {
        SdkBytes imageBytes = SdkBytes.fromInputStream(file.getInputStream());
        Image image = Image.builder().bytes(imageBytes).build();

        DetectModerationLabelsRequest request = DetectModerationLabelsRequest.builder()
                .image(image)
                .minConfidence(80F)
                .build();

        DetectModerationLabelsResponse response = rekognitionClient.detectModerationLabels(request);
        List<ModerationLabel> labels = response.moderationLabels();

        // Extract and return label names
        return labels.stream().map(ModerationLabel::name).toList();
    }

}
