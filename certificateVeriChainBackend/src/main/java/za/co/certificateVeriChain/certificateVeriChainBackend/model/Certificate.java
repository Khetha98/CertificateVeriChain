package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;

@Entity
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long certificateUid;
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
    @ManyToOne
    @JoinColumn(name = "template_id")
    private CertificateTemplate template;
    private String studentName;
}
