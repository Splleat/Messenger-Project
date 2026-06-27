package me.splleat.messengerproject.common.config;

import lombok.RequiredArgsConstructor;
import me.splleat.messengerproject.infrastructure.storage.StorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final StorageProperties properties;

    @Bean
    S3Presigner s3Presigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider());

        applyCustomEndpoint(builder);
        return builder.build();
    }

    @Bean
    S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(credentialsProvider());

        applyCustomEndpoint(builder);
        return builder.build();
    }

    private AwsCredentialsProvider credentialsProvider() {
        if (properties.hasStaticCredentials()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.accessKey(), properties.secretKey()));
        }
        return DefaultCredentialsProvider.builder()
                .build();
    }

    private void applyCustomEndpoint(S3Presigner.Builder builder) {
        if (properties.hasCustomEndpoint()) {
            builder.endpointOverride(URI.create(properties.endpoint()))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }
    }

    private void applyCustomEndpoint(S3ClientBuilder builder) {
        if (properties.hasCustomEndpoint()) {
            builder.endpointOverride(URI.create(properties.endpoint()))
                    .serviceConfiguration(S3Configuration.builder()
                            .pathStyleAccessEnabled(true)
                            .build());
        }
    }
}
