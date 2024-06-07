package xyz.haofamily.susie.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ AuthenticationConfiguration.class, AuthenticationConfigProperties.class })
public class TestConfiguration {

}
