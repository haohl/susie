package xyz.haofamily.susie.content;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { ContentItemRepository.class, TestEntityRepository.class,
    AnotherTestEntityRepository.class, ContentServiceConfiguration.class, ContentHolderListenerConfigure.class,
    ContentHolderListener.class })
public class MultipleContentHolderTest {

  @Autowired
  private TestEntityRepository repository1;
  @Autowired
  private AnotherTestEntityRepository repository2;

  @Autowired
  private ContentItemRepository contentItemRepository;

  @Test
  void testQuery() {
    TestEntity entity1 = new TestEntity();
    ContentItem primary1 = new ContentItem();
    primary1.setType(ContentItemType.Primary);
    entity1.setContents(Arrays.asList(primary1));
    this.repository1.save(entity1);
    AnotherTestEntity entity2 = new AnotherTestEntity();
    ContentItem primary2 = new ContentItem();
    primary2.setType(ContentItemType.Primary);
    entity2.setContents(Arrays.asList(primary2));
    this.repository2.save(entity2);

    TestEntity found1 = this.repository1.findById(entity1.id).get();
    assertNotNull(found1);
    assertEquals(1, found1.getContents().size());
    assertEquals(primary1.getId(), found1.getPrimary().getId());

    AnotherTestEntity found2 = this.repository2.findById(entity2.id).get();
    assertNotNull(found2);
    assertEquals(1, found2.getContents().size());
    assertEquals(primary2.getId(), found2.getPrimary().getId());

    List<ContentItem> items = this.contentItemRepository.findAll();
    assertThat(items).hasSize(2)
        .extracting(contentItem -> contentItem.getContentHolder())
        .satisfiesExactlyInAnyOrder(
            holder -> assertThat(holder).isInstanceOf(TestEntity.class),
            holder -> assertThat(holder).isInstanceOf(AnotherTestEntity.class));
  }
}
