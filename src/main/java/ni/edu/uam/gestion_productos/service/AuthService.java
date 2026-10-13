package ni.edu.uam.gestion_productos.service;

import ni.edu.uam.gestion_productos.dto.LoginRequest;
import ni.edu.uam.gestion_productos.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest datos) {
        Authentication auth = manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        datos.username(), datos.password()));
        return new LoginResponse(tokenService.crearToken(auth), "Bearer");
    }
}