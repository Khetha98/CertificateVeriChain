package za.co.certificateVeriChain.certificateVeriChainBackend.service.logServce;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.ActivityLog;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.ActivityLogRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repo;

    public void log(User user, String action, String details) {
        ActivityLog log = new ActivityLog();
        log.setOrganizationId(user.getOrganization().getId());
        log.setUserId(user.getId());
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(Instant.now().toString());
        repo.save(log);
    }
}

