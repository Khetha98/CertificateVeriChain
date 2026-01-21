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

    public String uploadTemplate(MultipartFile file, Long organizationId) throws IOException {
        // Use a consistent bucket name
        String dynamicBucketName = "certificates-" + organizationId;

        // Create bucket if missing
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(dynamicBucketName).build());
        } catch (NoSuchBucketException e) {
            s3.createBucket(CreateBucketRequest.builder().bucket(dynamicBucketName).build());
        }

        String fileName = file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(dynamicBucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // IMPORTANT: Return the fileName (key), NOT a success message
        return fileName;
    }

    // Replace your old download/downloadTemplate methods with this:
    public byte[] downloadTemplate(String key, Long organizationId) {
        String dynamicBucketName = "certificates-" + organizationId;

        return s3.getObject(
                GetObjectRequest.builder()
                        .bucket(dynamicBucketName)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes()
        ).asByteArray();
    }
}