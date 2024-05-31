package xyz.haofamily.susie.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ContentHolderRepository<T extends ContentHolder> extends JpaRepository<T, Long> {

}
