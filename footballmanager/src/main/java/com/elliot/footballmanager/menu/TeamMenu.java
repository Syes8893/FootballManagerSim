package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.entity.dao.impl.FootballTeamMatchSetupDaoImpl;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamFormation;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetupBuilder;
import com.elliot.footballmanager.footballteam.matchsetup.MatchDaySquad;
import com.elliot.footballmanager.match.model.Position;

import java.util.*;

/**
 * This class provides the list of options that are provided to the user prior to the start of a
 * Football Match. These options are usually presented when the currentDate in
 * <link>GameManager</link> is equal to an upcomingFixture.
 *
 * @author Elliot
 */
public class TeamMenu implements GameMenu {

  private final GameManager gameManager;

  public TeamMenu(GameManager gameManager) {
    this.gameManager = gameManager;
  }

  public void beginMenuSelectionLoop() {
    displayMatchDayMenuScreen();
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
            getGameManager().getCurrentFootballTeam().printFootballTeamInfo();
            displayMenuOptions();
            break;
          case 2:
            setFormation();
            displayMenuOptions();
            break;
          case 3:
            //TODO - allow user to swap players with other players in the field or with benched players
            editFormation();
            displayMenuOptions();
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
    System.out.println("\n" + ColorUtils.CYAN_BRIGHT + "TEAM MENU" + ColorUtils.RESET);
  }

  public void displayMenuOptions() {
    //TODO
    System.out.print("\n");
    System.out.println("[0] Save and Quit");
//    System.out.println("[1] See Team stats");
    System.out.println("[1] View Team");
    System.out.println("[2] Edit Formation");
    System.out.println("[3] Edit Lineup (Starting XI and Bench)");
    System.out.println("[4] Back to main menu");
  }

  private void setFormation(){
    int i = 1;
    for(FootballTeamFormation footballTeamFormation : FootballTeamFormation.values()){
      System.out.println("[" + i + "] " + footballTeamFormation.getFormationName());
      i++;
    }
    Scanner formation = new Scanner(System.in);
    int choice = Integer.parseInt(formation.nextLine())-1;
    if(choice == -1){
      System.out.println("Cancelled action.");
      return;
    }
    while(choice < 0 || choice >= FootballTeamFormation.values().length){
      System.out.println("Please choose a value between 1 and " + FootballTeamFormation.values().length);
      choice = Integer.parseInt(formation.nextLine())-1;
    }
    FootballTeamFormation chosenFormation = FootballTeamFormation.values()[choice];
    FootballTeam footballTeam = getGameManager().getCurrentFootballTeam();
    for(MatchDaySquad matchDaySquad : footballTeam.getMatchSetup().getAvailableFormations()){
      if(matchDaySquad.getFormation().toString().equals(chosenFormation.toString())){
        footballTeam.getMatchSetup().setSelectedFormation(matchDaySquad);
        break;
      }
    }
    if(!footballTeam.getMatchSetup().getSelectedFormation().getFormation().toString().equals(chosenFormation.toString()))
      footballTeam.setMatchSetup(FootballTeamMatchSetupBuilder.buildNewMatchSetup(footballTeam, chosenFormation));
    System.out.println("The formation has been set to: " + ColorUtils.GREEN_BOLD + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getFormation().getFormationName() + ColorUtils.RESET);
    new FootballTeamMatchSetupDaoImpl().updateFootballTeamMatchSetup(footballTeam);
  }

