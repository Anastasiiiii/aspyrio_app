package com.aspyrio_app.backend.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@ConditionalOnBean(S3Client.class)
@RequiredArgsConstructor
public class S3FileUploadService {
    private final S3Client s3Client;

    @Autowired(required = false)
    private S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    
    @Value("${aws.region}")
    private String region;

    public String uploadFile(MultipartFile file, String folder) throws IOException {
        return uploadFile(file, folder, true);
    }

    public String uploadFile(MultipartFile file, String folder, boolean allowImagesOnly) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        if (allowImagesOnly) {
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
        } else {
            if (contentType == null || (!contentType.startsWith("text/") && !contentType.equals("application/octet-stream"))) {
                String lowerFilename = originalFilename.toLowerCase();
                if (!lowerFilename.endsWith(".txt")) {
                    throw new IllegalArgumentException("Only .txt files are allowed");
                }
            }
        }

        String fileExtension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileExtension = originalFilename.substring(lastDotIndex);
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        String key = folder + "/" + uniqueFileName;

        String finalContentType = contentType;
        if (!allowImagesOnly && (contentType == null || contentType.equals("application/octet-stream"))) {
            finalContentType = "text/plain"; // Default for txt files
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(finalContentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key;
        } catch (S3Exception e) {
            throw new IOException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key != null) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                s3Client.deleteObject(deleteRequest);
            }
        } catch (S3Exception e) {
            System.err.println("Failed to delete file from S3: " + e.getMessage());
        }
    }

    public String extractKeyFromUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }
        
        try {
            if (!url.startsWith("http")) {
                return url;
            }
            
            String prefix1 = "https://" + bucketName + ".s3." + region + ".amazonaws.com/";
            String prefix2 = "https://" + bucketName + ".s3.amazonaws.com/";
            
            if (url.startsWith(prefix1)) {
                String key = url.substring(prefix1.length());
                int queryIndex = key.indexOf('?');
                return queryIndex > 0 ? key.substring(0, queryIndex) : key;
            } else if (url.startsWith(prefix2)) {
                String key = url.substring(prefix2.length());
                int queryIndex = key.indexOf('?');
                return queryIndex > 0 ? key.substring(0, queryIndex) : key;
            }
            
            if (url.contains(bucketName) && url.contains(".amazonaws.com/")) {
                int bucketIndex = url.indexOf(bucketName + ".s3");
                if (bucketIndex >= 0) {
                    int keyStart = url.indexOf("/", bucketIndex + bucketName.length() + 3);
                    if (keyStart > 0) {
                        String keyWithQuery = url.substring(keyStart + 1);
                        int queryIndex = keyWithQuery.indexOf('?');
                        return queryIndex > 0 ? keyWithQuery.substring(0, queryIndex) : keyWithQuery;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to extract key from url: " + e.getMessage());
        }
        
        return null;
    }

    public String getPresignedUrl(String fileKey) {
        if (fileKey == null || fileKey.trim().isEmpty()) {
            return null;
        }

        if (s3Presigner == null) {
            throw new IllegalStateException("S3Presigner is not available. Cannot generate presigned URL.");
        }

        try {
            String key = extractKeyFromUrl(fileKey);
            if (key == null) {
                key = fileKey;
            }

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            System.err.println("Failed to generate presigned URL: " + e.getMessage());
            return null;
        }
    }
}

