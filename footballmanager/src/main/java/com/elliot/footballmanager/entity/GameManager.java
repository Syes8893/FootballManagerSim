package com.elliot.footballmanager.entity;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.dao.GameManagerDao;
import com.elliot.footballmanager.entity.dao.StandingDao;
import com.elliot.footballmanager.entity.dao.impl.GameManagerDaoImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.dao.FixtureDao;
import com.elliot.footballmanager.entity.dao.impl.FixtureDaoImpl;
import com.elliot.footballmanager.entity.dao.impl.MatchEngineDaoImpl;
import com.elliot.footballmanager.entity.dao.impl.StandingDaoImpl;
import com.elliot.footballmanager.match.MatchResult;
import com.elliot.footballmanager.match.engine.MatchEngine;
import com.elliot.footballmanager.menu.MainMenu;
import com.elliot.footballmanager.menu.MatchDayMenu;
import com.elliot.footballmanager.season.NewSeasonBuilder;

/**
 * The GameManager class handles all the information required to successfully play the Football
 * Manager simulator.
 *
 * @author Elliot
 */
public class GameManager {

  public MainMenu mainMenu;

  private Country currentCountry;
  private League currentLeague;
  private FootballTeam currentFootballTeam;
  private Manager manager;
  private Date currentDate;
  private PriorityQueue<Fixture> upcomingFixtures;
  private ArrayList<Fixture> cupFixtures;
  private PriorityQueue<Fixture> seasonFixtures;
  private List<Fixture> allFixtures;
  private TreeMap<UUID, MatchResult> matchResults = new TreeMap<>();
  private HashMap<Integer, Standing> standingList = new HashMap<Integer, Standing>();

  private List<Country> allCountries = new ArrayList<>();
  private List<League> allLeagues = new ArrayList<>();
  private ArrayList<FootballTeam> allTeams = new ArrayList<>();

  public GameManager() {

  }

  public GameManager(Country currentCountry, League currentLeague, FootballTeam currentFootballTeam,
      Manager manager, Date currentDate) {
    this.currentCountry = currentCountry;
    this.currentLeague = currentLeague;
    this.currentFootballTeam = currentFootballTeam;
    this.manager = manager;
    this.currentDate = currentDate;
  }

  /**
   * Retrieves the selected Country, League, FootballTeam and Manager from the database and
   * instantiates a new GameManager object.
   * @return true if there is a save game, false otherwise
   */
  public boolean loadSavedGame() {
    //TODO - REVAMP LOADING AND SAVING

    //OLD CODE BELOW
//    GameManagerDao gameManagerDao = new GameManagerDaoImpl();
//    if(!gameManagerDao.loadSavedGame(this))
//      return false;
//
//    FixtureDao fixtureDao = new FixtureDaoImpl();
//    this.setUpcomingFixtures(
//        fixtureDao.getFootballTeamsUpcomingFixtures(this.getCurrentFootballTeam(), currentDate));
//
//    mainMenu = new MainMenu(this);
    return true;
  }

  /**
   * Persists the current information stored in the GameManager object into the database.
   */
  public void saveGame() {
    new Thread(){
      @Override
      public void run() {
        super.run();
        System.out.println("Saving game...");
        new GameManagerDaoImpl().saveGame(GameManager.this);
        new FixtureDaoImpl().insertFixturesIntoDatabase(allFixtures);
        new StandingDaoImpl().updateStandingRecords(standingList.values());
        new MatchEngineDaoImpl().persistResultsToDatabase(matchResults.values());
        System.out.println("Game Saved Successfully!");
      }
    }.start();
  }

  public String getQuickGameInfo() {
    return DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(this.getCurrentDate())
        + " " + this.getCurrentFootballTeam().getTeamName()
        + " " + this.getCurrentLeague().getLeagueName();
  }

