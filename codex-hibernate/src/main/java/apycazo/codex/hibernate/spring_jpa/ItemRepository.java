package apycazo.codex.hibernate.spring_jpa;

import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<ItemEntity, Integer> {
}
