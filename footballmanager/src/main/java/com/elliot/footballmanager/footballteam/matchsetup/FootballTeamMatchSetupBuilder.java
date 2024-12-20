package com.elliot.footballmanager.footballteam.matchsetup;

import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.match.model.Position;

import java.util.*;

/**
 * Provides a way of generating a FootballTeamMatchSetup object for a given footballTeam.
 *
 * @author elliot
 */
public class FootballTeamMatchSetupBuilder {

  private static String FORMATION_SPLIT_CHARACTER = "-";

  private static FootballTeam footballTeam;
  private static List<Player> footballTeamSquad;

  public FootballTeamMatchSetupBuilder() {

  }

  /**
   * Main method that goes through the FootballTeamMatchSetup creating all the required variables.
   *
   * @param incomingFootballTeam The FootballTeam to generate the FootballTeamMatchSetup object
   * for.
   * @return A newly populated FootballTeamMatchObject.
   */
  public static FootballTeamMatchSetup buildNewMatchSetup(FootballTeam incomingFootballTeam, FootballTeamFormation formation) {
    footballTeam = incomingFootballTeam;
    footballTeamSquad = footballTeam.getSquad();

    FootballTeamMatchSetup footballTeamMatchSetup = new FootballTeamMatchSetup();

    MatchDaySquad[] matchDaySquads = generateFormations();

    if(formation == null)
      footballTeamMatchSetup.setSelectedFormation(matchDaySquads[0]);
    else {
      for(MatchDaySquad matchDaySquad : matchDaySquads)
        if(matchDaySquad.getFormation().toString().equals(formation.toString())){
          footballTeamMatchSetup.setSelectedFormation(matchDaySquad);
          break;
        }
    }
    footballTeamMatchSetup.setAvailableFormations(matchDaySquads);

    footballTeamMatchSetup.setFreekickTaker(getBestFreekickTaker(footballTeam.getSquad()));
    footballTeamMatchSetup.setPenaltyTaker(getBestPenaltyTaker(footballTeam.getSquad()));
    footballTeamMatchSetup.setCornerTaker(getBestCornerTaker(footballTeam.getSquad()));

    return footballTeamMatchSetup;
  }

  /**
   * For a given FootballTeam this method will generate a list of Formations (The size is limited by
   * MAXIMUM_STORED_FORMATIONS within the FootballTeamMatchSetup class). One will be randomly chosen
   * and then set as the selected formation.
   */
  private static MatchDaySquad[] generateFormations() {
    Set<FootballTeamFormation> uniqueFormations = new HashSet<FootballTeamFormation>();
    MatchDaySquad[] matchDaySquads = new MatchDaySquad[FootballTeamMatchSetup.MAXIMUM_STORED_FORMATIONS];

    for (int i = 0; i < FootballTeamMatchSetup.MAXIMUM_STORED_FORMATIONS; i++) {
      MatchDaySquad matchDaySquadInformation = new MatchDaySquad();
      FootballTeamFormation formation = getUniqueFormation(uniqueFormations);

      matchDaySquadInformation.setFormation(formation);
      matchDaySquadInformation.setStartingLineup(buildStartingLineup(formation));
      matchDaySquadInformation.setSubstitutions(buildSubstitutions(matchDaySquadInformation));
      matchDaySquadInformation.setReserves(buildReserves(matchDaySquadInformation));

      matchDaySquads[i] = matchDaySquadInformation;
    }
    return matchDaySquads;
  }

  private static FootballTeamFormation getUniqueFormation(
      Set<FootballTeamFormation> uniqueFormations) {

    FootballTeamFormation footballTeamFormation;
    footballTeamFormation = FootballTeamFormation.getRandomFormation();

    if (uniqueFormations.contains(footballTeamFormation)) {
      return getUniqueFormation(uniqueFormations);
    } else {
      uniqueFormations.add(footballTeamFormation);
      return footballTeamFormation;
    }
  }

  private static Player[] buildStartingLineup(FootballTeamFormation footballTeamFormation) {
    //TODO: Find an alternative that will work with more complex formations (E.g. 4-1-2-1-2)
    //TODO: Change Squad variable inside FootballTeam into its own separate class (Can add HashMap for Defender - SubList of Squad | Attacker - SubList)
    List<Player> startingLineup = new ArrayList<Player>();
    String[] formationParts = footballTeamFormation.getFormationName()
        .split(FORMATION_SPLIT_CHARACTER);

    addBestGoalKeeper(startingLineup);
    addBestDefenders(startingLineup, Integer.valueOf(formationParts[0]));
    addBestMidfielders(startingLineup, Integer.valueOf(formationParts[1]));
    addBestStrikers(startingLineup, Integer.valueOf(formationParts[2]));

    return startingLineup.toArray(new Player[MatchDaySquad.MATCH_DAY_SQUAD]);
  }

  private static void addBestGoalKeeper(List<Player> startingLineup) {
    List<Player> goalkeepers = new ArrayList<Player>();
    for (Player player : footballTeamSquad) {
      if (player.getPreferredPositions().contains(Position.GK)) {
        goalkeepers.add(player);
      }
    }
    //TODO: Sort players within lists based upon their attributes then add the best 11 to starting lineup
//    startingLineup.add(goalkeepers.get(0));
    addPlayerToStartingLineup(startingLineup, goalkeepers, 1);
  }

