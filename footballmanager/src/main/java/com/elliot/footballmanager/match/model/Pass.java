package com.elliot.footballmanager.match.model;

import com.elliot.footballmanager.match.FootballTeamMatchStats;
import com.elliot.footballmanager.match.RandomNumberGenerator;
import com.elliot.footballmanager.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The pass object is used to select a passing strategy (Short / Long range). This strategy is used
 * to determine the next player to be assigned possession of the Football within the MatchEngine.
 *
 * @author Elliot
 */
public class Pass extends MatchEvent {

//  private static Integer SHORT_RANGE_PASSING_DISTANCE = 2;
//  private static String SHORT_PASSING_RANGE = "SHORT_RANGE";
//  private static String LONG_PASSING_RANGE = "LONG_RANGE";

  private Football football;
  private List<Player> squadCurrentlyInPossession, squadCurrentlyNotInPossession;

  private FootballTeamMatchStats matchStats;

  private Player playerPrevInPossesion;
  private Player playerSelectedForPass;

  public Pass() {

  }

  public Pass(List<Player> squadCurrentlyInPossession, List<Player> squadCurrentlyNotInPossession, Football football, FootballTeamMatchStats footballTeamMatchStats) {
    this.squadCurrentlyInPossession = squadCurrentlyInPossession;
    this.squadCurrentlyNotInPossession = squadCurrentlyNotInPossession;
    this.football = football;
    this.matchStats = footballTeamMatchStats;
  }

  public Player getPlayerTheBallIsBeingPassedTo() {
    List<Player> playersAvailableToPassTo;
    if (RandomNumberGenerator.getRandomNumberBetweenZeroAndOneHundred() < 60)
      playersAvailableToPassTo = getPlayersWithinSpecifiedPassingRange((double) football.getPlayerInPossession().getTechnicalAttributes().getShortPassing() /50.0);
    else
      playersAvailableToPassTo = getPlayersWithinSpecifiedPassingRange((double) football.getPlayerInPossession().getTechnicalAttributes().getLongPassing() /10.0);

    //TODO - add possibility for pass to arrive wrongly (find player nearby to selected player and give them the ball) (depending on short/long passing stat and receiver ball control stat);
    setPlayerSelectedForPass(playersAvailableToPassTo.get(RandomNumberGenerator
        .getRandomNumberBetweenZeroAnGivenNumber(playersAvailableToPassTo.size())));

    double maxDist = 10;
    if(playerSelectedForPass.getTechnicalAttributes().getBallControl() < 40 + RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(50))
      if(playerSelectedForPass.getTechnicalAttributes().getBallControl() < RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(100))
        for(Player player : squadCurrentlyNotInPossession){
          if(player.distanceToPlayer(player) < maxDist) {
            playerSelectedForPass = player;
            maxDist = player.distanceToPlayer(player);
          }
        }

    if(squadCurrentlyInPossession.contains(playerSelectedForPass))
      matchStats.addPass();

    doesEventNeedToBeLogged();
    return getPlayerSelectedForPass();
  }

  private List<Player> getPlayersWithinSpecifiedPassingRange(double passingRange) {
    List<Player> playersInShortRange = new ArrayList<>();
    List<Player> playersInLongRange = new ArrayList<>();

    for (Player player : squadCurrentlyInPossession) {
      if (player.equals(football.getPlayerInPossession()))
        continue;
      if (isWithinRangePassingDistance(player, passingRange))
        playersInShortRange.add(player);
      else
        playersInLongRange.add(player);
//      playerPrevInPossesion = player;
    }
    return !playersInShortRange.isEmpty() ? playersInShortRange : playersInLongRange;

//    if () {
//      return playersInShortRange.size() != 0 ? playersInShortRange : playersInLongRange;
//    } else {
//      return playersInLongRange.size() != 0 ? playersInLongRange : playersInShortRange;
//    }
  }

  private boolean isWithinRangePassingDistance(Player player, double passingRange) {
    return player.distanceToFootball(football) < passingRange;
  }

  public Player getPlayerSelectedForPass() {
    return playerSelectedForPass;
  }

  public void setPlayerSelectedForPass(Player playerSelectedForPass) {
    this.playerSelectedForPass = playerSelectedForPass;
  }

  @Override
  protected String buildMatchEventString() {
    StringBuilder message = new StringBuilder();
    message.append(getCurrentGameTime() + " ");
    message.append(football.getPlayerInPossession().getName());
    message.append(" (" + football.getPlayerInPossession().getCurrentClub().getTeamName() + ")");
    message.append(" passes to ");
    message.append(getPlayerSelectedForPass().getName());
    message.append(" (" + getPlayerSelectedForPass().getCurrentClub().getTeamName() + ")");

    return message.toString();
  }
}
