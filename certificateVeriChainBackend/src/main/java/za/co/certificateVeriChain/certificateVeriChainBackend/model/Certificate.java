package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.certificateVeriChain.certificateVeriChainBackend.enums.CertificateType;

import java.time.Instant;

@Entity
@Getter
@Setter
@ToString(exclude = {"organization", "template", "issuedBy"})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 🔑 Public identifier */
    @Column(nullable = false, unique = true, updatable = false)
    private String certificateUid;

    /* 🏫 Ownership */
    @ManyToOne(optional = false)
    private Organization organization;

    @ManyToOne(optional = false)
    private CertificateTemplate template;

    @ManyToOne(optional = false)
    private User issuedBy;

    /* 👤 Student identity */
    @Column(nullable = false)
    private String studentName;

    private String studentSurname;

    @Column(nullable = false)
    private String studentIdentifier;
    // student number / email / national ID hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificateType certificateType;

    /* 🔐 Integrity */
    @Column(nullable = false, unique = true, length = 64)
    private String certificateHash;

    private String txHash;

    /* 🔁 Lifecycle */
    private String status; // PENDING_APPROVAL, APPROVED, ACTIVE, REVOKED

    private Instant issuedAt;

    /* 🔍 Public verification */
    @Column(nullable = false, unique = true)
    private String verificationCode;

    @ManyToOne
    private CertificateBatch batch;

    @Column(length = 2000)
    private String merkleProof; // JSON array
}




