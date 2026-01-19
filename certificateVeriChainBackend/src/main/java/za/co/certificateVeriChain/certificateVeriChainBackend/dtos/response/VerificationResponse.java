package za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class VerificationResponse {

    private boolean valid;
    private String message;

    // Optional public info
    private String studentName;
    private String organizationName;
    private String issuedAt;

    public VerificationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public VerificationResponse(
            boolean valid,
            String message,
            String studentName,
            String organizationName,
            String issuedAt
    ) {
        this.valid = valid;
        this.message = message;
        this.studentName = studentName;
        this.organizationName = organizationName;
        this.issuedAt = issuedAt;
    }
}
