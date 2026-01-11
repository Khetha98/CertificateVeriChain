package za.co.certificateVeriChain.certificateVeriChainBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Organization;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByOrganizationId(Long id);
    boolean existsByOrganizationAndRole(Organization org, String role);
}
