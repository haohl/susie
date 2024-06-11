package xyz.haofamily.susie.auth;

import java.security.Key;
import java.security.KeyStore;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.security.Keys;
import xyz.haofamily.susie.auth.AuthenticationConfigProperties.JwtTokenConfig;
import xyz.haofamily.susie.user.BasicUserRepository;
import xyz.haofamily.susie.user.JpaAuthenticationProvider;

@Configuration
@ComponentScan(basePackages = { "xyz.haofamily.susie.auth", "xyz.haofamily.susie.user" })
@EntityScan(basePackages = { "xyz.haofamily.susie.auth", "xyz.haofamily.susie.user" })
@EnableJpaRepositories(basePackages = { "xyz.haofamily.susie.auth", "xyz.haofamily.susie.user" })
public class AuthenticationConfiguration {

  private ApplicationContext ctx;

  private AuthenticationConfigProperties properties;

  public AuthenticationConfiguration(ApplicationContext ctx, AuthenticationConfigProperties properties) {
    this.ctx = ctx;
    this.properties = properties;
  }

  @Bean
  @ConditionalOnProperty(prefix = "susie.security.auth", name = "type", havingValue = "JWT")
  public Key jwtKey() {
    JwtTokenConfig config = properties.getToken().getJwt();
    switch (config.getAlg()) {
      case RSA:
        return rsaKey(config);
      default:
        return hmacSHAKey(config);
    }
  }

  private Key hmacSHAKey(JwtTokenConfig config) {
    return Optional.ofNullable(config.getSecret()).map(String::getBytes).map(Keys::hmacShaKeyFor)
        .orElseThrow(() -> new IllegalStateException("Secret is required for HMAC algorithm"));
  }

  private Key rsaKey(JwtTokenConfig config) {
    if (config.getKeyStore() == null) {
      throw new IllegalStateException("RSA key store is required for RSA algorithm");
    }
    try {
      KeyStore ks = KeyStore.getInstance("JKS");
      ks.load(config.getKeyStore().getInputStream(), config.getKeyStorePassword().toCharArray());
      return ks.getKey(config.getKeyAlias(), config.getKeyPassword().toCharArray());
    } catch (Exception e) {
      throw new IllegalStateException("Can't load the key from the key store.", e);
    }
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    List<AuthenticationProvider> providers = this.properties.getProviders().stream()
        .map(type -> ctx.getBean(type.name(), AuthenticationProvider.class)).collect(Collectors.toList());
    return providers.isEmpty()
        ? new ProviderManager(ctx.getBean(AuthenticationProviderType.JDBC.name(), AuthenticationProvider.class))
        : new ProviderManager(providers);
  }

  @Bean
  @ConditionalOnMissingBean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean(name = "JDBC")
  @ConditionalOnMissingBean(name = "JDBC")
  public JpaAuthenticationProvider jpaAuthenticationProvider(BasicUserRepository repository,
      PasswordEncoder passwordEncoder) {
    return new JpaAuthenticationProvider(repository, passwordEncoder);
  }
}
