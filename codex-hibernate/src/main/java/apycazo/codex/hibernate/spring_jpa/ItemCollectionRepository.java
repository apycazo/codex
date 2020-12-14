package apycazo.codex.hibernate.spring_jpa;

import org.springframework.data.repository.CrudRepository;

public interface ItemCollectionRepository extends CrudRepository<ItemCollection, Integer> {

  ItemCollection findByName(String name);
}
