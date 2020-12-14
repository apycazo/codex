package apycazo.codex.hibernate.demo;

import apycazo.codex.hibernate.spring.SpringPersistenceConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitPlatform.class)
public class DataDemoAppTest {

  private static AnnotationConfigApplicationContext ctx;

  @BeforeEach
  void init() {
    ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringPersistenceConfig.class);
    ctx.register(DemoPersistenceConfig.class);
    ctx.refresh();
  }

  @AfterEach
  void tearDown() {
    ctx.stop();
  }

  @Test
  void lifecycle_test() {
    TeamRepository teamRepository = ctx.getBean(TeamRepository.class);
    String teamName = "Red";
    String player1Name = "P1";
    String player2Name = "P2";
    Team team = Team.instance(teamName);
    Player p1 = Player.instance(player1Name, team);
    Player p2 = Player.instance(player2Name, team);
    team.setPlayers(Arrays.asList(p1, p2));
    teamRepository.saveOrUpdate(team);
    // check if the team was saved
    team = teamRepository.findByName(teamName);
    assertThat(team).isNotNull();
    assertThat(team.getName()).isEqualTo(teamName);
    assertThat(team.getPlayers()).isNotNull();
    assertThat(team.getPlayers()).hasSize(2);
    PlayerRepository playerRepository = ctx.getBean(PlayerRepository.class);
    p1 = playerRepository.findByName(player1Name);
    assertThat(p1).isNotNull();
    assertThat(p1.getName()).isEqualTo(player1Name);
    assertThat(p1.getTeam()).isNotNull();
    assertThat(p1.getTeam().getName()).isEqualTo(teamName);
    p2 = playerRepository.findByName(player2Name);
    assertThat(p2).isNotNull();
    assertThat(p2.getName()).isEqualTo(player2Name);
    assertThat(p2.getTeam()).isNotNull();
    assertThat(p2.getTeam().getName()).isEqualTo(teamName);
    // removing the team removes its players
    teamRepository.remove(team);
    assertThat(teamRepository.find()).isEmpty();
    assertThat(playerRepository.find()).isEmpty();
  }

  @Test
  void custom_query_find_players_by_team() {
    TeamRepository teamRepository = ctx.getBean(TeamRepository.class);
    CustomRequestRepository repository = ctx.getBean(CustomRequestRepository.class);
    // --- create red/blue teams
    Stream.of("red", "blue").forEach(color -> {
      Team team = Team.instance(color);
      Player p1 = Player.instance(color + "-1", team);
      Player p2 = Player.instance(color + "-2", team);
      team.setPlayers(Arrays.asList(p1, p2));
      teamRepository.saveOrUpdate(team);
    });
    // --- find all players from blue team
    List<Player> bluePlayers = repository.getPlayersFromTeam("blue");
    assertThat(bluePlayers).isNotNull();
    assertThat(bluePlayers.size()).isEqualTo(2);
    Set<String> playerHandlers = bluePlayers.stream()
      .map(player -> player.getName() + "|" + player.getTeam().getName())
      .collect(Collectors.toSet());
    assertThat(playerHandlers).containsAll(Arrays.asList("blue-1|blue", "blue-2|blue"));
  }
}
