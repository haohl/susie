package xyz.haofamily.susie.content;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import xyz.haofamily.susie.content.local.LocalContentStorageService;
import xyz.haofamily.susie.content.oss.OssConfigProperties;
import xyz.haofamily.susie.content.oss.OssContentStorageService;

@Configuration
public class ContentServiceConfiguration {

  @Bean("contentStorageService")
  @ConditionalOnProperty(name = { "endpoint", "credentials.accessKey",
      "credentials.secretKey" }, prefix = "susie.content.oss")
  public ContentStorageService ossContentStorageService(OssConfigProperties ossConfigProperties) {
    return new OssContentStorageService(ossConfigProperties);
  }

  @Bean("contentStorageService")
  @ConditionalOnMissingBean(ContentStorageService.class)
  public ContentStorageService localContentStorageService() {
    try {
      return new LocalContentStorageService();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
