package xyz.haofamily.susie.auth.token;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractTokenGranter implements TokenGranter {

  private TokenService tokenService;

  protected abstract void validateParameters(Map<String, String> parameters) throws IllegalTokenRequestException;

  @Override
  public AccessToken grant(Map<String, String> parameters) throws IllegalTokenRequestException {
    validateParameters(parameters);
    return tokenService.create(parameters);
  }

  @Autowired
  public void setTokenService(TokenService tokenService) {
    this.tokenService = tokenService;
  }

}
