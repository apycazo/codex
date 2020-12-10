package apycazo.codex.hibernate.spring_jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

// see reference: https://docs.spring.io/spring-data/jpa/docs/2.4.1/reference/html/#reference
public interface ItemRepository extends CrudRepository<ItemEntity, Integer> {

  @Query("select i from ItemEntity i where i.value <= ?1")
  List<ItemEntity> findItemsWithValueLessThan(int value);

  @Query("select i from ItemEntity i where i.value = ?1")
  ItemEntity findItemWithValue(int value);

  int countByValueLessThan(int value);
}
