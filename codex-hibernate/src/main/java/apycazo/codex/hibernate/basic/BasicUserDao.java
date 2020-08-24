package apycazo.codex.hibernate.basic;

import apycazo.codex.hibernate.common.BasicUserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BasicUserDao {

  private final SessionFactory sessionFactory;

  public Integer create(BasicUserEntity entity) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      Integer id = (Integer)session.save(entity);
      transaction.commit();
      return id;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      log.error("Failed to save {}", entity, e);
      return null;
    }
  }

  public List<BasicUserEntity> list() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("from BasicUserEntity", BasicUserEntity.class).list();
    } catch (Exception e) {
      log.error("Failed to list", e);
      return Collections.emptyList();
    }
  }
}
