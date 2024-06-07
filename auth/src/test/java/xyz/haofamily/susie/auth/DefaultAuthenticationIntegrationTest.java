package xyz.haofamily.susie.auth;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@ContextConfiguration(classes = TestConfiguration.class)
@Transactional
public class DefaultAuthenticationIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  public void setUp() {
    String sql = "insert into basic_user (id, username, password, disabled) values (?, ?, ?, false), (?, ?, ?, true)";
    entityManager.createNativeQuery(sql)
        .setParameter(1, 1).setParameter(2, "admin").setParameter(3, passwordEncoder.encode("admin"))
        .setParameter(4, 2).setParameter(5, "user").setParameter(6, passwordEncoder.encode("user"))
        .executeUpdate();
  }

  @Test
  public void testAuthentication() throws Exception {
    Calendar now = Calendar.getInstance();
    Calendar start = (Calendar) now.clone();
    start.add(Calendar.SECOND, 900);
    Date expectedStart = start.getTime();
    Calendar end = (Calendar) now.clone();
    end.add(Calendar.SECOND, 901);
    Date expectedEnd = end.getTime();
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
    String refreshTokenStr = JsonPath.parse(responseContent).read("$.refresh_token");

    String sql = "select id,token,token_type,expiration,refresh_token_id,scope,username,client_id from access_token where username = ?";
    Object[] accessToken = (Object[]) entityManager.createNativeQuery(sql)
        .setParameter(1, "admin").getSingleResult();
    assertThat(accessToken[1]).isEqualTo(accessTokenStr);
    assertThat(accessToken[2]).isEqualTo("Bearer");
    assertThat(accessToken[6]).isEqualTo("admin");
    assertThat((Date) accessToken[3])
        .isAfterOrEqualTo(expectedStart)
        .isBeforeOrEqualTo(expectedEnd);

    String sql1 = "select id,token,expiration from refresh_token where id = ?";
    Object[] refreshToken = (Object[]) entityManager.createNativeQuery(sql1)
        .setParameter(1, accessToken[4]).getSingleResult();

    start = (Calendar) now.clone();
    start.add(Calendar.DATE, 1);
    expectedStart = start.getTime();
    end = (Calendar) now.clone();
    end.add(Calendar.DATE, 1);
    end.add(Calendar.SECOND, 1);
    expectedEnd = end.getTime();
    assertThat(refreshToken[1]).isEqualTo(refreshTokenStr);
    assertThat((Date) refreshToken[2])
        .isAfterOrEqualTo(expectedStart)
        .isBeforeOrEqualTo(expectedEnd);
  }

  @Test
  public void testAuthenticationWithEmptyInfo() throws Exception {
    mockMvc
        .perform(post("/api/token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("username=&password="))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testAuthenticationWithDisabledUser() throws Exception {
    mockMvc
        .perform(post("/api/token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("username=user&password=user"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testAuthenticationWithWrongPassword() throws Exception {
    mockMvc
        .perform(post("/api/token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("username=admin&password=pwd"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testAuthenticationWithNonExistUser() throws Exception {
    mockMvc
        .perform(post("/api/token")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content("username=root&password=root"))
        .andExpect(status().isBadRequest());
  }
}
