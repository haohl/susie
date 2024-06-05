package xyz.haofamily.susie.auth.token;

/**
 * 
 * <p>
 * InvalidTokenRequestException
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-05
 */
public class IllegalTokenRequestException extends RuntimeException {

  public IllegalTokenRequestException(String message) {
    super(message);
  }
}
