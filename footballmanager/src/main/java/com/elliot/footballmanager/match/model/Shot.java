package com.elliot.footballmanager.match.model;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.Manager;
import com.elliot.footballmanager.entity.dao.ManagerDao;
import com.elliot.footballmanager.match.FootballTeamMatchStats;
import com.elliot.footballmanager.match.RandomNumberGenerator;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.match.engine.MatchEngine;
import com.elliot.footballmanager.match.engine.ShotConstants;

import java.util.Random;

/**
 * Used within the MatchEngine to determine whether a Player decides to shoot and calculates whether
 * he scores.
 *
 * @author Elliot
 */
public class Shot extends MatchEvent {

  private Player playerTakingShot;
  private Integer numberOfTilesAwayFromGoal;
  private ShotOutcome shotOutcome;

  public Shot() {

  }

  public Shot(Player playerTakingShot) {
    this.playerTakingShot = playerTakingShot;
    numberOfTilesAwayFromGoal = getPlayersDistanceFromGoal();
  }

  private Integer getPlayersDistanceFromGoal() {
    return Math.abs(getPlayerTakingShot().getCurrentXCoordinate() - getPlayerTakingShot()
        .getOpposingTeamsGoal()[0]);
  }

  public boolean doesPlayerDecideToShoot(Player opposingTeamsGoalkeeper) {
    int keeperSkill = (opposingTeamsGoalkeeper.getGoalkeeperAttributes().getDiving() + opposingTeamsGoalkeeper.getGoalkeeperAttributes().getReflexes())/30;
    return RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(2) + 12.5 + keeperSkill
        <= playersProbabilityOfScoringAGoal();
  }

  public void attemptShot(Football football, Player opposingTeamsGoalkeeper,
      FootballTeamMatchStats matchStats) {
    int playersChanceOfScoring = playersProbabilityOfScoringAGoal();
    int keeperSkill = (opposingTeamsGoalkeeper.getGoalkeeperAttributes().getDiving()
            + opposingTeamsGoalkeeper.getGoalkeeperAttributes().getReflexes()
    + opposingTeamsGoalkeeper.getGoalkeeperAttributes().getHandling()
    + opposingTeamsGoalkeeper.getGoalkeeperAttributes().getPositioning())/60;
//    System.out.println("GK STATS: " + keeperSkill);
    double randomChanceOfScoring = RandomNumberGenerator.getRandomNumberBetweenZeroAnGivenNumber(4) + 17 + keeperSkill;
    if (randomChanceOfScoring <= (playersChanceOfScoring)) {
      updateMatchStatsGoalScored(matchStats);
      setShotOutcome(ShotOutcome.GOAL_SCORED);
    } else if (randomChanceOfScoring <= 21.5) {
      updateMatchStatsShotSaved(matchStats);
      setShotOutcome(ShotOutcome.SHOT_SAVED);
    } else {
      updateMatchStatsShotMissed(matchStats);
      setShotOutcome(ShotOutcome.SHOT_MISSED);
    }

    giveGoalKeeperTheFootball(football, opposingTeamsGoalkeeper);

    doesEventNeedToBeLogged();
  }

  private void updateMatchStatsGoalScored(FootballTeamMatchStats matchStats) {
    matchStats.incrementGoalsScoredByOne();
  }

  private void updateMatchStatsShotSaved(FootballTeamMatchStats matchStats) {
    matchStats.incrementShotsOnTargetByOne();
  }

  private void updateMatchStatsShotMissed(FootballTeamMatchStats matchStats) {
    matchStats.incrementShotsByOne();
  }

  private void giveGoalKeeperTheFootball(Football football, Player goalkeeper) {
    football.setPlayerInPossession(goalkeeper);
  }

  //TODO: Continue to flesh this out, E,g use additional attributes determining whether they're off balance etc.
  private Integer playersProbabilityOfScoringAGoal() {
    double total = 0;
    total += getPlayerTakingShot().getTechnicalAttributes().getFinishing();
    total += getPlayerTakingShot().getTechnicalAttributes().getShotPower();
//    total += getPlayerTakingShot().getTechnicalAttributes().getCurve();
    //TODO - make curve have effect when shot is from a weird angle
    total += getPlayerTakingShot().getTechnicalAttributes().getBallControl();
    total += getPlayerTakingShot().getTechnicalAttributes().getVolleys();

    Integer numberOfAttributesUsedForCalculation = 4;
    total = total / numberOfAttributesUsedForCalculation;

    for (int i = 0; i < getNumberOfTilesAwayFromGoal(); i++) {
      total = (Math.pow((total-Math.pow(total, 0.48)), 0.94));
//      total = (int)(Math.pow(total, 0.93));
    }
    if(getNumberOfTilesAwayFromGoal() >= 10)
      total += getPlayerTakingShot().getTechnicalAttributes().getLongShots()/5;

    return (int) total;
  }

  public Player getPlayerTakingShot() {
    return playerTakingShot;
  }

  public void setPlayerTakingShot(Player playerTakingShot) {
    this.playerTakingShot = playerTakingShot;
  }

  public Integer getNumberOfTilesAwayFromGoal() {
    return numberOfTilesAwayFromGoal;
  }

  public void setNumberOfTilesAwayFromGoal(Integer numberOfTilesAwayFromGoal) {
    this.numberOfTilesAwayFromGoal = numberOfTilesAwayFromGoal;
  }

  public ShotOutcome getShotOutcome() {
    return shotOutcome;
  }

  public void setShotOutcome(ShotOutcome shotOutcome) {
    this.shotOutcome = shotOutcome;
  }

  @Override
  public String buildMatchEventString() {
    StringBuilder message = new StringBuilder();
    if(this.shotOutcome == ShotOutcome.GOAL_SCORED){
      message.append(ColorUtils.BLUE_BACKGROUND);
    }
    message.append(getCurrentGameTime() + " ");
    message.append(getPlayerTakingShot().getName());
    message.append(" (" + getPlayerTakingShot().getCurrentClub().getTeamName() + ")");
    message.append(" shoots ");
    message.append(getShotOutcome().getShotOutcomeValue());
    if(this.shotOutcome != ShotOutcome.GOAL_SCORED)
      return message.toString();
    message.append(" (");
    message.append(MatchEngine.footballTeamToMatchStats.get(MatchEngine.footballTeamToMatchStats.keySet().toArray()[0]).getFootballTeam().getTeamName());
    message.append(" ");
    message.append("[" + MatchEngine.footballTeamToMatchStats.get(MatchEngine.footballTeamToMatchStats.keySet().toArray()[0]).getGoals() + "]");
    message.append(" - ");
    message.append("[" + MatchEngine.footballTeamToMatchStats.get(MatchEngine.footballTeamToMatchStats.keySet().toArray()[1]).getGoals() + "]");
    message.append(" ");
    message.append(MatchEngine.footballTeamToMatchStats.get(MatchEngine.footballTeamToMatchStats.keySet().toArray()[1]).getFootballTeam().getTeamName() + ")");
    message.append(ColorUtils.RESET);

    return message.toString();
  }

  public enum ShotOutcome {
    SHOT_MISSED("but misses the target."),
    SHOT_SAVED("but the keeper saves the shot."),
    GOAL_SCORED("and scores! GOAL!!!!");

    private String ShotOutcomeValue;

    ShotOutcome(String shotOutcomeValue) {
      this.ShotOutcomeValue = shotOutcomeValue;
    }

    public String getShotOutcomeValue() {
      return ShotOutcomeValue;
    }

    public void setShotOutcomeValue(String shotOutcomeValue) {
      ShotOutcomeValue = shotOutcomeValue;
    }
  }
}
