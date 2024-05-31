package xyz.haofamily.susie.content;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * ContentHolder is an interface that represents an entity that can hold one or
 * more content items,
 * the entity that implements this interface can have one primary content item
 * and multiple appendix,
 * the method getContents must annotate with @OneToMany, for example:
 * 
 * <pre>
 * <code>
 * class MyEntity implements ContentHolder {
 * {@literal @}Id
 * {@literal @}GeneratedValue(strategy = GenerationType.AUTO)
 * private Long id;
 * 
 * {@literal @}OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
 *    CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "contentHolder")
 * {@literal @}Transient
 * private Collection<ContentItem> contents = new ArrayList<>();
 * 
 * {@literal @}Override
 * public Collection<ContentItem> getContents() {
 *   return this.contents;
 * }
 * 
 * ...other fields and methods...
 * }
 * </code>
 * </pre>
 * 
 * 
 * @author Hualei Hao
 * @date 2024-05-20
 */
public interface ContentHolder {

  default ContentItem getPrimary() {
    return getContents().stream()
        .filter(item -> item.getType() == ContentItemType.Primary).findFirst().orElse(null);
  }

  default void setPrimary(ContentItem primaryContent) {
    if (getPrimary() != null) {
      throw new IllegalStateException("Primay content already exists.");
    }
    getContents().add(primaryContent);
  }

  Collection<ContentItem> getContents();

  default void setContents(Iterable<ContentItem> contents) {
    long primaryContentSize = Optional.ofNullable(contents).map(Iterable::spliterator)
        .map(i -> StreamSupport.stream(i, false))
        .get().filter(item -> item.getType() == ContentItemType.Primary).count();
    if (primaryContentSize > 1) {
      throw new IllegalArgumentException("Only one primary content is allowed.");
    }
    this.getContents().clear();
    Optional.ofNullable(contents).get().forEach(item -> {
      item.setContentHolder(this);
      getContents().add(item);
    });
  }

  default void addContents(Iterable<ContentItem> contents) {
    long primaryContentSize = Optional.ofNullable(contents).map(Iterable::spliterator)
        .map(i -> StreamSupport.stream(i, false))
        .get().filter(item -> item.getType() == ContentItemType.Primary).count();
    if (primaryContentSize > 1) {
      throw new IllegalArgumentException("Only one primary content is allowed.");
    }
    primaryContentSize += this.getContents().stream().filter(item -> item.getType() == ContentItemType.Primary)
        .count();
    if (primaryContentSize > 1) {
      throw new IllegalArgumentException("Only one primary content is allowed.");
    }
    Optional.ofNullable(contents).get().forEach(item -> {
      item.setContentHolder(this);
      getContents().add(item);
    });
  }
}
