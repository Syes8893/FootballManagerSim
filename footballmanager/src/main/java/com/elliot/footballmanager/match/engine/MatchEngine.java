package com.elliot.footballmanager.match.engine;

import com.elliot.footballmanager.fixture.Fixture;
import com.elliot.footballmanager.footballteam.FootballTeam;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetup;
import com.elliot.footballmanager.gamemanager.GameManager;
import com.elliot.footballmanager.match.FootballTeamMatchStats;
import com.elliot.footballmanager.match.MatchResult;
import com.elliot.footballmanager.match.model.Football;
import com.elliot.footballmanager.match.model.Movement;
import com.elliot.footballmanager.match.model.Pass;
import com.elliot.footballmanager.match.model.Tackle;
import com.elliot.footballmanager.match.model.pitch.FootballPitch;
import com.elliot.footballmanager.match.model.pitch.FootballPitchBuilder;
import com.elliot.footballmanager.match.model.pitch.FootballPitchPlayerPlacer;
import com.elliot.footballmanager.player.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The MatchEngine is where a simulation of a football
 * match takes place. Given two FootballTeams a match is
 * simulated and a MatchResult is given.
 * @author Elliot
 */
public class MatchEngine {

    private static Fixture fixture;

    private static FootballTeam homeTeam;
    private static FootballTeam awayTeam;

    public static FootballTeamMatchSetup homeTeamMatchSetup;
    public static FootballTeamMatchSetup awayTeamMatchSetup;

    private static Map<FootballTeam, FootballTeamMatchStats> footballTeamToMatchStats;

    public static FootballPitch[][] footballPitch;
    private static Football football;

    private static FootballTeamMatchStats matchStats;

    // Private Constructor to avoid instantiation of MatchEngine objects
    private MatchEngine() {

    }

    public static MatchResult beginFootballMatchSimulator(GameManager gameManager) {
        beginPreMatchSetup(gameManager);

        buildFootballPitch();
        addPlayersToPitch();

        giveATeamTheFootball();

        simulateOneHalfOfFootball();
        simulateOneHalfOfFootball();

        return null;
    }

    private static void beginPreMatchSetup(GameManager gameManager) {
        initialiseFixtureInformation(gameManager);
        initialiseFootballTeamMatchStats();
        initialiseFootballTeamSquads();
    }

    private static void initialiseFixtureInformation(GameManager gameManager) {
        fixture = gameManager.getUpcomingFixtures().remove();

        homeTeam = fixture.getHomeTeam();
        awayTeam = fixture.getAwayTeam();
    }

      private static void initialiseFootballTeamMatchStats() {
        footballTeamToMatchStats = new HashMap<FootballTeam, FootballTeamMatchStats>();
        footballTeamToMatchStats.put(homeTeam, new FootballTeamMatchStats(homeTeam));
        footballTeamToMatchStats.put(awayTeam, new FootballTeamMatchStats(awayTeam));
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
        if (football == null) {
            Player[] players = homeTeamMatchSetup.getSelectedFormation().getStartingLineup();
            Player playerToKickOffGame = players[6];

            football = new Football(playerToKickOffGame.getCurrentXCoordinate(), playerToKickOffGame.getCurrentYCoordinate(),
                    playerToKickOffGame);
            return;
        }

        if (football.getPlayerInPossession().getCurrentClub().equals(homeTeam)) {
            Player[] players = homeTeamMatchSetup.getSelectedFormation().getStartingLineup();
            football.setPlayerInPossession(players[6]);
        } else {
            Player[] players = awayTeamMatchSetup.getSelectedFormation().getStartingLineup();
            football.setPlayerInPossession(players[6]);
        }

    }

    private static void simulateOneHalfOfFootball() {
        double startTime = MatchEngineConstants.MINUTES_REMAINING_IN_HALF;
        double timeRemainingInHalf = MatchEngineConstants.MINUTES_REMAINING_IN_HALF;

        // TODO: Look into alternative for Double due to precision issues, BigDecimal suitable replacement?
        while (timeRemainingInHalf > 0D) {
            determineNextGameAction();
            timeRemainingInHalf -= 0.10D;
            Double currentTime = startTime - timeRemainingInHalf;

            System.out.println("[" + String.format("%.2f", currentTime) + "]" + football.getPlayerInPossession().getName() + " " + football.getCurrentXCoordinate() + " " + football.getCurrentYCoordinate());

        }
    }

    private static void determineNextGameAction() {
        if (RandomNumberGenerator.getRandomNumberBetweenZeroAndOneHundred() < 50) {
            passToAnotherTeamMate();
        } else {
            movePlayerInPossessionToNewTile();
        }

        // After a GameAction has happened begin moving all out of position players back to preferred Coordinates
        moveNonPossessionPlayersToPreferredCoordinates();
        checkIfAPlayerCanAttemptATackle();
        updatePlayersTackledRecovery();
    }

    private static void passToAnotherTeamMate() {
        Pass pass = new Pass(getSquadCurrentlyInPossession(), football);
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
                        && football.getCurrentXCoordinate().equals(player.getCurrentXCoordinate() + 1)
                        || football.getCurrentXCoordinate().equals(player.getCurrentXCoordinate() - 1)
                        || football.getCurrentYCoordinate().equals(player.getCurrentYCoordinate() + 1)
                        || football.getCurrentYCoordinate().equals(player.getCurrentYCoordinate() - 1))
                .collect(Collectors.toList());

        if (players.size() > 0) {
            Player playerToChallengeForPossession = players.get(RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(players.size()));

            Tackle tackle = new Tackle(playerToChallengeForPossession, football);
            tackle.attemptTackleOnPlayerInPossession();
        }
    }

    private static void updatePlayersTackledRecovery() {
        for (Player player : getAllPlayersFromBothSquads()) {
            player.setGameTicksUntilRecoveredFromTackle(player.getGameTicksUntilRecoveredFromTackle() - 1);
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
        footballPitch[player.getCurrentXCoordinate()][player.getCurrentYCoordinate()].removePlayerFromTile(player);
    }

    private static void addPlayerToNewTile(Player player) {
        footballPitch[player.getCurrentXCoordinate()][player.getCurrentYCoordinate()].addPlayerToTile(player);
    }

    private static List<Player> getSquadCurrentlyInPossession() {
        if (football.getPlayerInPossession().getCurrentClub().getTeamName().equals(homeTeam.getTeamName())) {
            return Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup());
        } else {
            return Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup());
        }
    }

    private static List<Player> getSquadNotCurrentlyInPossession() {
        if (football.getPlayerInPossession().getCurrentClub().getTeamName().equals(homeTeam.getTeamName())) {
            return Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup());
        } else {
            return Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup());
        }
    }

    private static List<Player> getAllPlayersFromBothSquads() {
        List<Player> allPlayersOnFootballPitch = new ArrayList<Player>();
        allPlayersOnFootballPitch.addAll(Arrays.asList(homeTeamMatchSetup.getSelectedFormation().getStartingLineup()));
        allPlayersOnFootballPitch.addAll(Arrays.asList(awayTeamMatchSetup.getSelectedFormation().getStartingLineup()));
        return allPlayersOnFootballPitch;
    }

    private static void persistMatchResultToDatabase(MatchResult matchResult) {
        MatchEngineDao matchEngineDao = new MatchEngineDaoImpl();
        matchEngineDao.persistResultToDatabase(matchResult);
    }
}
