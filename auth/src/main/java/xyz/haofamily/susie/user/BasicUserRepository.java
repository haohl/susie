package xyz.haofamily.susie.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * <p>
 * 
 * </p>
 * 
 * @author Hualei Hao
 * @date 2024-06-06
 */
public interface BasicUserRepository extends JpaRepository<BasicUser, Long> {
  Optional<BasicUser> findByUsername(String username);
}
