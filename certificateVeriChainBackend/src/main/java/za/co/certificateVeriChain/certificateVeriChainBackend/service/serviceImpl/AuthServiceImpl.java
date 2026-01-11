package za.co.certificateVeriChain.certificateVeriChainBackend.service.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.LoginUserDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.RegisterInstitutionDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.Organization;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.OrganizationRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.UserRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.AuthService;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final OrganizationRepository  organizationRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    @Transactional
    public User register(RegisterInstitutionDto input) {

        Organization org =
                organizationRepository.findByRegistrationNumber(input.getRegistrationNumber());

        // FIRST TIME: create organization
        if (org == null) {
            org = new Organization();
            org.setName(input.getInstitutionName());
            org.setRegistrationNumber(input.getRegistrationNumber());
            org.setStatus("PENDING");

            organizationRepository.save(org);
        }
        // OPTIONAL SAFETY: prevent duplicate admins
        else {
            boolean adminExists = userRepository.existsByOrganizationAndRole(
                    org, "INSTITUTION_ADMIN"
            );

            if (adminExists) {
                throw new IllegalStateException(
                        "Organization already has an admin. Invite users instead."
                );
            }
        }

        User user = new User();
        user.setFullName(input.getInstitutionName() + " Admin");
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole("INSTITUTION_ADMIN");
        user.setStatus("PENDING");
        user.setOrganization(org);
        user.setDateJoined(LocalDateTime.now().toString());

        return userRepository.save(user);
    }



    @Override
    public User authenticate(LoginUserDto input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );

            return userRepository.findByEmail(input.getEmail())
                    .orElseThrow();
        }catch(BadCredentialsException e){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,   // ← correct status
                    "Invalid email or password"
            );
        }
    }
}

