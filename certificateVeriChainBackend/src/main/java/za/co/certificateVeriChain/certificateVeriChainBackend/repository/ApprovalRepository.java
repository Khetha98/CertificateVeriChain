package za.co.certificateVeriChain.certificateVeriChainBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.PendingApprovalDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.GovernanceApproval;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository
        extends JpaRepository<GovernanceApproval, Long> {


    @Query("""
    select new za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.PendingApprovalDto(
        c.certificateUid,
        c.studentName,
        t.templateName,
        c.issuedAt,
        issuer.fullName
    )
    from GovernanceApproval ga
    join Certificate c on c.certificateUid = ga.certificateUid
    join CertificateTemplate t on t.id = c.template.id
    join User issuer on issuer.id = c.issuedBy.id
    where ga.approverUserId = :userId
      and ga.approved = false
""")
    List<PendingApprovalDto> findPendingForUser(Long userId);


    Optional<GovernanceApproval>
    findByCertificateUidAndApproverUserId(Long uid, Long userId);

    long countByCertificateUid(Long uid);

    long countByCertificateUidAndApprovedTrue(Long uid);
}
