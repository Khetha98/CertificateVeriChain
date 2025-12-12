package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;

@Entity
public class CertificateTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
    private String templateName;
    private String description;
    private String fileUrl;
    private String createdAt;

}
