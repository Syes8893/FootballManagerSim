package com.elliot.footballmanager.fixture;

import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.League;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Top level interface providing methods outlining the common functionality between various
 * FixtureGenerator's.
 *
 * @author Elliot
 */
public interface FixtureGenerator {

  ArrayList<Fixture> fixtures = new ArrayList<>();

  public void generateFixtures(Date date, ArrayList<FootballTeam> footballTeams);

//  public List<String> generateFixtureInsertStatements(Date date, League league);
//
//  public void insertFixturesIntoDatabase(List<String> fixtures);

}
