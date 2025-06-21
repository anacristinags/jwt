package api.jwt;

import api.jwt.repository.UserRepository;
import api.jwt.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.findByUsername("admin").ifPresentOrElse(
                user -> { /* Já existe */ },
                () -> {
                    api.jwt.model.User admin = new api.jwt.model.User(null, "admin", passwordEncoder.encode("123456"), "ROLE_ADMIN");
                    userRepository.save(admin);
                }
        );
        userRepository.findByUsername("user").ifPresentOrElse(
                user -> { /* Já existe */ },
                () -> {
                    api.jwt.model.User regularUser = new api.jwt.model.User(null, "user", passwordEncoder.encode("password"), "ROLE_USER");
                    userRepository.save(regularUser);
                }
        );
    }

    @Test
	void testLoginSuccess() throws Exception {
		String token = mockMvc.perform(post("/auth/login")
				.param("username", "admin")
				.param("password", "123456")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isOk())
				.andExpect(content().string(notNullValue()))
				.andReturn().getResponse().getContentAsString();

		assert jwtService.validateToken(token);
	}

    @Test
    void testLoginFailureInvalidPassword() throws Exception {
        mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "senhaErrada")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(containsString("Senha incorreta.")));
    }

    @Test
    void testProtectedEndpointAccessDeniedWithoutToken() throws Exception {
        mockMvc.perform(get("/api/hello"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void testProtectedEndpointAccessWithValidToken() throws Exception {
        String token = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(content().string("Olá! Você acessou um endpoint protegido com sucesso!"));
    }

    @Test
    void testProtectedAdminEndpointAccessWithAdminToken() throws Exception {
        String adminToken = mockMvc.perform(post("/auth/login")
                .param("username", "admin")
                .param("password", "123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + adminToken))
               .andExpect(status().isOk())
               .andExpect(content().string("Bem-vindo, Administrador! Este é um recurso restrito."));
    }

    @Test
    void testProtectedAdminEndpointAccessDeniedWithUserToken() throws Exception {
        String userToken = mockMvc.perform(post("/auth/login")
                .param("username", "user")
                .param("password", "password")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
               .andExpect(status().isOk())
               .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + userToken))
               .andExpect(status().isForbidden());
    }

    // Mais testes de validação
    // Testar token expirado (simulado manualmente)
    @Test
    void testExpiredTokenShouldBeRejected() throws Exception {
    // Alterar a expiração manualmente (só para simular)
        String fakeExpiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                "eyJzdWIiOiJ1c2VyIiwicm9sZSI6IlJPTEVfVVNFUiIsImlhdCI6MTYwOTAwMDAwMCwiZXhwIjoxNjA5MDAwMDAxfQ." +
                "fake-signature";

        mockMvc.perform(get("/api/hello")
                .header("Authorization", "Bearer " + fakeExpiredToken))
            .andExpect(status().isUnauthorized());
    }

    // Testar token com role inválida (não reconhecida)
    @Test
    void testTokenWithInvalidRoleShouldBeForbidden() throws Exception {
        // Gera token com role que não existe no sistema
        String token = jwtService.generateToken("user", "ROLE_UNKNOWN");

        mockMvc.perform(get("/api/admin")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
    }

    // Testar acesso ao endpoint público sem autenticação
    @Test
    void testPublicEndpointAccess() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk());
    }

    // Testar login com usuário inexistente
    @Test
    void testTokenContainsUsernameAndRoleClaim() throws Exception {
        String token = jwtService.generateToken("admin", "ROLE_ADMIN");

        var claims = jwtService.getAllClaimsFromToken(token);
        
        assert claims.get("role").equals("ROLE_ADMIN");
        assert jwtService.getUsernameFromToken(token).equals("admin");
    }

    
}