package apycazo.codex.k8s.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "entry")
public class EntryEntity {

  @Id
  @Column(name = "key", unique = true)
  private String key;

  @Column(name = "value")
  private String value;
}
