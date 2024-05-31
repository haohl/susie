package xyz.haofamily.susie.content;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.lang.NonNull;

/**
 * 
 * <p>
 * ContentStorageService
 * </p>
 * 
 * Low level api to store and retrieve content.
 * 
 * @author Hualei Hao
 */
public interface ContentStorageService {

  /**
   * Store the content and return a reference as key.
   * @param in The content input stream.
   * @return The key to reference the content.
   * @throws IOException
   */
  String store(@NonNull InputStream in) throws IOException;

  /**
   * Delete the content by key.
   * @param key The key to reference the content.
   * @throws IOException
   */
  void delete(@NonNull String key) throws IOException;

  /**
   * Fetch the content by key.
   * @param key The key to reference the content.
   * @return The content input stream.
   * @throws IOException
   */
  InputStream fetch(@NonNull String key) throws IOException;

  /**
   * Get the length of the content by key.
   * @param key The key to reference the content.
   * @return The length of the content.
   * @throws IOException
   */
  long length(@NonNull String key) throws IOException;
}
