package xyz.haofamily.susie.auth.token;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

public abstract class AbstractTokenGranter implements TokenGranter {

  private TokenService tokenService;

  protected abstract Authentication validateParameters(Map<String, String> parameters)
      throws IllegalTokenRequestException;

  @Override
  public AccessToken grant(Map<String, String> parameters) throws IllegalTokenRequestException {
    Authentication authentication = validateParameters(parameters);
    return tokenService.create(authentication);
  }

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }

}
