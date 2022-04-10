package apycazo.codex.k8s.data;

import apycazo.codex.k8s.config.ServiceProperties;
import apycazo.codex.k8s.config.SimpleDAO;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Set;

public class MysqlDataService extends SimpleDAO implements DataService {

  public MysqlDataService(ServiceProperties props) {
    super(props);
  }

  @Override
  public String getByKey(String key) {
    String statement = "select EntryEntity from EntryEntity where key=:key";
    return query(session ->
      session.createQuery(
        statement, EntryEntity.class
      ).uniqueResult().getValue()
    );
  }

  @Override
  public void saveOrUpdate(String key, String value) {
    EntryEntity entity = new EntryEntity(key, value);
    transaction(session -> session.save(entity));
  }

  @Override
  public void deleteKey(String key) {
    transaction(session -> {
      String query = "delete from EntryEntity where key=:key";
      Query<?> sqlQuery = session.createQuery(query);
      sqlQuery.setParameter("key", key);
      session.createQuery(query).executeUpdate();
      return "";
    });
  }

  @Override
  public Set<String> getKeys() {
    return null;
  }

  @Override
  public List<String> getValues() {
    return null;
  }

  @Override
  public long getCount() {
    return transaction(session -> {
      String query = "select count(*) from EntryEntity";
      return (Long)session.createQuery(query).uniqueResult();
    });
  }

  @Override
  public void delete() {
    transaction(session -> {
      String query = "delete from EntryEntity";
      session.createQuery(query).executeUpdate();
      return "";
    });
  }

  @Override
  public String implementation() {
    return "mysql-data";
  }
}
