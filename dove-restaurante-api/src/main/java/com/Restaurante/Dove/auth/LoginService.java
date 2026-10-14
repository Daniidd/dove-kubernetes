package com.Restaurante.Dove.auth;

import com.Restaurante.Dove.model.UsuarioEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public String logar(Login login) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword());
        
        try {
            var auth = this.authenticationManager.authenticate(usernamePassword);
            return tokenService.gerarToken((UsuarioEntity) auth.getPrincipal());
        } catch (Exception e) {
            throw new RuntimeException("Credenciais inválidas: " + e.getMessage());
        }
    }
}
