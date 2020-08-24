package apycazo.codex.hibernate.common;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "basic_user")
public class BasicUserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "username")
  private String username;

  @Column(name = "active")
  private boolean active;
}
