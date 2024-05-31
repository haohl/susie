package xyz.haofamily.susie.content;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import xyz.haofamily.susie.content.local.LocalContentStorageService;

@SpringBootTest
@ContextConfiguration(classes = { ContentServiceConfiguration.class })
public class DefaultContentStorageServiceTest {

  @Autowired
  private ContentStorageService storageService;

  @Test
  void testContentStorageServiceBean() {
    assertNotNull(storageService);
    assertTrue(storageService instanceof LocalContentStorageService);
  }
}
