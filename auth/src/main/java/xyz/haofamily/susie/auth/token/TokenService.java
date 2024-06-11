package xyz.haofamily.susie.auth.token;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import xyz.haofamily.susie.auth.AuthenticationConfigProperties;
import xyz.haofamily.susie.auth.AuthenticationType;

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
  private Key jwtKey;

  public TokenService(AuthenticationConfigProperties properties, AccessTokenRepository accessTokenRepository,
      RefreshTokenRepository refreshTokenRepository) {
    this.properties = properties;
    this.accessTokenRepository = accessTokenRepository;
    this.refreshTokenRepository = refreshTokenRepository;
  }

  public AccessToken create(Authentication authentication) {
    Date now = new Date();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setValue(uuidTokenValue());
    refreshToken.setExpiration(expiration(now, this.properties.getToken().getRefreshTtl()));

    AccessToken accessToken = AccessToken.from(authentication);
    accessToken.setTokenType(this.properties.getToken().getType());
    accessToken.setExpiration(expiration(now, this.properties.getToken().getTtl()));
    accessToken.setRefreshToken(refreshToken);
    switch (this.properties.getType()) {
      case AuthenticationType.JWT:
        accessToken.setValue(jwtTokenValue(authentication, now, accessToken.getExpiration()));
        break;
      default:
        accessToken.setValue(uuidTokenValue());
    }
    accessToken = this.accessTokenRepository.save(accessToken);
    return accessToken;
  }

  @Autowired(required = false)
  public void setJwtKey(Key jwtKey) {
    this.jwtKey = jwtKey;
  }

  private String jwtTokenValue(Authentication authentication, Date now, Date expiration) {
    JwtBuilder builder = Jwts.builder().header().type("JWT").and()
        .issuer(this.properties.getToken().getJwt().getIssuer())
        .subject(authentication.getName())
        .issuedAt(now)
        .expiration(expiration);
    switch (this.properties.getToken().getJwt().getAlg()) {
      case AuthenticationConfigProperties.JwtSignAlg.RSA:
        builder.signWith(jwtKey);
        break;
      default:
        builder.signWith(Keys.hmacShaKeyFor(this.properties.getToken().getJwt().getSecret().getBytes()));
    }
    return builder.compact();
  }

  private String uuidTokenValue() {
    return UUID.randomUUID().toString();
  }

  private Date expiration(Date from, int ttl) {
    Calendar expiration = Calendar.getInstance();
    expiration.setTime(from);
    expiration.add(Calendar.SECOND, ttl);
    return expiration.getTime();
  }
}
