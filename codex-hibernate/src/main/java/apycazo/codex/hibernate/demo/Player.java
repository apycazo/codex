package apycazo.codex.hibernate.demo;

import lombok.Data;

import javax.persistence.*;

/**
 * @author Andres Picazo
 */
@Entity
@Data
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String name;

  @ManyToOne
  @JoinColumn(name = "team_id")
  private Team team;

  public static Player instance (String name, Team team) {
    Player player = new Player();
    player.setName(name);
    player.setTeam(team);
    return player;
  }

  // Custom toString avoid recursive error while trying to print the team
  @Override
  public String toString () {
    return String.format("%d:%s", id, name);
  }
}

