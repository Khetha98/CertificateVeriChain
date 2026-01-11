package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class ActivityLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long organizationId;
    private Long userId;
    private String action;
    private String details;
    private String createdAt;
}