  private void editFormation(){
    int i = 0;
    int q = 0;
    MatchDaySquad matchDaySquad = getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation();
    System.out.print("Starting 11: \n");
    while(i < getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getStartingLineup().length){
      System.out.println("[" + (i+1) + "] " + matchDaySquad.getStartingLineup()[i].getNameAndOverall()
              + " " + matchDaySquad.getStartingLineup()[i].getPreferredPositions());
      i++;
    }
    System.out.print("\nBench:\n");
    while(q < getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions().length){
      System.out.println("[" + (i+1) + "] " + matchDaySquad.getSubstitutions()[q].getNameAndOverall()
              + " " + matchDaySquad.getSubstitutions()[q].getPreferredPositions());
      i++;
      q++;
    }
    System.out.println("Select player in squad to be replaced: ");
    Scanner formation = new Scanner(System.in);
    int choice = Integer.parseInt(formation.nextLine())-1;
    if(choice == -1){
      System.out.println("Cancelled replacement.");
      return;
    }
    while(choice < 0 || choice > i){
      System.out.println("Please choose a value between 1 and " + (i+1));
      choice = Integer.parseInt(formation.nextLine())-1;
    }

    Player tobeReplaced = (choice < 11) ? matchDaySquad.getStartingLineup()[choice] : matchDaySquad.getSubstitutions()[choice-11];

    ArrayList<Player> allPlayers = new ArrayList<>();
    System.out.print("\nBench:\n");
    for(int k = 0; k < getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions().length; k++) {
      HashSet<Position> intersect = new HashSet<>(tobeReplaced.getPreferredPositions());
      intersect.retainAll(getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions()[k].getPreferredPositions());
      System.out.println("[" + (k + 1) + "] " + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions()[k].getNameAndOverall()
              + " " + (!intersect.isEmpty() ? ColorUtils.GREEN_BOLD : "") + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions()[k].getPreferredPositions().toString()
              + (!intersect.isEmpty() ? ColorUtils.RESET : ""));
      allPlayers.add(getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions()[k]);
    }
    System.out.print("\nReserves:\n");
    for(int k = 0; k < getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getReserves().length; k++) {
      HashSet<Position> intersect = new HashSet<>(tobeReplaced.getPreferredPositions());
      intersect.retainAll(getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getReserves()[k].getPreferredPositions());
      System.out.println("[" + (k + 1 + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getSubstitutions().length) + "] "
              + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getReserves()[k].getNameAndOverall()
              + " " + (!intersect.isEmpty() ? ColorUtils.GREEN_BOLD : "") + getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getReserves()[k].getPreferredPositions().toString()
              + (!intersect.isEmpty() ? ColorUtils.RESET : ""));
      allPlayers.add(getGameManager().getCurrentFootballTeam().getMatchSetup().getSelectedFormation().getReserves()[k]);
    }

    System.out.println("Select player to replace " + tobeReplaced.getName() + ": ");
    int choice2 = Integer.parseInt(formation.nextLine())-1;
    if(choice2 == -1){
      System.out.println("Cancelled replacement.");
      return;
    }
    while(choice2 < 0 || choice2 > allPlayers.size()){
      System.out.println("Please choose a value between 1 and " + (allPlayers.size()+1));
      choice2 = Integer.parseInt(formation.nextLine())-1;
    }

    Player replacementPlayer = allPlayers.get(choice2);

    if(choice < 11){
      matchDaySquad.getStartingLineup()[choice] = replacementPlayer;
      if(choice2 < 7)
        matchDaySquad.getSubstitutions()[choice2] = tobeReplaced;
      else
        matchDaySquad.getReserves()[choice2-7] = tobeReplaced;
    }
    else{
      matchDaySquad.getSubstitutions()[choice-11] = replacementPlayer;
      if(choice2 < 7)
        matchDaySquad.getSubstitutions()[choice2] = tobeReplaced;
      else
        matchDaySquad.getReserves()[choice2-7] = tobeReplaced;
    }

    System.out.println(tobeReplaced.getName() + " has been replaced by " + replacementPlayer.getName() + "!");
    new FootballTeamMatchSetupDaoImpl().updateFootballTeamMatchSetup(getGameManager().getCurrentFootballTeam());
    getGameManager().getCurrentFootballTeam().printFootballTeamInfo();
  }

  private GameManager getGameManager() {
    return gameManager;
  }
}
