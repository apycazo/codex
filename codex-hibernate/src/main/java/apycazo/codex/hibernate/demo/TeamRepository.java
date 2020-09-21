package apycazo.codex.hibernate.demo;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public class TeamRepository extends GenericRepository<Team, Integer> {

  public Team findByName(String name) {
    DetachedCriteria criteria = DetachedCriteria
      .forClass(Team.class)
      .add(Property.forName("name").eq(name));
    return (Team) hibernate.findByCriteria(criteria).stream().findFirst().orElse(null);
  }
}
