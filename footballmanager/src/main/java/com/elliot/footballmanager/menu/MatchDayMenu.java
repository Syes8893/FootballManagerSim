package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.dao.FixtureDao;
import com.elliot.footballmanager.entity.dao.impl.FixtureDaoImpl;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.match.MatchResult;
import com.elliot.footballmanager.match.engine.MatchEngine;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class provides the list of options that are provided to the user prior to the start of a
 * Football Match. These options are usually presented when the currentDate in
 * <link>GameManager</link> is equal to an upcomingFixture.
 *
 * @author Elliot
 */
public class MatchDayMenu implements GameMenu {

  private Scanner scanner;
  private GameManager gameManager;

  public MatchDayMenu() {

  }

  public MatchDayMenu(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  public void beginMenuSelectionLoop() {
    displayMatchDayMenuScreen();
    displayMenuOptions();
    scanner = new Scanner(System.in);
    boolean quit = false;
    do {
      try {
        switch (scanner.nextInt()) {
          case 0: //exit game
            this.getGameManager().saveGame();
            quit = true;
            break;
          case 1: //simulate game
            List<MatchResult> matchResults = simulateCurrentDatesFixtures(true);
            displayPostMatchMenu(matchResults);
            quit = true;
            break;
          case 2: //quicksim game
            List<MatchResult> quicksimResults = simulateCurrentDatesFixtures(false);
            displayPostMatchMenu(quicksimResults);
            quit = true;
            break;
          case 3:
            System.out.println("TODO - Add squad options menu");
            break;
          case 4:
            getGameManager().mainMenu.beginMenuSelectionLoop();
            quit = true;
            break;
          default:
            System.out.println("Invalid selection! Please try again.");
            break;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid selection! Please try again.");
        scanner.next();
      }
    } while (!quit);
//    scanner.close();
  }

  private void displayMatchDayMenuScreen() {
    Fixture fixture = this.getGameManager().getUpcomingFixtures().peek();
    System.out.println("\n" + ColorUtils.BLUE_BOLD + "MATCHDAY" + ColorUtils.RESET);
    System.out.println(DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(fixture.getDateOfFixture())
        + " " + "|" + " "
        + fixture.getHomeTeam().getTeamName()
        + " " + "VS" + " "
        + fixture.getAwayTeam().getTeamName()
            + (!fixture.isCup() ? " (" + gameManager.getCurrentLeague().getLeagueName() + ")" : " (Cup)"));
  }

  //TODO
  private void displaySquadOptions() {
    System.out.println(
        gameManager.getCurrentFootballTeam().getMatchSetup().getSelectedFormation().toString());
  }

  public void displayMenuOptions() {
    System.out.println("[1] Start Match");
    System.out.println("[2] Quick-Sim Match");
    System.out.println("[3] View / Edit team formation");
    System.out.println("[4] Back to main menu");
  }

  private List<MatchResult> simulateCurrentDatesFixtures(boolean logging) {
    List<MatchResult> matchResults;

    MatchEngine.setIsLoggingGameEvents(logging);
    Fixture currentPlayersFixture = gameManager.getUpcomingFixtures().peek();

    MatchResult playersMatchResult = MatchEngine.beginFootballMatchSimulator(currentPlayersFixture, getGameManager().getStandingList());
    matchResults = simulateRemainingFixtures(currentPlayersFixture);
    gameManager.getUpcomingFixtures().remove();
    getGameManager().getCurrentDate().setHours(12);

    matchResults.add(playersMatchResult);

    for(MatchResult matchResult : matchResults){
      gameManager.getMatchResults().put(matchResult.getFixture().getFixtureId(), matchResult);
    }

    playersMatchResult.displayPostMatchInfo();

    if(!matchResults.get(0).getFixture().isCup())
      return matchResults;

    if(matchResults.size() == 1){
      System.out.println(matchResults.get(0).getWinner().getTeamName() + " wins the cup!");
      return matchResults;
    }
    //TODO - return whether player is champion

    int i = 0;
    Date fixtureDate = getGameManager().getCurrentDate();
    fixtureDate.setHours(0);
    while(i < matchResults.size()){ //TODO check why we dont check for -1, as we get results +1
      Fixture fixture = new Fixture(matchResults.get(i).getWinner(), matchResults.get(i+1).getWinner()
              , DateUtils.addDays(fixtureDate, 28), matchResults.get(i).getWinner().getLeagueId()
              , Math.max(matchResults.get(i).getFixture().getCupLevel()-1, matchResults.get(i+1).getFixture().getCupLevel())-1);
      if(fixture.getHomeTeam().getFootballTeamId() == gameManager.getCurrentFootballTeam().getFootballTeamId()
              || fixture.getAwayTeam().getFootballTeamId() == gameManager.getCurrentFootballTeam().getFootballTeamId()){
        gameManager.addUpcomingFixtures(fixture);
      }
      gameManager.addCupFixtures(fixture);
      gameManager.addSeasonFixtures(fixture);
      i+=2;
    }
    return matchResults;
  }

  private List<MatchResult> simulateRemainingFixtures(Fixture currentPlayersFixture) {
    if(gameManager.getSeasonFixtures().peek() == null)
      return new ArrayList<>();

    List<MatchResult> matchResults = new ArrayList<>();
    MatchEngine.setIsLoggingGameEvents(false);

    while(gameManager.getSeasonFixtures().peek() != null && gameManager.getSeasonFixtures().peek().getDateOfFixture().equals(gameManager.getCurrentDate())){
      if(gameManager.getSeasonFixtures().peek().equals(currentPlayersFixture)){
        gameManager.getSeasonFixtures().poll();
        continue;
      }
      MatchResult matchResult = MatchEngine.beginFootballMatchSimulator(gameManager.getSeasonFixtures().poll(), getGameManager().getStandingList());
      matchResults.add(matchResult);
    }
    return matchResults;
  }

  private void displayPostMatchMenu(List<MatchResult> matchResults) {
    PostMatchMenu postMatchMenu = new PostMatchMenu(gameManager, matchResults);
    postMatchMenu.beginMenuSelectionLoop();
  }

  private GameManager getGameManager() {
    return gameManager;
  }
}
