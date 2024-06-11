package xyz.haofamily.susie.auth.token;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 
 * <p>
 * DefaultTokenGranter
 * </p>
 * 
 * @author Hualei Hao
 */
@Component("tokenGranter")
public class DefaultTokenGranter extends AbstractTokenGranter {

  private static final Logger logger = LoggerFactory.getLogger(DefaultTokenGranter.class);

  private AuthenticationManager authenticationManager;

  public DefaultTokenGranter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  protected Authentication validateParameters(Map<String, String> parameters) throws IllegalTokenRequestException {
    String username = parameters.get("username");
    String password = parameters.get("password");
    Authentication authentication = null;
    try {
      authentication = this.authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(username, password));
    } catch (UsernameNotFoundException e) {
      throw new IllegalTokenRequestException(String.format("User[%s] not found.", username));
    } catch (BadCredentialsException e) {
      throw new IllegalTokenRequestException("Username or password incorrect.");
    } catch (AccountStatusException e) {
      throw new IllegalTokenRequestException(e.getMessage());
    } catch (AuthenticationException e) {
      logger.error("Failed to authenticate user: {}", username);
      logger.error(e.getMessage(), e);
      throw new IllegalTokenRequestException(e.getMessage());
    }
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalTokenRequestException("Failed to authenticate user.");
    }
    return authentication;
  }

}
