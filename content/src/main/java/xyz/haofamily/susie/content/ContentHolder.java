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
 * private Collection&lt;ContentItem&gt; contents = new ArrayList&lt;&gt;();
 * 
 * {@literal @}Override
 * public Collection&lt;ContentItem&gt; getContents() {
 *   return this.contents;
 * }
 * 
 * ...other fields and methods...
 * }
 * </code>
 * </pre>
 * 
 * Please notice that the method getContents must return the same collection,
 * otherwise you need implement the addContents and setContents methods
 * yourself.
 * 
 * @author Hualei Hao
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
