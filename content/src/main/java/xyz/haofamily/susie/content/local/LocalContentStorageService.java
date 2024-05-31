package xyz.haofamily.susie.content.local;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

import xyz.haofamily.susie.content.ContentStorageService;
import xyz.haofamily.susie.content.ContentStorageUtil;

public class LocalContentStorageService implements ContentStorageService {

  @Value("${susie.content.local.endpoint}")
  private String endpoint = "./data/contents/";

  private Path root;

  public LocalContentStorageService() throws IOException {
    this.root = Paths.get(this.endpoint.endsWith("/") ? this.endpoint : this.endpoint + "/");
    if (!Files.exists(this.root)) {
      Files.createDirectories(this.root);
    }
  }

  @Override
  public String store(@NonNull InputStream in) throws IOException {
    String key = ContentStorageUtil.generateKey();
    Path location = this.root.resolve(key);
    Path parentDir = location.getParent();
    if (!Files.exists(parentDir)) {
      Files.createDirectories(parentDir);
    }
    Files.copy(in, location);
    return key;
  }

  @Override
  public void delete(@NonNull String key) throws IOException {
    Path location = this.root.resolve(key);
    if (Files.exists(location)) {
      Files.delete(location);
    }
  }

  @Override
  public InputStream fetch(@NonNull String key) throws IOException {
    Path location = this.root.resolve(key);
    if (Files.exists(location)) {
      return Files.newInputStream(location);
    }
    return null;
  }

  @Override
  public long length(@NonNull String key) throws IOException {
    Path location = this.root.resolve(key);
    if (Files.exists(location)) {
      return Files.size(location);
    }
    return -1;
  }
}
