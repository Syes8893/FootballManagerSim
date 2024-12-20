package com.elliot.footballmanager.fixture;

import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.dao.FixtureDao;
import com.elliot.footballmanager.entity.dao.impl.FixtureDaoImpl;

import java.util.*;

import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.League;

/**
 * Given a {@link League} containing all the {@link FootballTeam}'s that are part of that
 * league this will return a collection of
 * {@link Fixture}'s generated using a round robin scheduling system.
 *
 * @author Elliot
 */
public class RoundRobinFixtureGenerator extends AbstractFixtureFactory implements FixtureGenerator {

  private Integer TOTAL_GAMES_IN_SEASON;
  private Integer HALF_GAMES_IN_SEASON;
  private Date fixtureDate;

  public RoundRobinFixtureGenerator() {

  }

  private void generateFixturesFromArray(FootballTeam[] footballTeams) {
    // Games stored in array at index 8 or higher will be generated as Sunday fixtures
//    int gamesForSunday = 6;
    int total = footballTeams.length - 1;
    for (int i = 0; i < footballTeams.length / 2; i++) {
      //TODO - remove sunday fixtures for now to test for bugs (extra fixtures played for some teams)
//      if (i < gamesForSunday) {
        fixtures.add(new Fixture(footballTeams[i], footballTeams[total], this.getFixtureDate(), footballTeams[i].getLeagueId(), -1));
//      } else {
//        allFixtures.add(createFixtureInsertStatement(footballTeams[i], footballTeams[total],
//            moveFixtureToNextDay(this.getFixtureDate())));
//      }
      total--;
    }

    //Divide matchdays over 300 days after starting date
    int timeBetweenGames = 300/((footballTeams.length-1) * 2);
    fixtureDate = DateUtils.addDays(fixtureDate, timeBetweenGames);
  }

  private Date moveFixtureToNextDay(Date fixtureDate) {
    fixtureDate = DateUtils.addDays(fixtureDate, 1);
    return fixtureDate;
  }

  private FootballTeam[] shiftFootballTeamArray(FootballTeam[] footballTeams, FootballTeam firstTeam) {
    FootballTeam[] shiftedFootballTeams = new FootballTeam[footballTeams.length];
    for (int i = 0; i < footballTeams.length - 1; i++) {
      shiftedFootballTeams[i + 1] = footballTeams[i];
    }
    shiftedFootballTeams[0] = footballTeams[footballTeams.length - 1];

    FootballTeam[] updatedFootballTeams = new FootballTeam[footballTeams.length + 1];
    updatedFootballTeams[0] = firstTeam;
    for (int i = 0; i < updatedFootballTeams.length - 1; i++) {
      updatedFootballTeams[i + 1] = shiftedFootballTeams[i];
    }

    return updatedFootballTeams;
  }

  private void reverseArrayOrder(FootballTeam[] footballTeams) {
    Collections.reverse(Arrays.asList(footballTeams));
  }

  private void buildFixturesList(ArrayList<FootballTeam> footballTeamsOriginal, Date date) {
    if (footballTeamsOriginal == null || footballTeamsOriginal.isEmpty())
      return;

    List<FootballTeam> footballTeams = (ArrayList<FootballTeam>) footballTeamsOriginal.clone();
    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 10);
    Date startDate = calendar.getTime();
    fixtureDate = startDate;

    Collections.shuffle(footballTeams);

    FootballTeam[] footballTeamsArray = new FootballTeam[footballTeams.size()];
    footballTeams.toArray(footballTeamsArray);

    TOTAL_GAMES_IN_SEASON = (footballTeams.size() * footballTeams.size()) - footballTeams.size();
    HALF_GAMES_IN_SEASON = TOTAL_GAMES_IN_SEASON / 2;

    // Home Fixtures
    while (fixtures.size() < HALF_GAMES_IN_SEASON) {
      generateFixturesFromArray(footballTeamsArray);
      // Shift all teams one place to the right (Bar the first one | Round Robin method)
      footballTeamsArray = shiftFootballTeamArray(
          Arrays.copyOfRange(footballTeamsArray, 1, footballTeamsArray.length),
          footballTeamsArray[0]);
    }

    // Reverse FootballTeams array to generate away Fixtures
    reverseArrayOrder(footballTeamsArray);

    // Away Fixtures
    while (fixtures.size() < TOTAL_GAMES_IN_SEASON) {
      generateFixturesFromArray(footballTeamsArray);
      // Shift all teams one place to the right (Bar the first one | Round Robin method)
      footballTeamsArray = shiftFootballTeamArray(
          Arrays.copyOfRange(footballTeamsArray, 1, footballTeamsArray.length),
          footballTeamsArray[0]);
    }
  }

  @Override
  public void generateFixtures(Date date, ArrayList<FootballTeam> footballTeams){
    buildFixturesList(footballTeams, date);
  }

//  @Override
//  public List<String> generateFixtureInsertStatements(Date date, League league) {
//
//    //Build fixtures for only selected league
//    this.getAllFixtures().addAll(buildFixturesList(league.getFootballTeams(), date));
//
//    return this.getAllFixtures();
//  }
//
//  @Override
//  public void insertFixturesIntoDatabase(List<String> fixtures) {
//    FixtureDao fixtureDao = new FixtureDaoImpl();
//    fixtureDao.insertFixturesIntoDatabase(fixtures);
//  }

  public Date getFixtureDate() {
    return fixtureDate;
  }

  public void setFixtureDate(Date fixtureDate) {
    this.fixtureDate = fixtureDate;
  }
}
