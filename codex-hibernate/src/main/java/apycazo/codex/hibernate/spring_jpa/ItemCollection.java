package apycazo.codex.hibernate.spring_jpa;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ItemCollection {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String name;
  // this relationship is unidirectional
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<ItemEntity> items;
}
