package com.elliot.footballmanager.match.model;

import com.elliot.footballmanager.match.RandomNumberGenerator;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.match.engine.MatchEngine;

/**
 * Provides a way of challenging the Player in possession for the ball.
 *
 * @author Elliot
 */
public class Tackle extends MatchEvent {

  private Player playerAttemptingChallenge;
  private Player playerBeingChallenged;
  private Football football;

  private TacklingMethod tacklingMethod;

  private int tackleOutcome;

  public Tackle() {

  }

  public Tackle(Player playerAttemptingChallenge, Football football) {
    this.playerAttemptingChallenge = playerAttemptingChallenge;
    this.playerBeingChallenged = football.getPlayerInPossession();
    this.football = football;
  }

  //Return 1 is success, 0 if fail
  public void attemptTackleOnPlayerInPossession() {
    double challengerTacklingRating = determineTacklingStrategy();
    double challengedStrengthRating = (playerBeingChallenged.getPhysicalAttributes().getStrength() + playerBeingChallenged.getPhysicalAttributes().getBalance())/2;

    double chanceOfSuccessfulTackle = (challengerTacklingRating / challengedStrengthRating) * 0.5;

    if (RandomNumberGenerator.getRandomNumberBetweenZeroAndOne() <= chanceOfSuccessfulTackle) {
      playerBeingChallenged.setGameTicksUntilRecoveredFromTackle(tacklingMethod.getGameTickRecoveryTime());
      football.setPlayerInPossession(playerAttemptingChallenge);
      tackleOutcome = 1;
    }
    else{
      tackleOutcome = 0;
      //TODO - give yellow card or red card depending on how harsh the tackle was
      //idea: yellow card for standings tackles and sliding tackles that fail
      //and red card if user already has yellow OR if sliding tackle and chance of success lower than some value x
    }

    doesEventNeedToBeLogged();
  }

  private double determineTacklingStrategy() {
    if (RandomNumberGenerator.getRandomNumberBetweenZeroAndOneHundred() < 65) {
      tacklingMethod = TacklingMethod.STANDING_TACKLE;
      return (playerAttemptingChallenge.getTechnicalAttributes().getStandingTackle() + playerAttemptingChallenge.getPhysicalAttributes().getStrength() * 0.5)/1.5;
    } else {
      tacklingMethod = TacklingMethod.SLIDING_TACKLE;
      return (playerAttemptingChallenge.getTechnicalAttributes().getSlidingTackle() + playerAttemptingChallenge.getPhysicalAttributes().getStrength() * 0.5 )/ 1.5;
    }
  }

  public Player getPlayerAttemptingChallenge() {
    return playerAttemptingChallenge;
  }

  public Player getPlayerBeingChallenged() {
    return playerBeingChallenged;
  }

  @Override
  protected String buildMatchEventString() {
    StringBuilder message = new StringBuilder();
    message.append(getCurrentGameTime() + " ");
    message.append(getPlayerAttemptingChallenge().getName());
    message.append(" (" + getPlayerAttemptingChallenge().getCurrentClub().getTeamName() + ")");
    message.append(tacklingMethod.getTackleMessage());
    message.append(getPlayerBeingChallenged().getName());
    message.append(" (" + getPlayerBeingChallenged().getCurrentClub().getTeamName() + ")");

    return message.toString();
  }

  /**
   * The different ways a player can attempt to tackle the player in Possession with.
   *
   * @author Elliot
   */
  private enum TacklingMethod {
    SLIDING_TACKLE(3, " slides in on "),
    STANDING_TACKLE(1, " tackles ");

    private Integer gameTickRecoveryTime;
    private String tackleMessage;

    TacklingMethod(Integer gameTickRecoveryTime, String tackleMessage) {
      this.gameTickRecoveryTime = gameTickRecoveryTime;
      this.tackleMessage = tackleMessage;
    }

    public Integer getGameTickRecoveryTime() {
      return gameTickRecoveryTime;
    }

    public void setGameTickRecoveryTime(Integer gameTickRecoveryTime) {
      this.gameTickRecoveryTime = gameTickRecoveryTime;
    }

    public String getTackleMessage() {
      return tackleMessage;
    }
  }

  public int getTackleOutcome() {
    return tackleOutcome;
  }
}
