package xyz.haofamily.susie.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

@Configuration
public class AuthenticationConfiguration {

  private ApplicationContext ctx;

  private AuthenticationConfigProperties properties;

  public AuthenticationConfiguration(ApplicationContext ctx, AuthenticationConfigProperties properties) {
    this.ctx = ctx;
    this.properties = properties;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    List<AuthenticationProvider> providers = this.properties.getProviders().stream()
        .map(type -> ctx.getBean(type.name(), AuthenticationProvider.class)).collect(Collectors.toList());
    return providers.isEmpty()
        ? new ProviderManager(ctx.getBean(AuthenticationProviderType.JDBC.name(), AuthenticationProvider.class))
        : new ProviderManager(providers);
  }
}
