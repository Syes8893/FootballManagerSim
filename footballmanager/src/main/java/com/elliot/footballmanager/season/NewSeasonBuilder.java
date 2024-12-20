package com.elliot.footballmanager.season;

import com.elliot.footballmanager.entity.*;

import com.elliot.footballmanager.fixture.FixtureGenerator;
import com.elliot.footballmanager.fixture.FixtureGeneratorFactory;
import com.elliot.footballmanager.fixture.FixtureGeneratorType;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetupBuilder;
import com.elliot.footballmanager.menu.StartMenu;

import java.util.*;

/**
 * Class to simplify the creation of a new Football season.
 *
 * @author Elliot
 */
public class NewSeasonBuilder {

  private static GameManager gameManager;

  private NewSeasonBuilder() {

  }

  public static void setupNewSeason(GameManager incomingGameManager) {
    gameManager = incomingGameManager;
    StartMenu.newGameStartDate.setYear(StartMenu.newGameStartDate.getYear()+1);
    //Set new start date to new year, perhaps store season in gamemanager?
    gameManager.setCurrentDate(StartMenu.newGameStartDate);

    //Reset match results
    gameManager.setMatchResults(new TreeMap<>());

    setupFixtures();
    setupTeamsMatchInfo();
    setupStandings();
  }

  //TODO - resolve issue for leagues with uneven amount of teams (will generate unbalanced amount of fixtures for each team)
  private static void setupFixtures() {
    System.out.println("Generating Fixtures...");
    ArrayList<Fixture> fixtureList = new ArrayList<>();

    //Generate league fixtures
    FixtureGeneratorFactory fixtureGeneratorFactory = new FixtureGeneratorFactory();
    FixtureGenerator fixtureGenerator = fixtureGeneratorFactory.getFixtureGenerator(FixtureGeneratorType.ROUND_ROBIN);
    fixtureGenerator.generateFixtures(gameManager.getCurrentDate(), gameManager.getCurrentLeague().getFootballTeams());
    fixtureList.addAll(fixtureGenerator.fixtures);


    //TODO - add multiple cups and allow for cups for leagues with uneven teams
    if(gameManager.getCurrentLeague().getFootballTeams().size()%2 == 0){
      //Generate cup fixtures
      FixtureGenerator cupFixtureGenerator = fixtureGeneratorFactory.getFixtureGenerator(FixtureGeneratorType.GROUP_TOURNAMENT);
//      cupFixtureGenerator.generateFixtures(gameManager.getCurrentDate(), gameManager.getCurrentLeague().getFootballTeams());
//      fixtureList.addAll(cupFixtureGenerator.fixtures);

      //TESTING all teams generation
      cupFixtureGenerator.fixtures.clear();
      cupFixtureGenerator.generateFixtures(gameManager.getCurrentDate(), new ArrayList<>(gameManager.getAllTeams().stream().filter(x -> x.getSquad().size() >= 11).toList()));
      fixtureList.addAll(cupFixtureGenerator.fixtures);
    }

    fixtureList.sort(Comparator.comparing(Fixture::getDateOfFixture));
    ArrayList<Fixture> allFixtures = new ArrayList<>(fixtureList.stream().distinct().toList());
    gameManager.setAllFixtures(allFixtures);
    PriorityQueue<Fixture> priorityQueue = new PriorityQueue<>(allFixtures.size(), Comparator.comparing(Fixture::getDateOfFixture));
    priorityQueue.addAll(allFixtures);
    gameManager.setSeasonFixtures(priorityQueue);

    ArrayList<Fixture> cupFixtureList = new ArrayList<>(fixtureList.stream().filter(Fixture::isCup).sorted(Comparator.comparing(Fixture::getCupLevel).reversed()).distinct().toList());
    gameManager.setCupFixtures(cupFixtureList);

    fixtureList = new ArrayList<>(fixtureList.stream().filter(x -> x.getDateOfFixture().after(gameManager.getCurrentDate())
            && (x.getHomeTeam().getFootballTeamId() == gameManager.getCurrentFootballTeam().getFootballTeamId()
            || x.getAwayTeam().getFootballTeamId() == gameManager.getCurrentFootballTeam().getFootballTeamId()))
            .distinct().toList());
    PriorityQueue<Fixture> queue = new PriorityQueue<>(fixtureList.size(), Comparator.comparing(Fixture::getDateOfFixture));
    queue.addAll(fixtureList);
    gameManager.setUpcomingFixtures(queue);

//    long delta2 = System.currentTimeMillis();
//    gameManager.setUpcomingFixtures(new FixtureDaoImpl().getFootballTeamsUpcomingFixtures(gameManager.getCurrentFootballTeam(), gameManager.getCurrentDate()));
//    System.out.println(System.currentTimeMillis() - delta2);
  }

  private static void setupTeamsMatchInfo() {
    System.out.println("Generating Team Match day data...");

//    FootballTeamMatchSetupDao footballTeamMatchSetupDao = new FootballTeamMatchSetupDaoImpl();

    new Thread(){
      @Override
      public void run() {
        super.run();
        for (FootballTeam footballTeam : gameManager.getCurrentLeague().getFootballTeams()) {
          footballTeam.setMatchSetup(FootballTeamMatchSetupBuilder.buildNewMatchSetup(footballTeam, null));
//          footballTeamMatchSetupDao.persistFootballTeamMatchSetup(footballTeam);
        }
      }
    }.start();
  }

  private static void setupStandings() {
    System.out.println("Generating Standings...");

//    Integer leagueId = gameManager.getCurrentLeague().getLeagueId();
//    FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
//    gameManager.getCurrentLeague().setFootballTeams(
//            new ArrayList<FootballTeam>(footballTeamDao.getAllFootballTeams(leagueId)));

    new Thread(){
      @Override
      public void run() {
        super.run();
        for (FootballTeam footballTeam : gameManager.getCurrentLeague().getFootballTeams()) {
          gameManager.addToStandingList(footballTeam, new Standing(footballTeam));
        }
      }
    }.start();

//    LeagueDao leagueDao = new LeagueDaoImpl();
//    List<League> leagues = leagueDao.getAllLeagues();
//
//    List<Integer> leaguesList = leagues.stream().map(League::getLeagueId).filter(id -> id != 78).toList();
//    ArrayList<FootballTeam> footballTeams = new ArrayList<>();
//    for(int i : leaguesList){
//       footballTeams.addAll(footballTeamDao.getAllFootballTeams(i));
//    }
//
//    for (FootballTeam footballTeam : footballTeams) {
//      standingDao.createNewStandingForFootballTeam(footballTeam);
//    }

  }
}
