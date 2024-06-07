package xyz.haofamily.susie.auth.token;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

/**
 * 
 * <p>
 * AccessToken
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-04
 */
@Entity
public class AccessToken {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonIgnore
  private Long id;

  @JsonProperty("access_token")
  @Column(name = "token")
  private String value;

  @JsonProperty("token_type")
  private String tokenType;

  @JsonIgnore
  private Date expiration;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "refresh_token_id", referencedColumnName = "id")
  @JsonIgnore
  private RefreshToken refreshToken;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private Set<String> scope;

  @JsonIgnore
  private String username;

  @JsonIgnore
  private String clientId;

  public static AccessToken from(Map<String, String> parameters) {
    AccessToken accessToken = new AccessToken();
    accessToken.username = parameters.get("username");
    if (parameters.containsKey("clientId")) {
      accessToken.clientId = parameters.get("clientId");
    }
    if (parameters.containsKey("scope")) {
      String[] scopes = parameters.get("scope").split(" ");
      accessToken.scope = Arrays.stream(scopes).peek(String::trim).collect(Collectors.toSet());
    }
    return accessToken;
  }

  @JsonProperty("expires_in")
  public long getExpiresIn() {
    return (expiration.getTime() - System.currentTimeMillis()) / 1000 + 1;
  }

  @JsonProperty("refresh_token")
  public String getRefreshTokenValue() {
    return refreshToken.getValue();
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public Date getExpiration() {
    return expiration;
  }

  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  public RefreshToken getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(RefreshToken refreshToken) {
    this.refreshToken = refreshToken;
  }

  public Set<String> getScope() {
    return scope;
  }

  public void setScope(Set<String> scope) {
    this.scope = scope;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

}
