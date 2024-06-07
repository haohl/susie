package xyz.haofamily.susie.auth;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.common.util.StringUtils;
import xyz.haofamily.susie.auth.token.IllegalTokenRequestException;
import xyz.haofamily.susie.auth.token.TokenGranter;

/**
 * 
 * <p>
 * AuthenticationController
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-04
 */
@RestController
@RequestMapping("/api")
public class AuthenticationController {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

  private ApplicationContext ctx;

  public AuthenticationController(ApplicationContext ctx) {
    this.ctx = ctx;
  }

  @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createToken(@RequestParam Map<String, String> parameters) {
    logger.debug("Create token with parameters: {}", parameters);
    String grantType = parameters.get("grant_type");
    if (StringUtils.isBlank(grantType)) {
      grantType = "tokenGranter";
    }
    if ("implicit".equals(grantType)) {
      return ResponseEntity.badRequest().body("Implicit grant type not supported from token endpoint");
    }
    try {
      Optional<ResponseEntity<?>> token = Optional.ofNullable(ctx.getBean(grantType, TokenGranter.class))
          .map(granter -> granter.grant(parameters))
          .map(ResponseEntity::ok);
      return token.orElse(ResponseEntity.badRequest().body("Invalid grant type"));
    } catch (IllegalTokenRequestException e) {
      logger.error("Failed to grant token with parameters: {}", parameters);
      logger.error(e.getLocalizedMessage(), e);
      return ResponseEntity.badRequest().body(e.getLocalizedMessage());
    }
  }
}
