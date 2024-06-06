package xyz.haofamily.susie.auth.token;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import xyz.haofamily.susie.auth.AuthenticationConfigProperties;

/**
 * 
 * <p>
 * TokenService
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-05
 */
@Service
public class TokenService {

  private AuthenticationConfigProperties properties;
  private AccessTokenRepository accessTokenRepository;
  private RefreshTokenRepository refreshTokenRepository;

  public TokenService(AuthenticationConfigProperties properties, AccessTokenRepository accessTokenRepository,
      RefreshTokenRepository refreshTokenRepository) {
    this.properties = properties;
    this.accessTokenRepository = accessTokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public AccessToken create(Map<String, String> parameters) {
    Date now = new Date();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setValue(UUID.randomUUID().toString());
    refreshToken.setExpiration(expiration(now, this.properties.getToken().getRefreshTtl()));

    AccessToken accessToken = AccessToken.from(parameters);
    accessToken.setTokenType(this.properties.getToken().getType());
    accessToken.setExpiration(expiration(now, this.properties.getToken().getTtl()));
    accessToken.setRefreshToken(refreshToken);
    accessToken.setValue(accessTokenValue());
    accessToken = this.accessTokenRepository.save(accessToken);
    return accessToken;
  }

  private String accessTokenValue() {
    return UUID.randomUUID().toString();
  }

  private Date expiration(Date from, int ttl) {
    Calendar expiration = Calendar.getInstance();
    expiration.setTime(from);
    expiration.add(Calendar.SECOND, ttl);
    return expiration.getTime();
  }
}
