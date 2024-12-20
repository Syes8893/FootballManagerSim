package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.entity.dao.impl.FootballTeamMatchSetupDaoImpl;
import com.elliot.footballmanager.entity.dao.impl.PlayerDaoImpl;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamFormation;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetupBuilder;
import com.elliot.footballmanager.footballteam.matchsetup.MatchDaySquad;
import com.elliot.footballmanager.match.model.Position;

import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class provides the list of options that are provided to the user prior to the start of a
 * Football Match. These options are usually presented when the currentDate in
 * <link>GameManager</link> is equal to an upcomingFixture.
 *
 * @author Elliot
 */
public class TransferMarketMenu implements GameMenu {

  private final GameManager gameManager;

  private String position;
  private String Country, League, Club;
  private int minRating = -1, maxRating = -1;
  private int minAge = -1, maxAge = -1;
  private int minValue = -1, maxValue = -1;

  public TransferMarketMenu(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  public void beginMenuSelectionLoop() {
    displayTitle();
    displayMenuOptions();

    Scanner scanner = new Scanner(System.in);
    boolean quit = false;
    do {
      try {
        switch (scanner.nextInt()) {
          case 0: //exit game
            this.getGameManager().saveGame();
            quit = true;
            break;
          case 1:
            System.out.println("Please enter player name to search for:");
            String searchName = new Scanner(System.in).nextLine();
            int index = 1;
//            for(FootballTeam footballTeam : gameManager.getCurrentLeague().getFootballTeams()){
            ArrayList<Player> players = new ArrayList<>();
            for(FootballTeam footballTeam : gameManager.getAllTeams()){
              for(Player player : footballTeam.getSquad()){
                if(Normalizer.normalize(player.getFullName().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").contains(searchName.toLowerCase().trim())){
                  System.out.println("[" + index + "] (" + footballTeam.getTeamName() + ") " + player.getNameAndOverallAndPosition());
                  players.add(player);
                  index++;
                }
              }
            }

            //TODO - open player purchasing menu
            if(index == 1){
              System.out.println("Could not find a player by the name of \"" + searchName + "\".");
              displayMenuOptions();
              break;
            }

            int choice = Integer.parseInt(new Scanner(System.in).nextLine())-1;
            if(choice == -1){
              System.out.println("Cancelled action.");
              displayMenuOptions();
              break;
            }
            while(choice < 0 || choice >= FootballTeamFormation.values().length){
              System.out.println("Please choose a value between 1 and " + FootballTeamFormation.values().length);
              choice = Integer.parseInt(new Scanner(System.in).nextLine())-1;
            }
            new PlayerMenu(gameManager, players.get(choice)).beginMenuSelectionLoop();
            displayMenuOptions();
            break;
          case 2:
            //TODO - submit search
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 3:
            //TODO - set position
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 4:
            //TODO - set country/league/club
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 5:
            //TODO - set rating
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 6:
            //TODO - set age
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 7:
            //TODO - set value
            System.out.println("TODO");
            displayMenuOptions();
            break;
          case 8:
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

  private void displayTitle() {
    System.out.println("\n" + ColorUtils.RED_BOLD_BRIGHT + "TRANSFER MARKET" + ColorUtils.RESET);
  }

  public void displayMenuOptions() {
    //TODO
    System.out.print("\n");
    System.out.println("[0] Save and Quit");
    System.out.println("[1] Search by name");
    System.out.println("[2] Submit search");
    System.out.println("[3] Set position (Current: null)");
    System.out.println("[4] Set country/league/club (Current: null/null/null)");
    System.out.println("[5] Set min & max rating (Current: -1/-1)");
    System.out.println("[6] Set min & max age (Current: -1/-1)");
    System.out.println("[7] Set min & max value (Current: -1/-1)");
    System.out.println("[8] Back to main menu");
  }

  private void setFormation(){
    System.out.println("[5] Submit search");
    Scanner scanner = new Scanner(System.in);
    int choice = Integer.parseInt(scanner.nextLine())-1;
    if(choice == -1){
      System.out.println("Cancelled action.");
      return;
    }
    while(choice < 0 || choice >= FootballTeamFormation.values().length){
      System.out.println("Please choose a value between 1 and " + FootballTeamFormation.values().length);
      choice = Integer.parseInt(scanner.nextLine())-1;
    }

  }

  private GameManager getGameManager() {
    return gameManager;
  }
}
