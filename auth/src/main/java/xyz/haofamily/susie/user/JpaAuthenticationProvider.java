package xyz.haofamily.susie.user;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class JpaAuthenticationProvider implements AuthenticationProvider {

  private BasicUserRepository repository;

  private PasswordEncoder passwordEncoder;

  public JpaAuthenticationProvider(BasicUserRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (!supports(authentication.getClass())) {
      throw new IllegalArgumentException("Unsupported authentication type");
    }
    String username = authentication.getName();
    if (username.isBlank()) {
      throw new BadCredentialsException("Username is empty");
    }
    BasicUser user = this.repository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format("User[%s] not found.", username)));
    if (authentication.getCredentials() == null) {
      throw new BadCredentialsException("Credentials is empty");
    }
    if (user.isDisabled()) {
      throw new DisabledException("User is disabled");
    }
    String presentedPassword = authentication.getCredentials().toString();
    if (!this.passwordEncoder.matches(presentedPassword, user.getPassword())) {
      throw new BadCredentialsException("Credentials do not match");
    }
    return UsernamePasswordAuthenticationToken.authenticated(username, presentedPassword, null);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }

}
