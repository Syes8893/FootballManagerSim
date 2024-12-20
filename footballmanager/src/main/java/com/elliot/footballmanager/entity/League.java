package com.elliot.footballmanager.entity;

import java.util.ArrayList;
import java.util.List;

import com.elliot.footballmanager.entity.Fixture;
import com.elliot.footballmanager.entity.FootballTeam;

/**
 * @author Elliot
 */
public class League {

  private Integer leagueId;
  private String leagueName;
  private Integer countryId;
  private ArrayList<FootballTeam> footballTeams;
  private List<Fixture> upcomingFixtures;

//  private LeagueTable leagueTable;

  public League() {

  }

  public League(Integer leagueId, String leagueName, Integer countryId) {
    this.leagueId = leagueId;
    this.leagueName = leagueName;
    this.countryId = countryId;
  }

  public int getLeagueId() {
    return leagueId;
  }

  public void setLeagueId(Integer leagueId) {
    this.leagueId = leagueId;
  }

  public String getLeagueName() {
    return leagueName;
  }

  public void setLeagueName(String leagueName) {
    this.leagueName = leagueName;
  }

  public Integer getCountryId() {
    return countryId;
  }

  public void setCountryId(Integer countryId) {
    this.countryId = countryId;
  }

  public ArrayList<FootballTeam> getFootballTeams() {
    return footballTeams;
  }

  public void setFootballTeams(ArrayList<FootballTeam> footballTeams) {
    this.footballTeams = footballTeams;
  }

  public List<Fixture> getUpcomingFixtures() {
    return upcomingFixtures;
  }

  public void setUpcomingFixtures(List<Fixture> upcomingFixtures) {
    this.upcomingFixtures = upcomingFixtures;
  }

  public String printLeagueMenuInfo() {
    return "[" + this.getLeagueId() + "]" + " " + this.getLeagueName();
  }
}
