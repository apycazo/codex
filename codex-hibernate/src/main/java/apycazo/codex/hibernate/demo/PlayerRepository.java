package apycazo.codex.hibernate.demo;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class PlayerRepository extends GenericRepository<Player, Integer> {

  public Player findByName(String name) {
    DetachedCriteria criteria = DetachedCriteria
      .forClass(Player.class)
      .add(Property.forName("name").eq(name));
    return (Player) hibernate.findByCriteria(criteria).stream().findFirst().orElse(null);
  }
}
