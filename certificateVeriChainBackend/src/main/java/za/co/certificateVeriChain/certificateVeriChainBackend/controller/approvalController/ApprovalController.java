package za.co.certificateVeriChain.certificateVeriChainBackend.controller.approvalController;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.PendingApprovalDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.ApprovalRepository;

import java.util.List;

@RestController
@RequestMapping("/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalRepository approvalRepo;

    @GetMapping("/pending")
    public List<PendingApprovalDto> pending(
            @AuthenticationPrincipal User user
    ) {
        return approvalRepo.findPendingForUser(user.getId());
    }
}

