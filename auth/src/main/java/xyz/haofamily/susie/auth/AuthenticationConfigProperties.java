package xyz.haofamily.susie.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
@ConfigurationProperties(prefix = "susie.security.auth")
public class AuthenticationConfigProperties {

  private AuthenticationType type = AuthenticationType.UUID;

  private List<AuthenticationProviderType> providers = new ArrayList<>();

  private TokenConfig token = new TokenConfig();

  public AuthenticationType getType() {
    return type;
  }

  public void setType(AuthenticationType type) {
    this.type = type;
  }

  public List<AuthenticationProviderType> getProviders() {
    return providers;
  }

  public void setProviders(List<AuthenticationProviderType> providers) {
    this.providers = providers;
  }

  public TokenConfig getToken() {
    return token;
  }

  public void setToken(TokenConfig token) {
    this.token = token;
  }

  public static class TokenConfig {
    private String type = "Bearer";
    private int ttl = 900;
    private int refreshTtl = 24 * 60 * 60;
    private JwtTokenConfig jwt = new JwtTokenConfig();

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public int getTtl() {
      return ttl;
    }

    public void setTtl(int ttl) {
      this.ttl = ttl;
    }

    public int getRefreshTtl() {
      return refreshTtl;
    }

    public void setRefreshTtl(int refreshTtl) {
      this.refreshTtl = refreshTtl;
    }

    public JwtTokenConfig getJwt() {
      return jwt;
    }

    public void setJwt(JwtTokenConfig jwt) {
      this.jwt = jwt;
    }

  }

  public static class JwtTokenConfig {
    private JwtSignAlg alg = JwtSignAlg.HmacSHA;
    private String issuer = "Susie-Authentication-Service";
    private String secret;
    private String keyAlias;
    private Resource keyStore;
    private String keyStorePassword;
    private String keyPassword;

    public String getSecret() {
      return secret;
    }

    public void setSecret(String secret) {
      this.secret = secret;
    }

    public String getKeyAlias() {
      return keyAlias;
    }

    public void setKeyAlias(String keyAlias) {
      this.keyAlias = keyAlias;
    }

    public Resource getKeyStore() {
      return keyStore;
    }

    public void setKeyStore(Resource keyStore) {
      this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
      return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
      this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPassword() {
      return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
      this.keyPassword = keyPassword;
    }

    public JwtSignAlg getAlg() {
      return alg;
    }

    public void setAlg(JwtSignAlg alg) {
      this.alg = alg;
    }

    public String getIssuer() {
      return issuer;
    }

    public void setIssuer(String issuer) {
      this.issuer = issuer;
    }

  }

  public static enum JwtSignAlg {
    HmacSHA, RSA
  }

}
