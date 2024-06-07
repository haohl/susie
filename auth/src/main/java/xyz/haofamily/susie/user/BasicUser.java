package xyz.haofamily.susie.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

/**
 * 
 * <p>
 * AuthenticateItem
 * </p>
 * 
 * @author Hualei Hao
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class BasicUser {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String username;

  private String password;

  private boolean disabled = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

}
