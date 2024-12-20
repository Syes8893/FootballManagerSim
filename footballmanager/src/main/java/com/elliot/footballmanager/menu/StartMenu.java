package com.elliot.footballmanager.menu;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.*;
import com.elliot.footballmanager.entity.dao.ManagerDao;
import com.elliot.footballmanager.entity.dao.impl.*;
import com.elliot.footballmanager.season.NewSeasonBuilder;

import java.util.*;

/**
 * The MainMenu class is used to create a new Menu object that displays all the options that area
 * available to the user.
 *
 * @author Elliot
 */
public class StartMenu implements GameMenu {

  private final GameManager gameManager = new GameManager();
  private final Scanner scanner = new Scanner(System.in);
  public static Date newGameStartDate = new GregorianCalendar(2022, Calendar.AUGUST, 1).getTime(); // (01/08/2023)


  public StartMenu() {
    displayWelcomeMessage();
    beginMenuSelectionLoop();
  }

  public void beginMenuSelectionLoop() {
    displayMenuOptions();

    boolean quit = false;
    while (!quit) {
      try {
        switch (scanner.nextInt()) {
          case 0: // [0] Exit Game
            System.out.println("Thanks for playing!");
            quit = true;
            break;
          case 1: // [1] Start New Game
            //TODO - for now remove all previous save data, dont remove other data when implementing multiple saves
            clearPreviousSaveData();
            createNewGame();
            quit = true;
            break;
          case 2: // [2] Continue Saved Game
            if(!gameManager.loadSavedGame())
              beginMenuSelectionLoop();
            quit = true;
            break;
          default:
            System.out.println("Invalid option, please try again.");
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid selection, please try again.");
        scanner.nextLine();
      }
    }
    scanner.close();
  }

  public void displayWelcomeMessage(){
    System.out.println("------------------------------------------------------------");
    System.out.println(ColorUtils.BLUE_BOLD + "Football Manager (Based on Elliots Java Football Manager)" + ColorUtils.RESET);
    System.out.println("------------------------------------------------------------");
  }

  public void displayMenuOptions() {
    System.out.println("Please choose one of the following options (by typing your choice and then pressing enter):");
    System.out.println("[0] Exit Game! (Please note you can hit 0 at any point to exit the game!)");
    System.out.println("[1] Start New Game!");
    System.out.println("[2] Continue Saved Game!");
  }

  /**
   * Remove any artifacts from the database that reference the save game that is being deleted.
   */
  private void clearPreviousSaveData() {
    SqliteDatabaseConnector.deleteSavedGameArtifacts(false);
  }

  /**
   * Calls the required methods in order to successfully instantiate a new FootballManager game. The
   * new details are persisted into the database via the {@link GameManager} class.
   */
  private void createNewGame() {
    chooseCountry();

    gameManager.setCurrentDate(newGameStartDate);
    NewSeasonBuilder.setupNewSeason(gameManager);

//    gameManager.saveGame();
    gameManager.mainMenu = new MainMenu(gameManager);
  }

  /**
   * Gets a Map of all available Countries from the database and the {@link Country} selected
   * by the user is added to the {@link GameManager} object
   */
  private void chooseCountry() {
    System.out.println("Please select the country that you would like to play in:");

    if(gameManager.getAllCountries().isEmpty()) {
      gameManager.setAllCountries(new CountryDaoImpl().getAllCountries());
      gameManager.getAllCountries().sort(Comparator.comparing(Country::getCountryName));
    }

    //Print all countries with index+1
    for(Country country : gameManager.getAllCountries()){
      System.out.println("[" + (gameManager.getAllCountries().indexOf(country)+1) + "] " + country.getCountryName());
    }

    boolean endLoop = false;

    while (!endLoop){
      int choice = scanner.nextInt();
      choice = Math.max(0, choice);

      if(choice == 0)
        endLoop = true;
      else if (gameManager.getAllCountries().get(choice-1) != null) {
        Country country = gameManager.getAllCountries().get(choice-1);
        gameManager.setCurrentCountry(country);
        System.out.println("You have selected: " + ColorUtils.GREEN + country.getCountryName() + ColorUtils.RESET + "\n");
        chooseLeague();
        endLoop = true;
      } else
        System.out.println("Invalid option, please try again.");
    }
  }

  /**
   * Gets a Map of all available Leagues for the selected {@link Country}. The selected
   * {@link League} is added to the {@link GameManager} object.
   */
  private void chooseLeague() {
    System.out.println("Please select the league that you would like to play in (or choose 0 to return to country selection):");

    gameManager.setAllLeagues(new LeagueDaoImpl().getAllLeaguesById(gameManager.getCurrentCountry().getCountryId(), false));
    gameManager.getAllLeagues().sort(Comparator.comparing(League::getLeagueName));


    for(League league : gameManager.getAllLeagues()){
      System.out.println("[" + (gameManager.getAllLeagues().indexOf(league)+1) + "] " + league.getLeagueName());
    }

    boolean endLoop = false;
    while (!endLoop){
      int choice = scanner.nextInt();
      choice = Math.max(0, choice);
      if (choice == 0) {
        chooseCountry();
        endLoop = true;
      } else if (gameManager.getAllLeagues().get(choice-1) != null) {
        League league = gameManager.getAllLeagues().get(choice-1);
        gameManager.setCurrentLeague(league);
        System.out.println("You have selected: " + ColorUtils.GREEN + league.getLeagueName() + ColorUtils.RESET + "\n");
        chooseTeam();
        endLoop = true;
      } else {
        System.out.println("Invalid option, please try again.");
      }
    }
  }


  /**
   * Gets a Map of all available FootballTeams for the selected {@link Country}. The selected
   * {@link FootballTeam} is added to the {@link GameManager} object.
   */
  private void chooseTeam() {
    System.out.println("Loading teams, please wait.");

    //TODO - revise this when doing international games, for now only load teams for selected league
    if(gameManager.getAllTeams().isEmpty())
      gameManager.setAllTeams(new FootballTeamDaoImpl().getAllFootballTeams());

    gameManager.getCurrentLeague().setFootballTeams(new ArrayList<>(gameManager.getAllTeams().stream().filter(x -> x.getLeagueId() == gameManager.getCurrentLeague().getLeagueId()).toList()));
    gameManager.getCurrentLeague().getFootballTeams().sort(Comparator.comparing(FootballTeam::getTeamName));

    new Thread(){
      @Override
      public void run() {
        super.run();
        new StandingDaoImpl().createNewStandingForFootballTeams(gameManager.getCurrentLeague().getFootballTeams());
      }
    }.start();

    System.out.println("Please select the FootballTeam that you would like to play as (or choose 0 to return to league selection):");

    for(FootballTeam footballTeam : gameManager.getCurrentLeague().getFootballTeams()){
      System.out.println("[" + (gameManager.getCurrentLeague().getFootballTeams().indexOf(footballTeam)+1) + "] " + footballTeam.getTeamName());
    }

    boolean endLoop = false;
    while(!endLoop) {
      int choice = scanner.nextInt();
      choice = Math.max(0, choice);
      if (choice == 0) {
        chooseLeague();
        endLoop = true;
      } else if (gameManager.getCurrentLeague().getFootballTeams().get(choice-1) != null) {
        FootballTeam footballTeam = gameManager.getCurrentLeague().getFootballTeams().get(choice-1);
        gameManager.setCurrentFootballTeam(footballTeam);
        System.out.println("You have selected: " + ColorUtils.GREEN + footballTeam.getTeamName() + ColorUtils.RESET + "\n");
        createNewManager();
        endLoop = true;
      } else {
        System.out.println("Invalid option, please try again.");
      }
    }
  }

  /**
   * Given information from the user a new {@link Math} object is created. This is persisted
   * into the database and added to the {@link GameManager} object.
   */
  private void createNewManager() {
/*    System.out.println("Please enter the managers first name:");

    String firstName, lastName;
    int transferBudget;
    quit = false;

//    do {
      firstName = scanner.next();
//      System.out.println("Is " + firstName + " correct? (Y/N)");
//
//      if (scanner.next().toUpperCase().equals("Y")) {
//        quit = true;
//      } else {
//        System.out.println("ERROR. Please enter the managers first name:");
//      }
//    } while (!quit);

    System.out.println("Please enter the managers last name:");
    quit = false;

//    do {
      lastName = scanner.next();
//      System.out.println("Is " + lastName + " correct? (Y/N)");
//
//      if (scanner.next().toUpperCase().equals("Y")) {
//        quit = true;
//      } else {
//        System.out.println("ERROR: Please enter the managers last name:");
//      }
//    } while (!quit);

    System.out.println("Please enter your preferred transfer budget:");
    quit = false;
    transferBudget = Integer.parseInt(scanner.next());
*/

    //TODO: Only allow one manager at the moment - This will need expanding when multiple save games are enabled
//    Manager manager = new Manager(1, firstName, lastName, gameManager.getCurrentFootballTeam()
//            , gameManager.getCurrentCountry().getCountryId(), transferBudget);

    //Simple transfer budget calculation
    int transferBudget = 10 * (int) Math.pow(Math.log(gameManager.getCurrentFootballTeam().getSquad().stream().map(x -> (int) (x.getValue()*1.0)).reduce(0, Integer::sum)), 1.5) + 50000;

    Manager manager = new Manager(1, "a", "a", gameManager.getCurrentFootballTeam()
            , gameManager.getCurrentCountry().getCountryId(), transferBudget);

    ManagerDao managerDao = new ManagerDaoImpl();
    managerDao.insertIntoManagerTable(manager);

    gameManager.setManager(manager);
  }
}
