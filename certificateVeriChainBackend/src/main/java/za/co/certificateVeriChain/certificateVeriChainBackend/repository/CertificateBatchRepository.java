package za.co.certificateVeriChain.certificateVeriChainBackend.repository;

import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.CertificateBatch;

import java.util.Optional;

@Repository
public interface CertificateBatchRepository extends JpaRepository<CertificateBatch, Long> {
    Optional<CertificateBatch> findByMerkleRoot(String merkleRoot);
}
