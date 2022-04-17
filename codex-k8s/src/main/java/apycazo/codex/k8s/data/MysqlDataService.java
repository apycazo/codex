package apycazo.codex.k8s.data;

import apycazo.codex.k8s.config.ServiceProperties;
import apycazo.codex.k8s.config.SimpleDAO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MysqlDataService extends SimpleDAO implements DataService {

  public MysqlDataService(ServiceProperties props) {
    super(props);
  }

  @Override
  public String getByKey(String key) {
    return query(session ->
      session
        .createQuery("from EntryEntity x where x.key=:key", EntryEntity.class)
        .setParameter("key", key)
        .uniqueResult()
        .getValue()
    );
  }

  @Override
  public void saveOrUpdate(String key, String value) {
    EntryEntity entity = new EntryEntity(key, value);
    transactionalOperation(session -> session.saveOrUpdate(entity));
  }

  @Override
  public void deleteKey(String key) {
    transactionalOperation(session ->
      session
        .createQuery("delete from EntryEntity x where x.key=:key")
        .setParameter("key", key)
        .executeUpdate()
    );
  }

  @Override
  public Set<String> getKeys() {
    return new HashSet<>(transaction(session ->
      session.createQuery("select x.key from EntryEntity x", String.class).list()
    ));
  }

  @Override
  public List<String> getValues() {
    return transaction(session ->
      session.createQuery("select x.value from EntryEntity x", String.class).list()
    );
  }

  @Override
  public long getCount() {
    return transaction(session -> {
      return (Long)session.createQuery("select count(*) from EntryEntity").uniqueResult();
    });
  }

  @Override
  public void delete() {
    transactionalOperation(session ->
      session.createQuery("delete from EntryEntity").executeUpdate()
    );
  }

  @Override
  public String implementation() {
    return "mysql-data";
  }
}
