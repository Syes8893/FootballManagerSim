package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.Standing;
import com.elliot.footballmanager.entity.dao.StandingDao;
import com.elliot.footballmanager.entity.dao.impl.StandingDaoImpl;
import com.elliot.footballmanager.match.MatchResult;
import com.elliot.footballmanager.standings.StandingBuilder;
import com.elliot.footballmanager.standings.StandingComparator;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Post Simulation menu. Post match analysis / Going back to the main menu.
 *
 * @author Elliot
 */
public class PostMatchMenu implements GameMenu {

  private Scanner scanner;
  private GameManager gameManager;
  private List<MatchResult> matchResults;

  private PostMatchMenu() {

  }

  public PostMatchMenu(GameManager gameManager, List<MatchResult> matchResults) {
    this.gameManager = gameManager;
    this.matchResults = matchResults;
  }

  @Override
  public void beginMenuSelectionLoop() {
    displayMenuOptions();
    scanner = new Scanner(System.in);
    boolean quit = false;
    do {
      try {
        switch (scanner.nextInt()) {
          case 0:
            this.getGameManager().saveGame();
            quit = true;
            break;
          case 1:
//            System.out.println("TODO - add match highlights (Goals etc)");
            getMatchResults().get(getMatchResults().size()-1).displayPostMatchInfo();
            displayMenuOptions();
            break;
          case 2:
            displayDailyFixtures();
            displayMenuOptions();
            break;
          case 3:
            getGameManager().displayLeagueTable();
            displayMenuOptions();
            break;
          case 4:
            GameMenu mainMenu = this.getGameManager().mainMenu;
            mainMenu.beginMenuSelectionLoop();
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

  //TODO: List additional information for player simulated match (passes etc..)
  private void displayDailyFixtures() {
    System.out.print("\n");
    System.out.println(ColorUtils.YELLOW_BOLD + "MATCH FIXTURES (" + new SimpleDateFormat("dd/MM/yyyy").format(getGameManager().getCurrentDate()) + ")" + ColorUtils.RESET);
    for (MatchResult matchResult : getMatchResults()) {
      matchResult.displayMatchResult();
    }
    System.out.print("\n");
  }

  public List<MatchResult> getMatchResults() {
    return matchResults;
  }

  public GameManager getGameManager() {
    return gameManager;
  }

  @Override
  public void displayMenuOptions() {
    System.out.println("[0] Save and Quit");
    System.out.println("[1] View Match highlights");
    System.out.println("[2] View today's fixtures");
    System.out.println("[3] View league table/standings");
    System.out.println("[4] Back to main menu");
  }
}
