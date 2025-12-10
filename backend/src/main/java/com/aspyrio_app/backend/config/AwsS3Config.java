package com.aspyrio_app.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

    @Value("${aws.access.key.id:}")
    private String accessKeyId;

    @Value("${aws.secret.access.key:}")
    private String secretAccessKey;

    @Value("${aws.region:eu-north-1}")
    private String region;

    @Value("${aws.s3.bucket.name:}")
    private String bucketName;

    @Bean
    @ConditionalOnExpression("!'${aws.access.key.id:}'.isEmpty() && !'${aws.secret.access.key:}'.isEmpty() && !'${aws.s3.bucket.name:}'.isEmpty()")
    public S3Client s3Client() {
        if (accessKeyId == null || accessKeyId.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS Access Key ID is required but not set.\n"
            );
        }
        if (secretAccessKey == null || secretAccessKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS Secret Access Key is required but not set.\n"
            );
        }
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS S3 Bucket Name is required but not set.\n"
            );
        }

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId.trim(), secretAccessKey.trim())
                        )
                )
                .build();
    }

    @Bean
    @ConditionalOnExpression("!'${aws.access.key.id:}'.isEmpty() && !'${aws.secret.access.key:}'.isEmpty() && !'${aws.s3.bucket.name:}'.isEmpty()")
    public S3Presigner s3Presigner() {
        if (accessKeyId == null || accessKeyId.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS Access Key ID is required but not set.\n"
            );
        }
        if (secretAccessKey == null || secretAccessKey.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS Secret Access Key is required but not set.\n"
            );
        }

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId.trim(), secretAccessKey.trim())
                        )
                )
                .build();
    }

    @Bean
    @ConditionalOnExpression("!'${aws.s3.bucket.name:}'.isEmpty()")
    public String s3BucketName() {
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalStateException(
                "AWS S3 Bucket Name is required but not set."
            );
        }
        return bucketName;
    }
}
