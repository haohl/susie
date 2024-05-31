package xyz.haofamily.susie.content.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "susie.content.oss")
public class OssConfigProperties {

  private String endpoint;

  private OssCredentials credentials;

  private String bucketName = "susie-contents";

  public String getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public OssCredentials getCredentials() {
    return credentials;
  }

  public void setCredentials(OssCredentials credentials) {
    this.credentials = credentials;
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

}
