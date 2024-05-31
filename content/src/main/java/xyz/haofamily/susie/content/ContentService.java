package xyz.haofamily.susie.content;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.support.Repositories;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * 
 * <p>
 * ContentService
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-05-22
 */
@Service
public class ContentService {

  private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

  private ContentStorageService contentStorageService;

  private Repositories repositories;

  public ContentService(ApplicationContext appCtx, ContentStorageService contentStorageService) {
    this.repositories = new Repositories(appCtx);
    this.contentStorageService = contentStorageService;
  }

  public InputStream getContentStream(@NonNull ContentItem item) {
    try {
      return this.contentStorageService.fetch(item.getStorageLocation());
    } catch (IOException e) {
      throw new RuntimeException("Failed to retrieve data from storage.", e);
    }
  }

  public ContentItem storeContentStream(ContentItemType type, @NonNull Resource resource) {
    ContentItem item = new ContentItem();
    item.setType(type);
    item.setFilename(resource.getFilename());
    try {
      String mimeType = Files.probeContentType(Paths.get(resource.getFilename()));
      item.setMimeType(mimeType);
    } catch (IOException e) {
      logger.error("Failed to determine the MIME type of the file: {}, {}", resource.getFilename(),
          e.getLocalizedMessage());
      item.setMimeType("application/octet-stream");
    }

    String storageLocation;
    try {
      storageLocation = this.contentStorageService.store(resource.getInputStream());
      item.setFileSize(this.contentStorageService.length(storageLocation));
    } catch (IOException e) {
      throw new RuntimeException("Failed to store data to storage.", e);
    }
    item.setStorageLocation(storageLocation);
    return item;
  }

  public void setContent(@NonNull ContentHolder contentHolder, @NonNull Resource resource) {
    Collection<ContentItem> items = contentHolder.getContents();
    if (items.isEmpty() || items.stream().noneMatch(item -> item.getType() == ContentItemType.Primary)) {
      setContent(contentHolder, ContentItemType.Primary, resource);
    } else {
      setContent(contentHolder, ContentItemType.Appendix, resource);
    }
  }

  public void setContent(@NonNull ContentHolder contentHolder, ContentItemType type, @NonNull Resource resource) {
    ContentItem item = new ContentItem();
    item.setType(type);
    item.setFilename(resource.getFilename());
    try {
      String mimeType = Files.probeContentType(Paths.get(resource.getFilename()));
      item.setMimeType(mimeType);
    } catch (IOException e) {
      logger.error("Failed to determine the MIME type of the file: {}, {}", resource.getFilename(),
          e.getLocalizedMessage());
      item.setMimeType("application/octet-stream");
    }

    String storageLocation;
    try {
      storageLocation = this.contentStorageService.store(resource.getInputStream());
      item.setFileSize(this.contentStorageService.length(storageLocation));
    } catch (IOException e) {
      throw new RuntimeException("Failed to store data to storage.", e);
    }
    item.setStorageLocation(storageLocation);
    setContent(contentHolder, item);
  }

  private void setContent(ContentHolder contentHolder, ContentItem item) {
    @SuppressWarnings("unchecked")
    ContentHolderRepository<ContentHolder> holderRepository = (ContentHolderRepository<ContentHolder>) this.repositories
        .getRepositoryFor(contentHolder.getClass())
        .orElseThrow(() -> new RuntimeException("No repository found for " + contentHolder.getClass().getName()));
    contentHolder.addContents(Arrays.asList(item));
    holderRepository.save(contentHolder);
  }
}
