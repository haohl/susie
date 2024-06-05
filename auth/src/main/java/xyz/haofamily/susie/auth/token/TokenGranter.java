package xyz.haofamily.susie.auth.token;

import java.util.Map;

/**
 * 
 * <p>
 * TokenGranter
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-04
 */
public interface TokenGranter {

  AccessToken grant(Map<String, String> parameters) throws IllegalTokenRequestException;

}
