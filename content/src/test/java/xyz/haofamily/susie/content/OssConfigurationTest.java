package xyz.haofamily.susie.content;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import xyz.haofamily.susie.content.oss.OssConfigProperties;
import xyz.haofamily.susie.content.oss.OssContentStorageService;

@SpringBootTest
@TestPropertySource(properties = {
    "susie.content.oss.credentials.accessKey=accessKey",
    "susie.content.oss.credentials.secretKey=secretKey"
})
@EnableConfigurationProperties(value = OssConfigProperties.class)
@ContextConfiguration(classes = { ContentServiceConfiguration.class }, initializers = OssConfigurationTest.Initializer.class)
@Testcontainers
public class OssConfigurationTest {

  private static final Logger logger = LoggerFactory.getLogger(OssConfigurationTest.class);

  @Autowired
  private ContentStorageService storageService;

  @Container
  private static final GenericContainer<?> minio = new GenericContainer<>(DockerImageName.parse("minio/minio:latest"))
      .withEnv("MINIO_ACCESS_KEY", "accessKey")
      .withEnv("MINIO_SECRET_KEY", "secretKey")
      .withCommand("server /data")
      .withExposedPorts(9000);

  @Test
  void testContentStorageServiceBean() {
    assertNotNull(storageService);
    assertTrue(storageService instanceof OssContentStorageService);
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      String ossEndpoint = String.format("susie.content.oss.endpoint=http://localhost:%d",
          minio.getMappedPort(9000));
      logger.info("OSS Endpoint: {}", ossEndpoint);
      TestPropertyValues.of(ossEndpoint).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}