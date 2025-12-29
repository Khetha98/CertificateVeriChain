package za.co.certificateVeriChain.certificateVeriChainBackend.service.serviceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.LoginUserDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.RegisterUserDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;
import za.co.certificateVeriChain.certificateVeriChainBackend.repository.UserRepository;
import za.co.certificateVeriChain.certificateVeriChainBackend.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService
{
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(RegisterUserDto input) {
        User user = new User();
                user.setFullName(input.getFullName());
                user.setEmail(input.getEmail());
                user.setPassword(passwordEncoder.encode(input.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }
}

