package xyz.haofamily.susie.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;

import xyz.haofamily.susie.auth.token.AccessToken;
import xyz.haofamily.susie.auth.token.IllegalTokenRequestException;
import xyz.haofamily.susie.auth.token.TokenGranter;

class AuthenticationControllerTest {

  @Mock
  private TokenGranter tokenGranter;

  @Mock
  private ApplicationContext ctx;

  @InjectMocks
  private AuthenticationController authenticationController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateTokenWithValidGrantType() {
    // Arrange
    Map<String, String> parameters = new HashMap<>();
    AccessToken accessToken = new AccessToken();
    parameters.put("grant_type", "password");
    when(tokenGranter.grant(anyMap())).thenReturn(accessToken);
    when(ctx.getBean("password", TokenGranter.class)).thenReturn(tokenGranter);

    // Act
    ResponseEntity<?> response = authenticationController.createToken(parameters);

    // Assert
    assertEquals(ResponseEntity.ok(accessToken), response);
    verify(tokenGranter).grant(parameters);
  }

  @Test
  void testCreateTokenWithInvalidGrantType() {
    // Arrange
    Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "invalid");
    when(ctx.getBean("password", TokenGranter.class)).thenReturn(null);

    // Act
    ResponseEntity<?> response = authenticationController.createToken(parameters);

    // Assert
    assertEquals(ResponseEntity.badRequest().body("Invalid grant type"), response);
    verify(tokenGranter, never()).grant(parameters);
  }

  @Test
  void testCreateTokenWithImplicitGrantType() {
    // Arrange
    Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "implicit");

    // Act
    ResponseEntity<?> response = authenticationController.createToken(parameters);

    // Assert
    assertEquals(ResponseEntity.badRequest().body("Implicit grant type not supported from token endpoint"), response);
    verify(tokenGranter, never()).grant(parameters);
  }

  @Test
  void testCreateTokenWithException() {
    // Arrange
    Map<String, String> parameters = new HashMap<>();
    parameters.put("grant_type", "password");
    when(ctx.getBean("password", TokenGranter.class)).thenReturn(tokenGranter);
    when(tokenGranter.grant(anyMap())).thenThrow(new IllegalTokenRequestException("Invalid request"));

    // Act
    ResponseEntity<?> response = authenticationController.createToken(parameters);

    // Assert
    assertEquals(ResponseEntity.badRequest().body("Invalid request"), response);
    verify(tokenGranter).grant(parameters);
  }
}