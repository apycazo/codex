package apycazo.codex.hibernate.spring;

import apycazo.codex.hibernate.common.BasicUserEntity;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class BasicUserRepository extends HibernateDaoSupport {

  public BasicUserRepository(SessionFactory sessionFactory) {
    super.setSessionFactory(sessionFactory);
  }

  @Transactional
  public void persist(BasicUserEntity entity) {
    getHibernateTemplate().saveOrUpdate(entity);
  }

  @Transactional(readOnly = true)
  public List<BasicUserEntity> find() {
    return getHibernateTemplate().loadAll(BasicUserEntity.class);
  }
}
