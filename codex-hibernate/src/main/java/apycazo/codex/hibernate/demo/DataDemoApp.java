package apycazo.codex.hibernate.demo;

import apycazo.codex.hibernate.spring.SpringPersistenceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class DataDemoApp {

  public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringPersistenceConfig.class);
    ctx.register(DemoPersistenceConfig.class);
    ctx.refresh();
    log.info("DEMO: Simple");
    simpleDemo(ctx);
    log.info("DEMO: Team");
    teamDemo(ctx);
  }

  public static void simpleDemo(AnnotationConfigApplicationContext ctx) {
    PlayerRepository playerRepository = ctx.getBean(PlayerRepository.class);
    // --- simple create and remove a player without team
    Player player = Player.instance("test", null);
    playerRepository.saveOrUpdate(player);
    List<Player> savedPlayers = playerRepository.find();
    log.info("Player count: {}", savedPlayers.size());
    savedPlayers.forEach(p -> log.info("Id: {}, Name: {}", p.getId(), p.getName()));
    Player p = playerRepository.findById(1);
    log.info("ID 1 belongs to player: {}", p.getName());
    playerRepository.remove(p);
    p = playerRepository.findById(1);
    log.info("ID 1 exists?: {}", p != null);
    // simple save
    Integer id = playerRepository.save(player);
    log.info("Saved player with ID {}", id);
    p = playerRepository.findById(id);
    log.info("Player has expected id? {}", p != null);
    playerRepository.remove(p);
  }

  public static void teamDemo(AnnotationConfigApplicationContext ctx) {
    TeamRepository teamRepository = ctx.getBean(TeamRepository.class);
    Team team = Team.instance("Red");
    Player p1 = Player.instance("P1", team);
    Player p2 = Player.instance("P2", team);
    team.setPlayers(Arrays.asList(p1, p2));
    teamRepository.saveOrUpdate(team);
    // check if the team was saved
    team = teamRepository.findByName("Red");
    log.info("Team with ID 1 is {} and has {} players", team.getName(), team.getPlayers().size());
    PlayerRepository playerRepository = ctx.getBean(PlayerRepository.class);
    p1 = playerRepository.findByName("P1");
    log.info("Player(1) name is {}, and plays for {}", p1.getName(), p1.getTeam().getName());
    p1 = playerRepository.findByName("P2");
    log.info("Player(1) name is {}, and plays for {}", p2.getName(), p2.getTeam().getName());
    // deleting the team deletes the players
    teamRepository.remove(team);
    List<Player> players = playerRepository.find();
    log.info("Team deleted, {} players remaining", players.size());
  }
}
