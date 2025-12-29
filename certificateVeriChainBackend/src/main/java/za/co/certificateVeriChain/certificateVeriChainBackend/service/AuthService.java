package za.co.certificateVeriChain.certificateVeriChainBackend.service;

import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.LoginUserDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.dtos.request.RegisterUserDto;
import za.co.certificateVeriChain.certificateVeriChainBackend.model.User;

public interface AuthService {
    public User register(RegisterUserDto input);
    public User authenticate(LoginUserDto input);
}
