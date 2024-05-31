package xyz.haofamily.susie.content;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface ContentItemRepository extends JpaRepository<ContentItem, Long> {

  List<ContentItem> findByContentHolder(ContentHolder contentHolder);
}
