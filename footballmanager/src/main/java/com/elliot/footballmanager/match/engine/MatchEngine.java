package com.elliot.footballmanager.match.engine;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.entity.Standing;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetup;
import com.elliot.footballmanager.match.FootballTeamMatchStats;
import com.elliot.footballmanager.match.MatchResult;
import com.elliot.footballmanager.match.RandomNumberGenerator;
import com.elliot.footballmanager.match.model.*;
import com.elliot.footballmanager.match.model.pitch.FootballPitch;
import com.elliot.footballmanager.match.model.pitch.FootballPitchBuilder;
import com.elliot.footballmanager.match.model.pitch.FootballPitchPlayerPlacer;
import com.elliot.footballmanager.standings.StandingBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * The MatchEngine is where a simulation of a football match takes place. Given two FootballTeams a
 * match is simulated and a MatchResult is given.
 *
 * @author Elliot
 */
public class MatchEngine {

  private static boolean logGameEvents = false;

  private static Fixture fixture;

  private static FootballTeam homeTeam;
  private static FootballTeam awayTeam;

  //TODO - add player tiredness during game and make it affect performance
  public static FootballTeamMatchSetup homeTeamMatchSetup;
  public static FootballTeamMatchSetup awayTeamMatchSetup;

  public static Map<String, FootballTeamMatchStats> footballTeamToMatchStats;

  public static FootballPitch[][] footballPitch;
  private static Football football;

  private static double currentTimeInGame = 0.0d;

  // Private Constructor to avoid instantiation of MatchEngine objects
  private MatchEngine() {

  }

