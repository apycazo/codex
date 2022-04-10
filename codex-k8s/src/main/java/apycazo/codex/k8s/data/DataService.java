package apycazo.codex.k8s.data;

import java.util.List;
import java.util.Set;

public interface DataService {

  String getByKey(String key);

  void saveOrUpdate(String key, String value);

  void deleteKey(String key);

  Set<String> getKeys();

  List<String> getValues();

  long getCount();

  void delete();

  default String implementation() {
    return "default";
  }
}
