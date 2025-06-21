package api.jwt.service;

import api.jwt.model.User;
import api.jwt.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // Injeta o serviço de JWT

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Autentica um usuário e, se bem-sucedido, gera e retorna um token JWT.
     * @param username Nome de usuário.
     * @param password Senha em texto claro.
     * @return O token JWT.
     * @throws BadCredentialsException Se as credenciais forem inválidas.
     */
    public String authenticateUserAndGenerateToken(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Credenciais inválidas: Usuário não encontrado.");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Credenciais inválidas: Senha incorreta.");
        }

        return jwtService.generateToken(user.getUsername(), user.getRole());
    }
}