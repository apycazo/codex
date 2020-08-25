package apycazo.codex.hibernate.common;

import lombok.Data;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

  @Transient
  public static List<BasicUserEntity> examples() {
    return Arrays
      .stream(new String[]{"frodo", "sam", "merry", "pippin"})
      .map(name -> {
        BasicUserEntity entity = new BasicUserEntity();
        entity.setUsername(name);
        entity.setActive(name.length() != 5);
        return entity;
      }).collect(Collectors.toList());
  }
}
