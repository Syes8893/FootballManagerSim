package com.elliot.footballmanager.match;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * The MatchResult object is created after a FootballMatch has been simulated within the
 * MatchEngine.
 *
 * @author Elliot
 */
public class MatchResult {

  private final Fixture fixture;

  private final FootballTeamMatchStats homeTeamMatchStats;
  private final FootballTeamMatchStats awayTeamMatchStats;

  private final Result result;

  public MatchResult(Fixture fixture, FootballTeamMatchStats homeTeamMatchStats,
      FootballTeamMatchStats awayTeamMatchStats) {
    this.fixture = fixture;
    this.homeTeamMatchStats = homeTeamMatchStats;
    this.awayTeamMatchStats = awayTeamMatchStats;

    result = determineResultOfMatch();
  }

  public Result determineResultOfMatch() {
    return Result.determineResultOfMatch(this);
  }

  public FootballTeam getWinner(){
    if(result.equals(Result.H))
      return fixture.getHomeTeam();
    else if(result.equals(Result.A))
      return fixture.getAwayTeam();
    else
      return null;
  }

  public Fixture getFixture() {
    return fixture;
  }

  public FootballTeamMatchStats getHomeTeamMatchStats() {
    return homeTeamMatchStats;
  }

  public FootballTeamMatchStats getAwayTeamMatchStats() {
    return awayTeamMatchStats;
  }

  public Result getResult() {
    return result;
  }

  public void displayMatchResult() {
    String stringBuilder = "FT: "
            + (getFixture().getHomeTeam().equals(getWinner()) ? ColorUtils.GREEN_BRIGHT : "")
            + homeTeamMatchStats.getFootballTeam().getTeamName()
            + " "
            + (fixture.isCup() && (homeTeamMatchStats.getPenaltyShootout() > 0 || awayTeamMatchStats.getPenaltyShootout() > 0) ? "(" + homeTeamMatchStats.getPenaltyShootout() + ") " : "")
            + "[" + homeTeamMatchStats.getGoals() + "]"
            + (getFixture().getHomeTeam().equals(getWinner()) ? ColorUtils.RESET : "")
            + " - "
            + (getFixture().getAwayTeam().equals(getWinner()) ? ColorUtils.GREEN_BRIGHT : "")
            + "[" + awayTeamMatchStats.getGoals() + "]"
            + (fixture.isCup() && (homeTeamMatchStats.getPenaltyShootout() > 0 || awayTeamMatchStats.getPenaltyShootout() > 0) ? " (" + awayTeamMatchStats.getPenaltyShootout() + ")" : "")
            + " "
            + awayTeamMatchStats.getFootballTeam().getTeamName()
            + (getFixture().getAwayTeam().equals(getWinner()) ? ColorUtils.RESET : "");
    System.out.println(stringBuilder);
  }

  public void displayPostMatchInfo() {
    FootballTeam homeTeam = homeTeamMatchStats.getFootballTeam();
    FootballTeam awayTeam = awayTeamMatchStats.getFootballTeam();
    String homeName = homeTeam.getTeamName() + new String(new char[Math.max(5-homeTeam.getTeamName().length(), 0)]).replace("\0", " "); //+ new String(new char[Math.max(0, awayTeam.getTeamName().length()-homeTeam.getTeamName().length())]).replace("\0", " ");
    String awayName = awayTeam.getTeamName() + new String(new char[Math.max(5-awayTeam.getTeamName().length(), 0)]).replace("\0", " "); //+ new String(new char[Math.max(0, homeTeam.getTeamName().length()-awayTeam.getTeamName().length())]).replace("\0", " ");
    int totalPossessionTicks = getHomeTeamMatchStats().getPosessionTicks() + getAwayTeamMatchStats().getPosessionTicks();
    double possessionHome = (homeTeamMatchStats.getPosessionTicks()/(double)totalPossessionTicks)*100.0;
    double possessionAway = 100-possessionHome;
    DecimalFormat df = new DecimalFormat("#.#");
    df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    String posHomeString = df.format(possessionHome) + "%";
    String posAwayString = df.format(possessionAway) + "%";

    String result = ColorUtils.PURPLE_BOLD
            + "\nMATCH RESULT\n"
            + ColorUtils.RESET
            + homeName + " | " + awayName
            + " "
            + "(" + homeTeamMatchStats.getGoals() + " - " + awayTeamMatchStats.getGoals() + ")"
            + (fixture.isCup() && (homeTeamMatchStats.getPenaltyShootout() > 0 || awayTeamMatchStats.getPenaltyShootout() > 0) ? " [Shootout " + homeTeamMatchStats.getPenaltyShootout() + " - " + awayTeamMatchStats.getPenaltyShootout() + "]"
            : "")
            + "\n"
            + homeTeamMatchStats.getShots() + new String(new char[Math.max(5-(homeTeamMatchStats.getShots()).toString().length(), (homeName.length()-homeTeamMatchStats.getShots().toString().length()))]).replace("\0", " ")
            + " | "
            + awayTeamMatchStats.getShots() + new String(new char[Math.max(5-(awayTeamMatchStats.getShots()).toString().length(), (awayName.length()-awayTeamMatchStats.getShots().toString().length()))]).replace("\0", " ")
            + " "
            + "(Shots)"
            + "\n"
            + homeTeamMatchStats.getShotsOnTarget() + new String(new char[Math.max(5-(homeTeamMatchStats.getShotsOnTarget()).toString().length(), (homeName.length()-homeTeamMatchStats.getShotsOnTarget().toString().length()))]).replace("\0", " ")
            + " | "
            + awayTeamMatchStats.getShotsOnTarget() + new String(new char[Math.max(5-(awayTeamMatchStats.getShotsOnTarget()).toString().length(), (awayName.length()-awayTeamMatchStats.getShotsOnTarget().toString().length()))]).replace("\0", " ")
            + " "
            + "(Shots on target)"
            + "\n"
            + homeTeamMatchStats.getPasses() + new String(new char[Math.max(5-(homeTeamMatchStats.getPasses()).toString().length(), (homeName.length()-homeTeamMatchStats.getPasses().toString().length()))]).replace("\0", " ")
            + " | "
            + awayTeamMatchStats.getPasses() + new String(new char[Math.max(5-(awayTeamMatchStats.getPasses()).toString().length(), (awayName.length()-awayTeamMatchStats.getPasses().toString().length()))]).replace("\0", " ")
            + " "
            + "(Passes)"
            + "\n"
            + posHomeString + new String(new char[Math.max(0, (homeName.length()-posHomeString.length()))]).replace("\0", " ")
            + " | "
            + posAwayString + new String(new char[Math.max(0, (awayName.length()-posAwayString.length()))]).replace("\0", " ")
            + " "
            + "(Possession)"
            + "\n"
            ;
    System.out.println(result);
  }
}
