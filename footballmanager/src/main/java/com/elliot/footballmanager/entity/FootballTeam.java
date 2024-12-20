package com.elliot.footballmanager.entity;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.footballteam.IFootballTeam;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetup;
import com.elliot.footballmanager.entity.dao.FootballTeamMatchSetupDao;
import com.elliot.footballmanager.entity.dao.impl.FootballTeamMatchSetupDaoImpl;
import com.elliot.footballmanager.entity.dao.PlayerDao;
import com.elliot.footballmanager.entity.dao.impl.PlayerDaoImpl;
import com.elliot.footballmanager.footballteam.matchsetup.FootballTeamMatchSetupBuilder;
import com.elliot.footballmanager.footballteam.matchsetup.MatchDaySquad;
import com.elliot.footballmanager.match.engine.MatchEngine;

/**
 * @author Elliot
 */
public class FootballTeam implements Serializable, IFootballTeam {

  private Integer footballTeamId;
  private String teamName;
  private Integer leagueId;
  private String location;
  private String stadium;
  private Integer stadiumCapacity;
  private int value;
  private List<Player> squad;
  private FootballTeamMatchSetup matchSetup;

  public FootballTeam() {

  }

  public FootballTeam(Integer footballTeamId, String teamName, Integer leagueId,
      String location, String stadium, Integer stadiumCapacity, int value) {
    this.footballTeamId = footballTeamId;
    this.teamName = teamName;
    this.leagueId = leagueId;
    this.location = location;
    this.stadium = stadium;
    this.stadiumCapacity = stadiumCapacity;
    this.value = value;
    this.squad = getSquad();
  }

  private void buildSquad() {
    PlayerDao pDao = new PlayerDaoImpl();
    this.setSquad(pDao.getAllPlayersForFootballTeam(this));
  }

  private void getMatchSetupFromDatabase() {
    FootballTeamMatchSetupDao footballTeamMatchSetupDao = new FootballTeamMatchSetupDaoImpl();
    this.setMatchSetup(footballTeamMatchSetupDao.getFootballTeamMatchSetup(footballTeamId));
  }

  public int getFootballTeamId() {
    return footballTeamId;
  }

  public void setFootballTeamId(Integer footballTeamId) {
    this.footballTeamId = footballTeamId;
  }

  public String getTeamName() {
    return teamName;
  }

  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  public int getLeagueId() {
    return leagueId;
  }

  public void setLeagueId(Integer leagueId) {
    this.leagueId = leagueId;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getStadium() {
    return stadium;
  }

  public void setStadium(String stadium) {
    this.stadium = stadium;
  }

  public Integer getStadiumCapacity() {
    return stadiumCapacity;
  }

  public void setStadiumCapacity(Integer stadiumCapacity) {
    this.stadiumCapacity = stadiumCapacity;
  }

  public List<Player> getSquad() {
    if (squad == null) {
      buildSquad();
    }
    return squad;
  }

  public void setSquad(List<Player> squad) {
    this.squad = squad;
  }

  public FootballTeamMatchSetup getMatchSetup() {
    if (matchSetup == null) {
      getMatchSetupFromDatabase();
      if(matchSetup == null)
        matchSetup = FootballTeamMatchSetupBuilder.buildNewMatchSetup(this, null);
    }

    return matchSetup;
  }

  public void setMatchSetup(FootballTeamMatchSetup matchSetup) {
    this.matchSetup = matchSetup;
  }


  public void printFootballTeamInfo() {
    String[] formationInStringArray = matchSetup.getSelectedFormation().getFormation().getFormationName().split("-");
    int defendersCount = Integer.parseInt(formationInStringArray[0]);
    int midfieldersCount = Integer.parseInt(formationInStringArray[1]);
    int attackersCount = Integer.parseInt(formationInStringArray[2]);
    int i = 1;
    String goalkeeper = "Goalkeeper: " + matchSetup.getSelectedFormation().getStartingLineup()[0].getNameAndOverallAndPosition();
    String defenders = "Defenders (" + defendersCount + "): ";
    while(i <= defendersCount){
      defenders += ", " + matchSetup.getSelectedFormation().getStartingLineup()[i].getNameAndOverallAndPosition();
      i++;
    }

    String midfielders = "Midfielders (" + midfieldersCount + "): ";
    while(i <= midfieldersCount + defendersCount){
      midfielders += ", " + matchSetup.getSelectedFormation().getStartingLineup()[i].getNameAndOverallAndPosition();
      i++;
    }

    String attackers = "Attackers (" + attackersCount + "): ";
    while(i <= attackersCount + midfieldersCount + defendersCount){
      attackers += ", " + matchSetup.getSelectedFormation().getStartingLineup()[i].getNameAndOverallAndPosition();
      i++;
    }
    String startingLineup =
            (goalkeeper + "\n")
            + (defenders + "\n").replaceFirst(", ", "")
            + (midfielders + "\n").replaceFirst(", ", "")
            + (attackers + "\n\n").replaceFirst(", ", "");

    String subs = "Subs: ";
    for(Player p : matchSetup.getSelectedFormation().getSubstitutions())
      subs += ", " + p.getNameAndOverallAndPosition();
    subs = subs.replaceFirst(", ", "");

    String reserves = "Reserves: ";
    for(Player p : matchSetup.getSelectedFormation().getReserves())
      reserves += ", " + p.getNameAndOverallAndPosition();
    reserves = reserves.replaceFirst(", ", "");

    String result =
            ColorUtils.BLUE_BOLD
            + getTeamName() + " (" + matchSetup.getSelectedFormation().getFormation().getFormationName() + ")\n"
            + ColorUtils.RESET
            + startingLineup
            + subs
            + "\n" + reserves
            ;
    System.out.println(result);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FootballTeam that = (FootballTeam) o;
    return Objects.equals(footballTeamId, that.footballTeamId) &&
        Objects.equals(teamName, that.teamName) &&
        Objects.equals(leagueId, that.leagueId) &&
        Objects.equals(location, that.location) &&
        Objects.equals(stadium, that.stadium) &&
        Objects.equals(stadiumCapacity, that.stadiumCapacity) &&
        Objects.equals(value, that.value) &&
        Objects.equals(squad, that.squad) &&
        Objects.equals(matchSetup, that.matchSetup);
  }

  @Override
  public int hashCode() {
    return Objects
        .hash(footballTeamId, teamName, leagueId, location, stadium, stadiumCapacity, squad,
            matchSetup);
  }
}
