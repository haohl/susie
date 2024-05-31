package xyz.haofamily.susie.content;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { ContentItemRepository.class })
public class ApplicationTest {

  @Test
  void basicTest() {
    assertTrue(true);
  }
}
