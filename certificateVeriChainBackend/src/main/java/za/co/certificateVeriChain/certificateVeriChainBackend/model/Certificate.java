package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long certificateUid;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private CertificateTemplate template;

    @Column(nullable = false)
    private String studentName;

    /* 🔐 Blockchain integrity */
    @Column(nullable = false, unique = true, length = 64)
    private String certificateHash;

    private String txHash;

    /* 🔁 Lifecycle */
    private String status;     // ISSUED, REVOKED
    private String issuedAt;

    /* 🔍 Public verification */
    @Column(nullable = false, unique = true)
    private String verificationCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "issued_by_user_id")
    private User issuedBy;
}

