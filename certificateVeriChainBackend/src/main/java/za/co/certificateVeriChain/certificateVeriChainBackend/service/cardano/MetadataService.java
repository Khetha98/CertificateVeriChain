package za.co.certificateVeriChain.certificateVeriChainBackend.service.cardano;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetadataService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Path writeMetadataFile(String certificateHash) {
        try {
            Map<String, Object> metadata = Map.of(
                    "674", Map.of(
                            "certificate_hash", certificateHash,
                            "issuer", "CertificateVeriChain",
                            "version", "1.0"
                    )
            );

            Path file = Files.createTempFile("metadata-", ".json");
            MAPPER.writeValue(file.toFile(), metadata);
            return file;

        } catch (Exception e) {
            throw new RuntimeException("Failed to write metadata file", e);
        }
    }
}


