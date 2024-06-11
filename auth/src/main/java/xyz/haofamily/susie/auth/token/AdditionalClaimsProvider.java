package xyz.haofamily.susie.auth.token;

import java.util.Map;

import org.springframework.security.core.Authentication;

/**
 * 
 * <p>
 * AdditionalClaimsProvider
 * </p>
 * 
 * @author Hualei Hao
 */
public interface AdditionalClaimsProvider {
  Map<String, ?> getClaims(Authentication authentication);
}