  private static void addBestDefenders(List<Player> startingLineup, Integer numberOfDefenders) {
    List<Player> defenders = buildGeneralPositionsByPreferredPositions(
        Position.GeneralisedPosition.DEFENDER);
    addPlayerToStartingLineup(startingLineup, defenders, numberOfDefenders);
  }

  private static void addBestMidfielders(List<Player> startingLineup, Integer numberOfMidfielders) {
    List<Player> midfielders = buildGeneralPositionsByPreferredPositions(
        Position.GeneralisedPosition.MIDFIELDER);
    addPlayerToStartingLineup(startingLineup, midfielders, numberOfMidfielders);
  }

  private static void addBestStrikers(List<Player> startingLineup, Integer numberOfStrikers) {
    List<Player> strikers = buildGeneralPositionsByPreferredPositions(
        Position.GeneralisedPosition.ATTACKER);
    addPlayerToStartingLineup(startingLineup, strikers, numberOfStrikers);
  }

  private static List<Player> buildGeneralPositionsByPreferredPositions(
      Position.GeneralisedPosition generalisedPositionToSearchFor) {
    List<Player> generalPositionMatches = new ArrayList<Player>();

    for (Player player : footballTeamSquad) {
      for (Position preferredPosition : player.getPreferredPositions()) {
        if (Position.getGeneralPositionBySpecificPosition(preferredPosition)
            .equals(generalisedPositionToSearchFor)) {
          generalPositionMatches.add(player);
        }
      }
    }
    return generalPositionMatches;
  }

  private static void addPlayerToStartingLineup(List<Player> startingLineup,
      List<Player> playersToAdd, Integer numberOfPlayersToAdd) {
    int playersAddedSoFar = 0;

    for (Player player : playersToAdd) {
      if (!startingLineup.contains(player) && playersAddedSoFar < numberOfPlayersToAdd) {
        playersAddedSoFar++;
        startingLineup.add(player);
      }
    }
    //found error: players are split before adding to team and as such there may be a lack of players for defense, midfield of attack
    //which leaves the lineup not filled completely
    //TODO - properly iron out a solution as to not use attackers for defense before having added attackers
    if(playersAddedSoFar < numberOfPlayersToAdd) {
      for (Player player : footballTeamSquad) {
        if(!startingLineup.contains(player) && playersAddedSoFar < numberOfPlayersToAdd){
          playersAddedSoFar++;
          startingLineup.add(player);
        }
      }
    }
  }

  private static Player[] buildSubstitutions(MatchDaySquad matchDaySquad) {
    Set<Player> ineligiblePlayers = new HashSet<Player>(
        Arrays.asList(matchDaySquad.getStartingLineup()));
    Player[] substitutions = new Player[MatchDaySquad.MATCH_DAY_SUBSTITUTIONS];
    int remainingPlayers = 0;

    for (Player player : footballTeamSquad) {
      if (!ineligiblePlayers.contains(player)
          && remainingPlayers < MatchDaySquad.MATCH_DAY_SUBSTITUTIONS) {
        substitutions[remainingPlayers] = player;
        remainingPlayers++;
      }
    }
    return substitutions;
  }

  private static Player[] buildReserves(MatchDaySquad matchDaySquad) {
    Set<Player> ineligiblePlayers = new HashSet<Player>(
            Arrays.asList(matchDaySquad.getStartingLineup()));
    Set<Player> ineligiblePlayers2 = new HashSet<Player>(
            Arrays.asList(matchDaySquad.getSubstitutions()));
    Player[] reserves = new Player[footballTeam.getSquad().size()-MatchDaySquad.MATCH_DAY_SQUAD-MatchDaySquad.MATCH_DAY_SUBSTITUTIONS];
    int remainingPlayers = 0;

    for (Player player : footballTeamSquad) {
      if (!ineligiblePlayers.contains(player) && !ineligiblePlayers2.contains(player)) {
        reserves[remainingPlayers] = player;
        remainingPlayers++;
      }
    }
    return reserves;
  }

  private static Player getBestFreekickTaker(List<Player> squad) {
    Player bestFreekickAbility = squad != null ? squad.get(0) : new Player();
    for (Player player : squad) {
      if (player.getTechnicalAttributes().getFreeKick() > bestFreekickAbility
          .getTechnicalAttributes().getFreeKick()) {
        bestFreekickAbility = player;
      }
    }
    return bestFreekickAbility;
  }

  private static Player getBestPenaltyTaker(List<Player> squad) {
    Player bestPenaltyAbility = squad != null ? squad.get(0) : new Player();
    for (Player player : squad) {
      if (player.getTechnicalAttributes().getPenalties() > bestPenaltyAbility
          .getTechnicalAttributes().getPenalties()) {
        bestPenaltyAbility = player;
      }
    }
    return bestPenaltyAbility;
  }

  private static Player getBestCornerTaker(List<Player> squad) {
    Player bestCornerTaker = squad != null ? squad.get(0) : new Player();
    for (Player player : squad) {
      if (player.getTechnicalAttributes().getCrossing() > bestCornerTaker.getTechnicalAttributes()
          .getCrossing()) {
        bestCornerTaker = player;
      }
    }
    return bestCornerTaker;
  }

  private static void setFootballTeam(FootballTeam footballTeam) {
    FootballTeamMatchSetupBuilder.footballTeam = footballTeam;
  }
}
