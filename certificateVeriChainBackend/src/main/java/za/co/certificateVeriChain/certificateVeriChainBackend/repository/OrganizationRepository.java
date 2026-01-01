package za.co.certificateVeriChain.certificateVeriChainBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Organization;

import java.util.List;
@Repository
public interface OrganizationRepository extends JpaRepository< Organization, Long> {
    List<Organization> findByStatus(String status);
}
