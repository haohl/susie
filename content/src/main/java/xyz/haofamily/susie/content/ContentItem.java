package xyz.haofamily.susie.content;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyKeyJavaClass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

/**
 * 
 * 
 * @author Hualei Hao
 * @date 2024-05-20
 */
@Entity
public class ContentItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, length = 20)
  @Enumerated(EnumType.STRING)
  private ContentItemType type;

  @Any
  @AnyDiscriminator
  @Column(name = "content_holder_class_name", nullable = false)
  @AnyKeyJavaClass(Long.class)
  @JoinColumn(name = "content_holder_id", nullable = false)
  private ContentHolder contentHolder;

  private String filename;

  private long fileSize;
  
  private String mimeType;

  private String storageLocation;

  public Long getId() {
    return id;
  }

  public ContentItemType getType() {
    return type;
  }

  public void setType(ContentItemType type) {
    this.type = type;
  }

  public ContentHolder getContentHolder() {
    return contentHolder;
  }

  public void setContentHolder(ContentHolder contentHolder) {
    this.contentHolder = contentHolder;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public long getFileSize() {
    return fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getStorageLocation() {
    return storageLocation;
  }

  public void setStorageLocation(String storageLocation) {
    this.storageLocation = storageLocation;
  }

}
