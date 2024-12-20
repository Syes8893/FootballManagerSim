package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamFormation;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetupBuilder;
import com.elliot.footballmanager.footballteam.matchsetup.MatchDaySquad;

import java.text.Normalizer;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class provides the list of options that are provided to the user prior to the start of a
 * Football Match. These options are usually presented when the currentDate in
 * <link>GameManager</link> is equal to an upcomingFixture.
 *
 * @author Elliot
 */
public class PlayerMenu implements GameMenu {

  private final GameManager gameManager;

  private final Player player;

  public PlayerMenu(GameManager gameManager, Player player) {
    this.gameManager = gameManager;
    this.player = player;
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
            //TODO - print player info & stats
            System.out.println("TODO");
            quit = true;
            break;
          case 2:
            System.out.println("You have successfully purchased " + player.getName() + "!");
            //remove from old team
            player.getCurrentClub().getSquad().remove(player);
            player.getCurrentClub().setMatchSetup(FootballTeamMatchSetupBuilder.buildNewMatchSetup(player.getCurrentClub(), null));
            for(MatchDaySquad matchDaySquad : gameManager.getCurrentFootballTeam().getMatchSetup().getAvailableFormations()){
              Player[] newReserves = new Player[matchDaySquad.getReserves().length+1];
              System.arraycopy(matchDaySquad.getReserves(), 0, newReserves, 0, matchDaySquad.getReserves().length);
              newReserves[newReserves.length-1] = player;
              matchDaySquad.setReserves(newReserves);
            }
//            MatchDaySquad matchDaySquad = gameManager.getCurrentFootballTeam().getMatchSetup().getSelectedFormation();
//            Player[] newReserves = new Player[matchDaySquad.getReserves().length+1];
//            System.arraycopy(matchDaySquad.getReserves(), 0, newReserves, 0, matchDaySquad.getReserves().length);
//            newReserves[newReserves.length-1] = player;
//            matchDaySquad.setReserves(newReserves);
            gameManager.getCurrentFootballTeam().getSquad().add(player);
            player.setCurrentClub(gameManager.getCurrentFootballTeam());
            quit = true;
            break;
          case 3:
            System.out.println("TODO");
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
    System.out.println("[1] View player info");
    System.out.println("[2] Purchase player");
    System.out.println("[3] Back to main menu");
  }

//  private void setFormation(){
//    System.out.println("[5] Submit search");
//    Scanner scanner = new Scanner(System.in);
//    int choice = Integer.parseInt(scanner.nextLine())-1;
//    if(choice == -1){
//      System.out.println("Cancelled action.");
//      return;
//    }
//    while(choice < 0 || choice >= FootballTeamFormation.values().length){
//      System.out.println("Please choose a value between 1 and " + FootballTeamFormation.values().length);
//      choice = Integer.parseInt(scanner.nextLine())-1;
//    }
//
//  }

  private GameManager getGameManager() {
    return gameManager;
  }
}
