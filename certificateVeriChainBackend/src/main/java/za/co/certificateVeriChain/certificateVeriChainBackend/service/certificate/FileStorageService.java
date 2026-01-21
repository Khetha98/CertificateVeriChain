package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3;

    @Value("${cloud.aws.s3.bucket:certificate-templates}")
    private String bucketName;

    public FileStorageService(
            @Value("${cloud.aws.s3.endpoint}") String endpoint,
            @Value("${cloud.aws.region.static}") String region,
            @Value("${cloud.aws.credentials.accessKey}") String accessKey,
            @Value("${cloud.aws.credentials.secretKey}") String secretKey
    ) {
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .forcePathStyle(true)
                .build();
    }

    public String uploadTemplate(MultipartFile file) {
        String bucketName = "certificates"; // or use your @Value variable

        // 1. Check if bucket exists, create if not
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            System.out.println("Created missing bucket: " + bucketName);
        }

        // 2. Proceed with upload
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .build();

            s3.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return "File uploaded successfully";
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    // Fix: Re-added the download method used by CertificateService
    public byte[] download(String key) {
        return s3.getObject(
                b -> b.bucket(bucketName).key(key),
                ResponseTransformer.toBytes()
        ).asByteArray();
    }

    // Kept for the Controller
    public byte[] downloadTemplate(String key) {
        return s3.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes()
        ).asByteArray();
    }
}