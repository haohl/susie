package xyz.haofamily.susie.content;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

@Component
public class ContentHolderListenerConfigure {

  private EntityManagerFactory emf;

  private ContentHolderListener contentHolderListener;

  public ContentHolderListenerConfigure(EntityManagerFactory emf, ContentHolderListener contentHolderListener) {
    this.emf = emf;
    this.contentHolderListener = contentHolderListener;
  }

  @PostConstruct
  protected void init() {
    SessionFactoryImpl sessionFactory = emf.unwrap(SessionFactoryImpl.class);
    EventListenerRegistry registry = sessionFactory.getEventEngine().getListenerRegistry();
    registry.appendListeners(EventType.PERSIST, contentHolderListener);
    registry.appendListeners(EventType.MERGE, contentHolderListener);
    registry.prependListeners(EventType.DELETE, contentHolderListener);
  }

}
