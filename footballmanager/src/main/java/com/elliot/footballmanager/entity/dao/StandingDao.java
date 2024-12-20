package com.elliot.footballmanager.entity.dao;

import com.elliot.footballmanager.entity.FootballTeam;

import com.elliot.footballmanager.entity.Standing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Elliot
 */
public interface StandingDao {

  public void createNewStandingForFootballTeams(List<FootballTeam> footballTeams);

  /**
   * Updates the Standings stored in the database
   *
   * @param standings Standings to be updated
   */
  public void updateStandingRecords(Collection<Standing> standings);

  /**
   * @param footballTeamId The FootballTeam you want to retrieve the Standing object for
   * @return The Standing object
   */
  public Standing getStandingByFootballTeamId(Integer footballTeamId);

  /**
   * @param leagueId The league we want all FootballTeam standings for
   * @return List of standings for given league
   */
  //TODO: Replace List<Standing> with LeagueTable object
  public List<Standing> getOrderedTableByLeagueId(Integer leagueId);
}
