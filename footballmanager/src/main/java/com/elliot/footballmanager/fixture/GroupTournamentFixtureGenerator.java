package com.elliot.footballmanager.fixture;

import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.League;
import com.elliot.footballmanager.entity.dao.FixtureDao;
import com.elliot.footballmanager.entity.dao.impl.FixtureDaoImpl;

import java.util.*;

/**
 * Provides the ability to generate a collection of Fixture's using a group tournament algorithm
 *
 * @author Elliot
 */
public class GroupTournamentFixtureGenerator extends AbstractFixtureFactory implements FixtureGenerator {


  private Date fixtureDate;

  public GroupTournamentFixtureGenerator() {
    // TODO Auto-generated constructor stub
  }

//  private void generateFixturesFromArray(List<String> allFixtures, FootballTeam[] footballTeams) {
//    int total = footballTeams.length - 1;
//    for (int i = 0; i < footballTeams.length / 2; i++) {
//      //TODO - remove sunday fixtures for now to test for bugs (extra fixtures played for some teams)
//      allFixtures.add(createFixtureInsertStatement(footballTeams[i], footballTeams[total],
//              this.getFixtureDate()));
//      total--;
//    }
//
//    //Divide matchdays over 200 days after starting date
//    int timeBetweenGames = (int) (200/(Math.log(footballTeams.length)/Math.log(2)));
//    fixtureDate = DateUtils.addDays(fixtureDate, timeBetweenGames);
//  }

  private void buildFixturesList(ArrayList<FootballTeam> footballTeams, Date date){
    if (footballTeams == null || footballTeams.isEmpty())
      return;

    ArrayList<FootballTeam> filteredTeams = new ArrayList<>(footballTeams);

    Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 20);
    Date startDate = calendar.getTime();
    fixtureDate = startDate;

    Collections.shuffle(filteredTeams);

    int treeHeight = (int) Math.floor(Math.log(filteredTeams.size())/Math.log(2));
    int i = 0;
    int originalSize = filteredTeams.size();
    if(fixtureDate.getDay() == 6)
      DateUtils.addDays(fixtureDate, 3);
    while (i < (originalSize-Math.pow(2, treeHeight))*2){
      fixtures.add(new Fixture(filteredTeams.remove(0), filteredTeams.get(0),  fixtureDate, filteredTeams.remove(0).getLeagueId(), treeHeight+1));
      i+=2;
    }
    while (!filteredTeams.isEmpty()){
      fixtures.add(new Fixture(filteredTeams.remove(0), filteredTeams.get(0),  DateUtils.addDays(fixtureDate, 28), filteredTeams.remove(0).getLeagueId(), treeHeight));
    }
  }

  @Override
  public void generateFixtures(Date date, ArrayList<FootballTeam> footballTeams){
    buildFixturesList(footballTeams, date);
  }


//  @Override
//  public List<String> generateFixtureInsertStatements(Date date, League league) {
//    this.getAllFixtures().addAll(buildFixturesList(league.getFootballTeams(), date));
//
//    return this.getAllFixtures();
////    return buildFixturesList(league.getFootballTeams(), date, league);
//  }

//  @Override
//  public void insertFixturesIntoDatabase(List<String> fixtures) {
//    FixtureDao fixtureDao = new FixtureDaoImpl();
//    fixtureDao.insertFixturesIntoDatabase(fixtures);
//  }

}
