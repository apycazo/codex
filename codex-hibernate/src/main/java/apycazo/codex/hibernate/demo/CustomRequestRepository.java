package apycazo.codex.hibernate.demo;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomRequestRepository {

  private final HibernateTemplate hibernate;

  public List<Player> getPlayersFromTeam(String teamName) {
    Session currentSession = getCurrentSession();
    // create a new criteria builder
    CriteriaBuilder cb = currentSession.getCriteriaBuilder();
    // we want to build a query returning Player entities
    CriteriaQuery<Player> criteria = cb.createQuery(Player.class);
    // from Player
    Root<Player> playerTable = criteria.from(Player.class);
    // join team
    Join<Player, Team> teamJoin = playerTable.join("team", JoinType.INNER);
    // where team.name == teamName
    CriteriaQuery<Player> query = criteria
      .select(playerTable)
      .where(cb.equal(teamJoin.get("name"), teamName));
    return currentSession.createQuery(query).getResultList();
  }

  private Session getCurrentSession() throws HibernateException {
    SessionFactory sessionFactory = hibernate.getSessionFactory();
    if (sessionFactory == null) {
      throw new HibernateException("No session factory");
    } else {
      return sessionFactory.getCurrentSession();
    }
  }
}
