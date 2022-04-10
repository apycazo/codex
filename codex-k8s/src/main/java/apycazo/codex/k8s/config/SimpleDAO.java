package apycazo.codex.k8s.config;

import apycazo.codex.k8s.data.DbError;
import apycazo.codex.k8s.data.EntryEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class SimpleDAO {

  private static final Logger log = LoggerFactory.getLogger(SimpleDAO.class);

  private final SessionFactory sessionFactory;

  public SimpleDAO(ServiceProperties props) {
    HibernateConfigurer hibernate = new HibernateConfigurer();
    Configuration configuration = hibernate.configuration(
      props.getDbhost(), props.getDbuser(), props.getDbpass()
    );
    configuration.addAnnotatedClass(EntryEntity.class);
    sessionFactory = hibernate.sessionFactory(configuration);
  }

  protected SessionFactory sessionFactory() {
    return sessionFactory;
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
        transaction.rollback();
        log.error("Operation failed, rolled back", e);
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
