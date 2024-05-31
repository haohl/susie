package xyz.haofamily.susie.content;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.lang.NonNull;

/**
 * 
 * <p>
 * 
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-05-28
 */
public interface ContentStorageService {

  String store(@NonNull InputStream in) throws IOException;

  void delete(@NonNull String key) throws IOException;

  InputStream fetch(@NonNull String key) throws IOException;

  long length(@NonNull String key) throws IOException;
}
