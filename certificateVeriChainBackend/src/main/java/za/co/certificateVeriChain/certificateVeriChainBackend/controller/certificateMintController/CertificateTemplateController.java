package za.co.certificateVeriChain.certificateVeriChainBackend.controller.certificateMintController;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.response.CertificateTemplateDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.CertificateTemplate;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.CertificateTemplateRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.certificate.FileStorageService;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class CertificateTemplateController {

    private final CertificateTemplateRepository repo;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CertificateTemplateDto create(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {

        String fileUrl = fileStorageService.uploadTemplate(
                file,
                user.getOrganization().getId()
        );

        CertificateTemplate t = new CertificateTemplate();
        t.setTemplateName(name);
        t.setDescription(description);
        t.setFileUrl(fileUrl);
        t.setOrganization(user.getOrganization());
        t.setCreatedAt(Instant.now().toString());

        CertificateTemplate saved = repo.save(t);

        return new CertificateTemplateDto(
                saved.getId(),
                saved.getTemplateName(),
                saved.getDescription(),
                saved.getFileUrl(),
                saved.getCreatedAt()
        );
    }


    @GetMapping
    public List<CertificateTemplateDto> list(
            @AuthenticationPrincipal User user
    ) {
        return repo.findByOrganizationId(user.getOrganization().getId())
                .stream()
                .map(t -> new CertificateTemplateDto(
                        t.getId(),
                        t.getTemplateName(),
                        t.getDescription(),
                        t.getFileUrl(),
                        t.getCreatedAt()
                ))
                .toList();
    }


}



