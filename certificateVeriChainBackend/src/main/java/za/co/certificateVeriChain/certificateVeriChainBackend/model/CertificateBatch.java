package za.co.certificateVeriChain.certificateVeriChainBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class CertificateBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Merkle root (anchored on-chain)
    private String merkleRoot;

    // Cardano transaction hash
    private String txHash;

    private Instant anchoredAt;
}