  //TODO - add stat tracking for players during a seasons (so link player ID in table to goals scored etc)
  public static MatchResult beginFootballMatchSimulator(Fixture fixture, HashMap<Integer, Standing> standingList) {
    beginPreMatchSetup(fixture);

    buildFootballPitch();
    addPlayersToPitch();

    giveATeamTheFootball();

    if(isLoggingGameEvents())
      System.out.print("\n" + ColorUtils.YELLOW_BOLD_BRIGHT + "KICKOFF" + ColorUtils.RESET + "\n\n");
    simulateOneHalfOfFootball();
    if(isLoggingGameEvents()){
      //TODO - Add half time menu so player can modify team setup during halftime
      System.out.print("\r             ");
      System.out.print("\n" + ColorUtils.YELLOW_BOLD_BRIGHT + "HALF TIME" + ColorUtils.RESET
              + " (" + homeTeam.getTeamName()
              + " [" + footballTeamToMatchStats.get(homeTeam.getTeamName()).getGoals() + "]"
              + " - [" + footballTeamToMatchStats.get(awayTeam.getTeamName()).getGoals() + "]"
              + " " + awayTeam.getTeamName() + ")"
              + "\n\n");
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    setCurrentTimeInGame(45.00D);
    simulateOneHalfOfFootball();
    if(isLoggingGameEvents()){
      System.out.print("\r             ");
      System.out.print("\n" + ColorUtils.YELLOW_BOLD_BRIGHT + "FULL TIME" + ColorUtils.RESET
              + " (" + homeTeam.getTeamName()
              + " [" + footballTeamToMatchStats.get(homeTeam.getTeamName()).getGoals() + "]"
              + " - [" + footballTeamToMatchStats.get(awayTeam.getTeamName()).getGoals() + "]"
              + " " + awayTeam.getTeamName() + ")"
              + "\n");
    }
    //if cup match, shoot penalties;
    if(fixture.isCup() && Objects.equals(footballTeamToMatchStats.get(homeTeam.getTeamName()).getGoals(), footballTeamToMatchStats.get(awayTeam.getTeamName()).getGoals()))
      simulatePenalties();
    return beginPostMatchSetup(standingList);
  }

  private static void beginPreMatchSetup(Fixture fixture) {
    resetMatchEngineVariables();

    initialiseFixtureInformation(fixture);
    initialiseFootballTeamMatchStats();
    initialiseFootballTeamSquads();
  }

  private static void resetMatchEngineVariables() {
    currentTimeInGame = 0.0d;
  }

  private static void initialiseFixtureInformation(Fixture fixture) {
    MatchEngine.fixture = fixture;

    homeTeam = fixture.getHomeTeam();
    awayTeam = fixture.getAwayTeam();
  }

  private static void initialiseFootballTeamMatchStats() {
    footballTeamToMatchStats = new HashMap<String, FootballTeamMatchStats>();
    footballTeamToMatchStats.put(homeTeam.getTeamName(), new FootballTeamMatchStats(homeTeam));
    footballTeamToMatchStats.put(awayTeam.getTeamName(), new FootballTeamMatchStats(awayTeam));
  }

  private static void initialiseFootballTeamSquads() {
    homeTeamMatchSetup = homeTeam.getMatchSetup();
    awayTeamMatchSetup = awayTeam.getMatchSetup();
  }

  private static void buildFootballPitch() {
    footballPitch = FootballPitchBuilder.buildNewFootballPitch();
  }

  private static void addPlayersToPitch() {
    FootballPitchPlayerPlacer.addPlayersToFootballPitch();
  }

  private static void giveATeamTheFootball() {
    Player[] players = homeTeamMatchSetup.getSelectedFormation().getStartingLineup();
    Player playerToKickOffGame = players[6];

    football = new Football(playerToKickOffGame.getCurrentXCoordinate(),
        playerToKickOffGame.getCurrentYCoordinate(),
        playerToKickOffGame);

    if (football.getPlayerInPossession().getCurrentClub().equals(homeTeam)) {
      players = homeTeamMatchSetup.getSelectedFormation().getStartingLineup();
      football.setPlayerInPossession(players[6]);
    } else {
      players = awayTeamMatchSetup.getSelectedFormation().getStartingLineup();
      football.setPlayerInPossession(players[6]);
    }

  }

  private static void simulateOneHalfOfFootball() {
    double simulationTime =
        MatchEngine.getCurrentTimeInGame() < MatchEngineConstants.MINUTES_IN_FIRST_HALF ?
            MatchEngineConstants.MINUTES_IN_FIRST_HALF + (double) new Random().nextInt(4)
            : MatchEngineConstants.MINUTES_IN_SECOND_HALF + (double) new Random().nextInt(6);

    while (MatchEngine.getCurrentTimeInGame() <= simulationTime) {
      determineNextGameAction();
      MatchEngine.setCurrentTimeInGame(MatchEngine.getCurrentTimeInGame() + 0.05D);

//      MatchEngine.setCurrentTimeInGame(MatchEngine.getCurrentTimeInGame() + 0.50D);
      isDelayForUserRequired();
    }
  }

  private static void simulatePenalties() {
    int penaltiesTaken = 0;
    int[] penaltyGoals = {0, 0};
    Comparator<Player> penaltySorter = (o1, o2) -> o2.getTechnicalAttributes().getPenalties().compareTo(o1.getTechnicalAttributes().getPenalties());
    List<Player> homePenaltySquad = homeTeam.getSquad();
    homePenaltySquad.sort(penaltySorter);
    List<Player> awayPenaltySquad = awayTeam.getSquad();
    awayPenaltySquad.sort(penaltySorter);
    while (penaltiesTaken%10 != 0 || penaltyGoals[0] == penaltyGoals[1] && penaltiesTaken < 100) {
      Player playerHome = homePenaltySquad.get((penaltiesTaken/2)%homeTeam.getSquad().size());
      int scoringChanceHome = (int)((playerHome.getMentalAttributes().getComposure()
              + playerHome.getTechnicalAttributes().getPenalties()
              + (0.5 * playerHome.getTechnicalAttributes().getShotPower()))/2.5);
      Player gkAway = awayTeam.getMatchSetup().getSelectedFormation().getStartingLineup()[0];
      int savesChanceAway = (gkAway.getGoalkeeperAttributes().getReflexes()
              + gkAway.getGoalkeeperAttributes().getDiving());
      penaltyGoals[0] += scoringChanceHome > savesChanceAway/2.2 ? 1 : 0;
      penaltiesTaken++;

      Player playerAway = awayPenaltySquad.get((penaltiesTaken/2)%awayTeam.getSquad().size());
      int scoringChanceAway = (int)((playerAway.getMentalAttributes().getComposure()
              + playerAway.getTechnicalAttributes().getPenalties()
              + (0.5 * playerAway.getTechnicalAttributes().getShotPower()))/2.5);
      Player gkHome = homeTeam.getMatchSetup().getSelectedFormation().getStartingLineup()[0];
      int savesChanceHome = (gkHome.getGoalkeeperAttributes().getReflexes()
              + gkHome.getGoalkeeperAttributes().getDiving());
      penaltyGoals[1] += scoringChanceAway > savesChanceHome/2.2 ? 1 : 0;
      penaltiesTaken++;
    }
    if(penaltiesTaken >= 99)
      footballTeamToMatchStats.get(homeTeam.getTeamName()).setPenaltyShootout(penaltyGoals[0]+1);
    else
      footballTeamToMatchStats.get(homeTeam.getTeamName()).setPenaltyShootout(penaltyGoals[0]);
    footballTeamToMatchStats.get(awayTeam.getTeamName()).setPenaltyShootout(penaltyGoals[1]);
  }

  private static void determineNextGameAction() {
    footballTeamToMatchStats.get(football.getPlayerInPossession().getCurrentClub().getTeamName()).addPosessionTick();
    if (RandomNumberGenerator.getRandomNumberBetweenZeroAndOneHundred() < 50) {
      passToAnotherTeamMate();
    } else {
      movePlayerInPossessionToNewTile();
    }

    // After a GameAction has happened begin moving all out of position players back to preferred Coordinates
    checkIfAPlayerCanAttemptATackle();

    checkIfPlayerAttemptsShotAtGoal();

    moveNonPossessionPlayersToPreferredCoordinates();
    updatePlayersTackledRecovery();
  }

  private static void passToAnotherTeamMate() {
    Pass pass = new Pass(getSquadCurrentlyInPossession(), getSquadNotCurrentlyInPossession(), football, footballTeamToMatchStats.get(football.getPlayerInPossession().getCurrentClub().getTeamName()));
    Player newPlayerInPossession = pass.getPlayerTheBallIsBeingPassedTo();
    football.setPlayerInPossession(newPlayerInPossession);
  }

  private static void movePlayerInPossessionToNewTile() {
    Player playerInPossession = football.getPlayerInPossession();
    removePlayerFromOldTile(playerInPossession);

    Movement movement = new Movement();
    movement.movePlayerToNewTile(playerInPossession);

    addPlayerToNewTile(playerInPossession);
    football.updatePlayerInPossessionCoordinates();
  }

  private static void checkIfAPlayerCanAttemptATackle() {
    List<Player> players = getSquadNotCurrentlyInPossession().stream()
        .filter(player -> player.getGameTicksUntilRecoveredFromTackle().equals(0)
            && player.distanceToFootball(football) < 1).toList();

    if (!players.isEmpty()) {
      Player playerToChallengeForPossession = players
          .get(RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(players.size()));

      Tackle tackle = new Tackle(playerToChallengeForPossession, football);
      tackle.attemptTackleOnPlayerInPossession();
    }
  }

  private static void checkIfPlayerAttemptsShotAtGoal() {
    Shot shot = new Shot(football.getPlayerInPossession());
    Player opposingTeamsGoalKeeper = getSquadNotCurrentlyInPossession().get(0);

    if (shot.doesPlayerDecideToShoot(opposingTeamsGoalKeeper)) {
      FootballTeamMatchStats matchStats = footballTeamToMatchStats
          .get(football.getPlayerInPossession().getCurrentClub().getTeamName());

      shot.attemptShot(football, opposingTeamsGoalKeeper, matchStats);
    }
  }

  private static void updatePlayersTackledRecovery() {
    for (Player player : getAllPlayersFromBothSquads()) {
      if (player.getGameTicksUntilRecoveredFromTackle() > 0) {
        player.setGameTicksUntilRecoveredFromTackle(
            player.getGameTicksUntilRecoveredFromTackle() - 1);
      }
    }
  }

  private static void moveNonPossessionPlayersToPreferredCoordinates() {
    for (Player player : getAllPlayersFromBothSquads()) {
      if (football.getPlayerInPossession().equals(player)) {
        continue;
      }

      if (!player.getCurrentXCoordinate().equals(player.getPreferredXCoordinate())
          || !player.getCurrentYCoordinate().equals(player.getPreferredYCoordinate())) {
        Movement movement = new Movement();
        movement.movePlayerNotInPossessionBackToPreferredPositions(player);
      }
    }
  }

  private static void removePlayerFromOldTile(Player player) {
    footballPitch[player.getCurrentXCoordinate()][player.getCurrentYCoordinate()]
        .removePlayerFromTile(player);
  }

  private static void addPlayerToNewTile(Player player) {
    footballPitch[player.getCurrentXCoordinate()][player.getCurrentYCoordinate()]
        .addPlayerToTile(player);
  }

  private static List<Player> getSquadCurrentlyInPossession() {
    if (football.getPlayerInPossession().getCurrentClub().getTeamName()
        .equals(homeTeam.getTeamName())) {
      return Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup());
    } else {
      return Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup());
    }
  }

  private static List<Player> getSquadNotCurrentlyInPossession() {
    if (football.getPlayerInPossession().getCurrentClub().getTeamName()
        .equals(homeTeam.getTeamName())) {
      return Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup());
    } else {
      return Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup());
    }
  }

  private static List<Player> getAllPlayersFromBothSquads() {
    List<Player> allPlayersOnFootballPitch = new ArrayList<Player>();
    allPlayersOnFootballPitch
        .addAll(Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup()));
    allPlayersOnFootballPitch
        .addAll(Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup()));
    return allPlayersOnFootballPitch;
  }

  public static boolean isLoggingGameEvents() {
    return logGameEvents;
  }

  public static void setIsLoggingGameEvents(boolean isLoggingGameEvents) {
    MatchEngine.logGameEvents = isLoggingGameEvents;
  }

  private static void isDelayForUserRequired() {
    // If the logGameEvents flag is true, Add small delay slowing number of Events displayed
    //TODO - only log major events
        try {
            if (MatchEngine.isLoggingGameEvents()) {
//                Thread.sleep(65);
                Thread.sleep(38);
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
  }

  public static double getCurrentTimeInGame() {
    return currentTimeInGame;
  }

  private static void setCurrentTimeInGame(double currentTimeInGame) {
    MatchEngine.currentTimeInGame = BigDecimal.valueOf(currentTimeInGame)
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }

  private static MatchResult beginPostMatchSetup(HashMap<Integer, Standing> standingList) {
    MatchResult matchResult = buildMatchResult();

    if(!fixture.isCup()){
//      updateStandingInformation(matchResult);
//      persistMatchResultToDatabase(matchResult);
      StandingBuilder standingBuilder = new StandingBuilder(matchResult, standingList);
      standingBuilder.buildStandingsFromMatchResult();
    }
    return matchResult;
  }

  private static MatchResult buildMatchResult() {
    FootballTeamMatchStats homeTeamMatchStats = MatchEngine.footballTeamToMatchStats
        .get(homeTeam.getTeamName());
    FootballTeamMatchStats awayTeamMatchStats = MatchEngine.footballTeamToMatchStats
        .get(awayTeam.getTeamName());
    return new MatchResult(MatchEngine.fixture, homeTeamMatchStats, awayTeamMatchStats);
  }

//  private static void updateStandingInformation(MatchResult matchResult) {
//    StandingBuilder standingBuilder = new StandingBuilder(matchResult);
//    standingBuilder.buildStandingsFromMatchResult();
//    standingBuilder.updateStandingsInDatabase();
//  }
//
//  private static void persistMatchResultToDatabase(MatchResult matchResult) {
//    MatchEngineDao matchEngineDao = new MatchEngineDaoImpl();
//    matchEngineDao.persistResultToDatabase(matchResult);
//  }
}
