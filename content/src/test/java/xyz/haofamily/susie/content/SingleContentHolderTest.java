package xyz.haofamily.susie.content;

// Add this import statement
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = { ContentItemRepository.class, TestEntityRepository.class,
    ContentServiceConfiguration.class, ContentHolderListenerConfigure.class, ContentHolderListener.class })
public class SingleContentHolderTest {

  @Autowired
  private TestEntityRepository repository;

  @Autowired
  private ContentItemRepository contentItemRepository;

  @Test
  void testCreateWithSingleContent() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    entity.setContents(Arrays.asList(primary));
    this.repository.save(entity);
    assertNotNull(entity.id);
    assertNotNull(primary.getId());
    assertTrue(this.repository.existsById(entity.id));
    assertTrue(this.contentItemRepository.existsById(primary.getId()));
  }

  @Test
  void testCreateWithMultipleContents() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entity.setContents(Arrays.asList(primary, appendix));
    this.repository.save(entity);
    assertNotNull(entity.id);
    assertNotNull(primary.getId());
    assertNotNull(appendix.getId());
    assertTrue(this.repository.existsById(entity.id));
    assertTrue(this.contentItemRepository.existsById(primary.getId()));
    assertTrue(this.contentItemRepository.existsById(appendix.getId()));
  }

  @Test
  void testQuery() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entity.setContents(Arrays.asList(primary, appendix));
    this.repository.save(entity);

    TestEntity found = this.repository.findById(entity.id).get();
    assertNotNull(found);
    assertEquals(2, found.getContents().size());
    assertEquals(primary.getId(), found.getPrimary().getId());
  }

  @Test
  void testUpdateWithAddContent() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    entity.setContents(Arrays.asList(primary));
    this.repository.save(entity);

    TestEntity entityExist = this.repository.findById(entity.id).get();
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entityExist.getContents().add(appendix);
    this.repository.save(entityExist);

    TestEntity found = this.repository.findById(entity.id).get();
    assertNotNull(found);
    assertEquals(2, found.getContents().size());
    assertEquals(primary.getId(), found.getPrimary().getId());
  }

  @Test
  void testUpdateWithRemoveContent() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entity.setContents(Arrays.asList(primary, appendix));
    this.repository.save(entity);

    TestEntity entityExist = this.repository.findById(entity.id).get();
    entityExist.getContents().removeIf(item -> item.getType() != ContentItemType.Primary);
    this.repository.save(entityExist);

    assertFalse(this.contentItemRepository.existsById(appendix.getId()));

    TestEntity found = this.repository.findById(entity.id).get();
    assertNotNull(found);
    assertEquals(1, found.getContents().size());
    assertEquals(primary.getId(), found.getPrimary().getId());
  }

  @Test
  void testUpdateWithReplaceContent() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entity.setContents(Arrays.asList(primary, appendix));
    this.repository.save(entity);

    TestEntity entityExist = this.repository.findById(entity.id).get();
    ContentItem anotherAppendix = new ContentItem();
    anotherAppendix.setType(ContentItemType.Appendix);
    entityExist.setContents(Arrays.asList(entityExist.getPrimary(), anotherAppendix));
    this.repository.save(entityExist);

    assertNotEquals(appendix.getId(), anotherAppendix.getId());
    assertFalse(this.contentItemRepository.existsById(appendix.getId()));

    TestEntity found = this.repository.findById(entity.id).get();
    assertNotNull(found);
    assertThat(found.getContents())
        .hasSize(2)
        .extracting(ContentItem::getId)
        .containsExactlyInAnyOrder(primary.getId(), anotherAppendix.getId());
  }

  @Test
  void testDelete() {
    TestEntity entity = new TestEntity();
    ContentItem primary = new ContentItem();
    primary.setType(ContentItemType.Primary);
    ContentItem appendix = new ContentItem();
    appendix.setType(ContentItemType.Appendix);
    entity.setContents(Arrays.asList(primary, appendix));
    this.repository.save(entity);

    this.repository.deleteById(entity.id);
    assertFalse(this.repository.existsById(entity.id));
    assertFalse(this.contentItemRepository.existsById(primary.getId()));
    assertFalse(this.contentItemRepository.existsById(appendix.getId()));
  }
}
