package xyz.haofamily.susie.content;

import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

@Entity
public class TestEntity implements ContentHolder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  Long id;

  @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
      CascadeType.REFRESH }, fetch = FetchType.LAZY, mappedBy = "contentHolder")
  @Transient
  private Collection<ContentItem> contents = new ArrayList<>();

  @Override
  public Collection<ContentItem> getContents() {
    return this.contents;
  }

}