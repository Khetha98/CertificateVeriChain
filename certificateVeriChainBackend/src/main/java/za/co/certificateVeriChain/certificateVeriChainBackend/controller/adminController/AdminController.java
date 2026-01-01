package za.co.certificateVeriChain.certificateVeriChainBackend.controller.adminController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Organization;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.OrganizationRepository;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    OrganizationRepository organizationRepository;
    @GetMapping("/institutions/pending")
    public List<Organization> pendingInstitutions() {
        return organizationRepository.findByStatus("PENDING");
    }

    @PostMapping("/institutions/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable Long id) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow();

        org.setStatus("ACTIVE");

        org.getUsers().forEach(user -> user.setStatus("ACTIVE"));

        organizationRepository.save(org);

        return ResponseEntity.ok("Institution approved");
    }

    @PostMapping("/institutions/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable Long id) {

        Organization org = organizationRepository.findById(id)
                .orElseThrow();

        org.setStatus("REJECTED");
        org.getUsers().forEach(user -> user.setStatus("REJECTED"));

        organizationRepository.save(org);

        return ResponseEntity.ok("Institution rejected");
    }


}
