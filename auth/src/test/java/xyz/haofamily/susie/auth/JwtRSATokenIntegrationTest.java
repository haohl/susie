package xyz.haofamily.susie.auth;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest(properties = { "susie.security.auth.type=JWT",
        "susie.security.auth.token.jwt.alg=RSA",
        "susie.security.auth.token.jwt.keyAlias=susie-test",
        "susie.security.auth.token.jwt.keyStore=classpath:susie-test.jks",
        "susie.security.auth.token.jwt.keyStorePassword=susie-test",
        "susie.security.auth.token.jwt.keyPassword=susie-test" })
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class JwtRSATokenIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${susie.security.auth.token.jwt.keyStore}")
    private Resource keyStoreResource;

    private Key publicKey;

    @BeforeEach
    public void setUp() {
        String sql = "insert into basic_user (id, username, password, disabled) values (?, ?, ?, false)";
        entityManager.createNativeQuery(sql)
                .setParameter(1, 1).setParameter(2, "admin").setParameter(3, passwordEncoder.encode("admin"))
                .executeUpdate();
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(keyStoreResource.getInputStream(), "susie-test".toCharArray());
            publicKey = ks.getCertificate("susie-test").getPublicKey();
        } catch (Exception e) {
            throw new IllegalStateException("Can't load the key from the key store.", e);
        }
    }

    @Test
    public void testVerifyWithPublicKey() throws Exception {
        Calendar now = Calendar.getInstance();
        Calendar start = (Calendar) now.clone();
        start.add(Calendar.SECOND, 900);
        Date expected = start.getTime();
        MvcResult result = mockMvc
                .perform(post("/api/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("username=admin&password=admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(900))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        String accessTokenStr = JsonPath.parse(responseContent).read("$.access_token");
        Jwt<?, ?> jwt = Jwts.parser().verifyWith((PublicKey) publicKey).build().parse(accessTokenStr);
        assertThat(jwt)
                .hasFieldOrProperty("header")
                .extracting("header")
                .hasFieldOrPropertyWithValue("typ", "JWT")
                .extracting("alg").asString().matches("RS\\d{3}");
        Claims claims = (Claims) jwt.getPayload();
        assertThat(claims)
                .containsKey("exp")
                .hasFieldOrPropertyWithValue("iss", "Susie-Authentication-Service")
                .hasFieldOrPropertyWithValue("sub", "admin");
        assertThat(claims.getIssuedAt()).isCloseTo(now.getTime(), 5000);
        assertThat(claims.getExpiration()).isCloseTo(expected, 5000);
    }

    @Test
    public void testVerifyJWTWithIncorrectPublicKey() throws Exception {
        MvcResult result = mockMvc
                .perform(post("/api/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("username=admin&password=admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(900))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        String accessTokenStr = JsonPath.parse(responseContent).read("$.access_token");
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        assertThatExceptionOfType(SignatureException.class)
                .isThrownBy(() -> Jwts.parser().verifyWith((PublicKey) pair.getPublic()).build().parse(accessTokenStr));
    }
}
