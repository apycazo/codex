package apycazo.codex.k8s.config;

import apycazo.codex.k8s.others.DbError;
import apycazo.codex.k8s.data.EntryEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleDAO {

  private static final Logger log = LoggerFactory.getLogger(SimpleDAO.class);

  private final SessionFactory sessionFactory;

  public SimpleDAO(ServiceProperties props) {
    HibernateConfigurer hibernate = new HibernateConfigurer();
    Configuration configuration = hibernate.configuration(
      props.getDbhost(), props.getDbuser(), props.getDbpass(), props.getHibernateProperties()
    );
    configuration.addAnnotatedClass(EntryEntity.class);
    sessionFactory = hibernate.sessionFactory(configuration);
  }

  public void transactionalOperation(Consumer<Session> op) {
    transaction(session -> {
      op.accept(session);
      return null;
    });
  }

  public <T> T transaction(Function<Session, T> op) {
    Transaction transaction = null;
    try (Session session = sessionFactory.openSession()) {
      transaction = session.beginTransaction();
      T result = op.apply(session);
      transaction.commit();
      return result;
    } catch (Exception e) {
      if (transaction != null) {
        log.error("Operation failed, rolling back", e);
        transaction.rollback();
      } else {
        log.error("Operation failed", e);
      }
      throw new DbError("Transaction failed", e);
    }
  }

  public <T> T query(Function<Session, T> op) {
    try (Session session = sessionFactory.openSession()) {
      return op.apply(session);
    } catch (Exception e) {
      throw new DbError("Query failed", e);
    }
  }
}
