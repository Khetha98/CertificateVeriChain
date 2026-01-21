package za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3;
    private static final String BUCKET = "certificate-templates";

    public FileStorageService() {
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:9000")) // ✅ API port
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("minioadmin", "minioadmin")
                        )
                )
                .forcePathStyle(true) // ✅ REQUIRED for MinIO
                .build();
    }

    public String uploadTemplate(MultipartFile file, Long orgId) throws IOException {

        String key = "templates/" + orgId + "/" + UUID.randomUUID() + ".pdf";

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return key; // ✅ STORE KEY ONLY
    }


    public byte[] download(String key) {
        return s3.getObject(
                b -> b.bucket(BUCKET).key(key),
                ResponseTransformer.toBytes()
        ).asByteArray();
    }

    public byte[] downloadTemplate(String key) {
        return s3.getObject(
                GetObjectRequest.builder()
                        .bucket(BUCKET)
                        .key(key)
                        .build(),
                ResponseTransformer.toBytes()
        ).asByteArray();
    }

}