  /**
   * Advances the currentDate by a day until a new Fixture is found.
   */
  public void simulateGame() {
    //TODO - after season ends generate new season fixtures
    if(this.getUpcomingFixtures().isEmpty()){
      while (seasonFixtures.peek() != null && currentDate.before(seasonFixtures.peek().getDateOfFixture())) {
        currentDate = DateUtils.addDays(currentDate, 1);
        currentDate.setHours(0);
        simulateFixtures(); //TODO - fix fixtures as some teams get slightly more games than others
        System.out
                .print("\rCurrent date: " + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(currentDate));
        try {
//        Thread.sleep(400);
          Thread.sleep(1); //TODO - revert to 400 millis
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      currentDate.setHours(0);
      SqliteDatabaseConnector.deleteSavedGameArtifacts(true);
      NewSeasonBuilder.setupNewSeason(this);
      mainMenu.beginMenuSelectionLoop();
      return;
    }

    while (currentDate.before(this.getUpcomingFixtures().peek().getDateOfFixture())) {
      currentDate = DateUtils.addDays(currentDate, 1);
      currentDate.setHours(0);
      if (!currentDate.equals(this.getUpcomingFixtures().peek().getDateOfFixture()))
        simulateFixtures(); //TODO - fix fixtures as some teams get slightly more games than others
      System.out
          .print("\rCurrent date: " + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(currentDate));
      try {
//        Thread.sleep(400);
        Thread.sleep(1); //TODO - revert to 400 millis
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.print("\r                                           ");

    // Match day;
    if (currentDate.equals(this.getUpcomingFixtures().peek().getDateOfFixture()) && currentDate.getHours() == 0) {
      MatchDayMenu matchDayMenu = new MatchDayMenu(this);
      matchDayMenu.beginMenuSelectionLoop();
    }
  }

  private void simulateFixtures() {
    List<MatchResult> matchResults = new ArrayList<>();
    MatchEngine.setIsLoggingGameEvents(false);

    while(getSeasonFixtures().peek() != null && seasonFixtures.peek().getDateOfFixture().equals(currentDate)){
      MatchResult matchResult = MatchEngine.beginFootballMatchSimulator(seasonFixtures.poll(), standingList);
      matchResults.add(matchResult);
      getMatchResults().put(matchResult.getFixture().getFixtureId(), matchResult);
    }

    if(matchResults.isEmpty())
      return;

    if(!matchResults.get(0).getFixture().isCup())
      return;

    if(matchResults.size() == 1) {
      System.out.println(matchResults.get(0).getWinner().getTeamName() + " wins the cup!");
      return;
    }
    //TODO - return whether player is champion

    int i = 0;
    Date fixtureDate = getCurrentDate();
    fixtureDate.setHours(0);
    while(i < matchResults.size()){
      Fixture fixture = new Fixture(matchResults.get(i).getWinner(), matchResults.get(i+1).getWinner()
              , DateUtils.addDays(fixtureDate, 28), matchResults.get(i).getWinner().getLeagueId()
              , Math.max(matchResults.get(i).getFixture().getCupLevel()-1, matchResults.get(i+1).getFixture().getCupLevel())-1);
      cupFixtures.add(fixture);
      seasonFixtures.add(fixture);
      i+=2;
    }
  }

  public void displayLeagueTable() {
    List<Standing> list = new ArrayList<>(standingList.values().stream().filter(x -> x.getLeagueId() == currentLeague.getLeagueId()).toList());
    list.sort((lhs, rhs) -> {
		int i = rhs.getPoints()-(lhs.getPoints());
		int k = rhs.getGoalDifference()-(lhs.getGoalDifference());
		int z = rhs.getGoalsFor()-(lhs.getGoalsFor());
		return (i != 0) ? i : (k != 0) ? k : z;
	});
    int size = list.stream().map(x -> (x.getFootballTeamName().length())).max(Comparator.comparingInt(a -> a)).orElse(0);
    System.out.print("\n");
    System.out.println(ColorUtils.RED_BOLD + currentLeague.getLeagueName().toUpperCase() + " STANDINGS (" + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(getCurrentDate()) + ")" + ColorUtils.RESET);
    list.forEach(x -> System.out.println(
            (x.getFootballTeamName().equals(this.getCurrentFootballTeam().getTeamName()) ? "" + ColorUtils.PURPLE_BOLD : "")
            + (list.indexOf(x) >= 9 ? list.indexOf(x)+1 : list.indexOf(x)+1 + " ") + " | "
            + (x.getFootballTeamName() + new String(new char[size-x.getFootballTeamName().length()]).replace("\0", " "))
            + " | MP:" + (x.getWins() + x.getDraws() + x.getLosses())
            + " | W:" + x.getWins() + " | D:" + x.getDraws() + " | L:" + x.getLosses()
            + " | P:" + x.getPoints() + " | GF:" + x.getGoalsFor() + " | GA:" + x.getGoalsAgainst()
            + " | GD:" + x.getGoalDifference()
            + (x.getFootballTeamName().equals(this.getCurrentFootballTeam().getTeamName()) ? " << YOUR TEAM" + ColorUtils.RESET : "")
    ));
    System.out.print("\n");
  }

  public void displayCupMatchups() {
    cupFixtures.sort(Comparator.comparingInt(Fixture::getCupLevel).reversed());
    int maxCupLevel = cupFixtures.get(0).getCupLevel();
    int currentCupLevel = cupFixtures.get(0).getCupLevel();
    int index = 0;
    System.out.println(ColorUtils.YELLOW_BOLD + "Elimination Round "
            + " (" + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(cupFixtures.get(index).getDateOfFixture()) + ")" + ColorUtils.RESET);
    while(index < cupFixtures.size()){
      if(cupFixtures.get(index).getCupLevel() == currentCupLevel)
        if(matchResults.containsKey(cupFixtures.get(index).getFixtureId()))
          matchResults.get(cupFixtures.get(index).getFixtureId()).displayMatchResult();
        else
          if(cupFixtures.get(index).getHomeTeam().equals(currentFootballTeam)
                  || cupFixtures.get(index).getAwayTeam().equals(currentFootballTeam))
            System.out.println(ColorUtils.PURPLE_BOLD + "FT: " + cupFixtures.get(index).getHomeTeam().getTeamName()
                    + " - " + cupFixtures.get(index).getAwayTeam().getTeamName() + " << YOUR MATCH" + ColorUtils.RESET);
          else
            System.out.println("FT: " + cupFixtures.get(index).getHomeTeam().getTeamName() + " - " + cupFixtures.get(index).getAwayTeam().getTeamName());
      else{
        System.out.println(" ");
        String roundName;
        if(cupFixtures.get(index).getCupLevel() == 1)
          roundName = "Final";
        else if(cupFixtures.get(index).getCupLevel() == 2)
          roundName = "Semi-Final";
        else if(cupFixtures.get(index).getCupLevel() == 3)
          roundName = "Quarter-Final";
        else
          roundName = "Round " +  (maxCupLevel-cupFixtures.get(index).getCupLevel());
        System.out.println(ColorUtils.YELLOW_BOLD + roundName
                + " (" + DateUtils.FIXTURE_DATE_DISPLAY_FORMAT.format(cupFixtures.get(index).getDateOfFixture()) + ")" + ColorUtils.RESET);
        currentCupLevel--;
        index--;
      }
      index++;
    }
  }

  public Country getCurrentCountry() {
    return currentCountry;
  }

  public void setCurrentCountry(Country selectedCountry) {
    this.currentCountry = selectedCountry;
  }

  public League getCurrentLeague() {
    return currentLeague;
  }

  public void setCurrentLeague(League currentLeague) {
    this.currentLeague = currentLeague;
  }

  public FootballTeam getCurrentFootballTeam() {
    return currentFootballTeam;
  }

  public void setCurrentFootballTeam(FootballTeam footballTeam) {
    this.currentFootballTeam = footballTeam;
  }

  public Date getCurrentDate() {
    return currentDate;
  }

  public void setCurrentDate(Date currentDate) {
    this.currentDate = currentDate;
  }

  public Manager getManager() {
    return manager;
  }

  public void setManager(Manager manager) {
    this.manager = manager;
  }

  public PriorityQueue<Fixture> getUpcomingFixtures() {
    return upcomingFixtures;
  }

  public void setUpcomingFixtures(PriorityQueue<Fixture> upcomingFixtures) {
    this.upcomingFixtures = upcomingFixtures;
  }

  public ArrayList<Fixture> getCupFixtures() {
    return cupFixtures;
  }

  public void addUpcomingFixtures(Fixture fixture) {
    this.upcomingFixtures.add(fixture);
  }

  public void addCupFixtures(Fixture fixture){
    cupFixtures.add(fixture);
  }

  public void setCupFixtures(ArrayList<Fixture> cupFixtures) {
    this.cupFixtures = cupFixtures;
  }

  public PriorityQueue<Fixture> getSeasonFixtures() {
    return seasonFixtures;
  }

  public void setSeasonFixtures(PriorityQueue<Fixture> seasonFixtures) {
    this.seasonFixtures = seasonFixtures;
  }

  public void addSeasonFixtures(Fixture fixture){this.seasonFixtures.add(fixture);}

  public List<Fixture> getAllFixtures() {
    return allFixtures;
  }

  public void setAllFixtures(List<Fixture> allFixtures) {
    this.allFixtures = allFixtures;
  }

  public TreeMap<UUID, MatchResult> getMatchResults() {
    return matchResults;
  }

  public void setMatchResults(TreeMap<UUID, MatchResult> matchResults) {
    this.matchResults = matchResults;
  }

  public List<Country> getAllCountries() {
    return allCountries;
  }

  public void setAllCountries(List<Country> allCountries) {
    this.allCountries = allCountries;
  }

  public List<League> getAllLeagues() {
    return allLeagues;
  }

  public void setAllLeagues(List<League> allLeagues) {
    this.allLeagues = allLeagues;
  }

  public HashMap<Integer, Standing> getStandingList() {
    return standingList;
  }

  public void addToStandingList(FootballTeam footballTeam, Standing standing){
    standingList.put(footballTeam.getFootballTeamId(), standing);
  }

  public void setStandingList(HashMap<Integer, Standing> standingList) {
    this.standingList = standingList;
  }

  public ArrayList<FootballTeam> getAllTeams() {
    return allTeams;
  }

  public void setAllTeams(ArrayList<FootballTeam> allTeams) {
    this.allTeams = allTeams;
  }
}
