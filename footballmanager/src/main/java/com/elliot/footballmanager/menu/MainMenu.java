package com.elliot.footballmanager.menu;

import java.util.*;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.GameManager;

/**
 * Once a user has loaded / created a save game the MainMenu class provides the list of options
 * available throughout the FootballManager simulator.
 *
 * @author Elliot
 */
public class MainMenu implements GameMenu {

//  private static Scanner scanner = new Scanner(System.in);
  private GameManager gameManager;
  private Scanner scanner;

  public MainMenu(GameManager gameManager) {
    this.gameManager = gameManager;
    this.gameManager.mainMenu = this;
    beginMenuSelectionLoop();
  }

  /**
   * The main loop used to play the game and display the different pieces of information required.
   */
  public void beginMenuSelectionLoop() {
    scanner = new Scanner(System.in);
    displayMenuOptions();

    boolean quit = false;
    do {
      try {
        switch (scanner.nextInt()) {
          case 0: //exit game
            this.getGameManager().saveGame();
            quit = true;
            break;
          case 1: //Progress / Simulate game
            this.getGameManager().simulateGame();
            quit = true;
            break;
          case 2: //View upcoming fixtures
            getUpcomingFixtures();
            displayMenuOptions();
            break;
          case 3:
            getGameManager().displayLeagueTable();
            displayMenuOptions();
            break;
          case 4:
            //TODO - display cup matchups
            getGameManager().displayCupMatchups();
            displayMenuOptions();
            break;
          case 5:
            TeamMenu teamMenu = new TeamMenu(getGameManager());
            teamMenu.beginMenuSelectionLoop();
            quit = true;
            break;
          case 6:
            TransferMarketMenu transferMarketMenu = new TransferMarketMenu(getGameManager());
            transferMarketMenu.beginMenuSelectionLoop();
//            System.out.println("TODO - Add Transfer Market");
            quit = true;
            break;
//          case 6:
//            //TODO - allow club selection (and allow country and league selection)
//
//            getGameManager().getManager().setCurrentFootballTeam(getGameManager().getCurrentLeague().getFootballTeams().get(new Random().nextInt(getGameManager().getCurrentLeague().getFootballTeams().size())));
//            getGameManager().setCurrentFootballTeam(getGameManager().getManager().getCurrentFootballTeam());
//            getGameManager().setUpcomingFixtures(new FixtureDaoImpl().getFootballTeamsUpcomingFixtures(getGameManager().getCurrentFootballTeam(), getGameManager().getCurrentDate()));
//
//            ManagerDao managerDao = new ManagerDaoImpl();
//            managerDao.insertIntoManagerTable(getGameManager().getManager());
//
//            System.out.println("\nYou are now managing: " + ColorUtils.GREEN_BOLD + getGameManager().getCurrentFootballTeam().getTeamName() + ColorUtils.RESET + "\n");
//            displayMenuOptions();
//
//            break;
          default:
            System.out.println("Invalid selection! Please try again.");
            displayMenuOptions();
            break;
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid selection! Please try again.");
        scanner.next();
      }
    } while (!quit);
//    scanner.close();
  }

  public void displayMenuOptions() {
    System.out.print("\n");
    System.out.println("[0] Save and Quit");
    System.out.println("[1] Simulate / Progress Game");
    System.out.println("[2] View Upcoming Fixtures");
    System.out.println("[3] View League Table/Standings");
    System.out.println("[4] View Cup Match-ups");
    System.out.println("[5] View Team Menu");
    System.out.println("[6] View Transfer Market");
//    System.out.println("[6] Manage another club");
  }

  private void displayQuickInfo() {
    System.out.println(this.getGameManager().getQuickGameInfo());
  }

  private GameManager getGameManager() {
    return gameManager;
  }

  private void getUpcomingFixtures() {
    int i = 0;
    System.out.print("\n");
    System.out.println(ColorUtils.BLUE_BOLD + getGameManager().getUpcomingFixtures().size() + " UPCOMING MATCHES (" + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(getGameManager().getCurrentDate()) + ")" + ColorUtils.RESET);
    Queue<Fixture> upcomingQueue = new PriorityQueue<>(getGameManager().getUpcomingFixtures());
    while(upcomingQueue.peek() != null){
      if(i == 7){
        System.out.println("...");
        break;
      }
      Fixture fixture = upcomingQueue.poll();
      System.out.println((fixture.isCup() ? ColorUtils.YELLOW_BRIGHT : "")
          + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(fixture.getDateOfFixture())
          + " " + "|" + " "
          + (fixture.getHomeTeam().getTeamName().equals(this.getGameManager().getCurrentFootballTeam().getTeamName())
                      ? fixture.getAwayTeam().getTeamName() + " (Home)"
                      : fixture.getHomeTeam().getTeamName() + " (Away)")
          + (fixture.isCup() ? " [Cup]" + ColorUtils.RESET : "")
      );
      i++;
    }

    System.out.print("\n");
  }

//  private void openMatchDayMenu() {
//    if (!this.getGameManager().isMatchDay()) {
//      System.out.println("There is no match to play! It is not currently a Match day. " +
//          "Please simulate the game to a Match day first.");
//      displayMenuOptions();
//      return;
//    }
//
//    GameMenu matchDayMenu = new MatchDayMenu(gameManager);
//    matchDayMenu.beginMenuSelectionLoop();
//  }
}
