package za.co.certificateVeriChain.certificateVeriChainBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Certificate;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByVerificationCode(String code);
}
