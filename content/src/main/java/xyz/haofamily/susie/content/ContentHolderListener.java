package xyz.haofamily.susie.content;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.DeleteContext;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.MergeContext;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.event.spi.PersistContext;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ContentHolderListener implements PersistEventListener, MergeEventListener, DeleteEventListener {

  private static final Logger logger = LoggerFactory.getLogger(ContentHolderListener.class);

  private ContentStorageService service;

  private ContentItemRepository repository;

  public ContentHolderListener(ContentStorageService service, ContentItemRepository repository) {
    this.service = service;
    this.repository = repository;
  }

  @Override
  public void onPersist(PersistEvent event) throws HibernateException {
    if (event.getObject() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getObject();
      afterInsertAndUpdate(contentHolder);
    }
  }

  @Override
  public void onPersist(PersistEvent event, PersistContext createdAlready) throws HibernateException {
    if (event.getObject() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getObject();
      afterInsertAndUpdate(contentHolder);
    }
  }

  @Override
  public void onMerge(MergeEvent event) throws HibernateException {
    if (event.getEntity() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getEntity();
      afterInsertAndUpdate(contentHolder);
    }
  }

  @Override
  public void onMerge(MergeEvent event, MergeContext copiedAlready) throws HibernateException {
    if (event.getEntity() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getEntity();
      afterInsertAndUpdate(contentHolder);
    }
  }

  @Override
  public void onDelete(DeleteEvent event) throws HibernateException {
    if (event.getObject() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getObject();
      afterDelete(contentHolder);
    }
  }

  @Override
  public void onDelete(DeleteEvent event, DeleteContext transientEntities) throws HibernateException {
    if (event.getObject() instanceof ContentHolder) {
      ContentHolder contentHolder = (ContentHolder) event.getObject();
      afterDelete(contentHolder);
    }
  }

  void afterInsertAndUpdate(ContentHolder contentHolder) {
    Collection<ContentItem> contents = contentHolder.getContents();
    if (contents.isEmpty()) {
      return;
    }
    long primaryContentCount = contents.stream()
        .filter(item -> item.getType() == ContentItemType.Primary).count();
    if (primaryContentCount > 1) {
      throw new IllegalStateException("Content holder can only have one primary content.");
    }
    List<Long> idCache = contents.stream()
        .peek(item -> item.setContentHolder(contentHolder))
        .map(ContentItem::getId)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    List<ContentItem> toDelete = this.repository.findByContentHolder(contentHolder).stream()
        .filter(item -> !idCache.contains(item.getId())).collect(Collectors.toList());
    if (!toDelete.isEmpty()) {
      for (ContentItem item : toDelete) {
        try {
          this.service.delete(item.getStorageLocation());
        } catch (IOException e) {
          logger.error(String.format("Failed delete '%s' from storage.", item.getStorageLocation()), e);
        }
      }
      this.repository.deleteAll(toDelete);
    }
    this.repository.saveAllAndFlush(contents);
  }

  void afterDelete(ContentHolder contentHolder) {
    if (!contentHolder.getContents().isEmpty()) {
      for (ContentItem item : contentHolder.getContents()) {
        try {
          this.service.delete(item.getStorageLocation());
        } catch (IOException e) {
          logger.error(String.format("Failed delete '%s' from storage.", item.getStorageLocation()), e);
        }
      }
      this.repository.deleteAll(contentHolder.getContents());
    }
  }
}