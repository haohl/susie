package xyz.haofamily.susie.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "susie.security.auth")
public class AuthenticationConfigProperties {

  private AuthenticationType type;

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

  }

}
