package com.elliot.footballmanager.standings;

import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.dao.StandingDao;
import com.elliot.footballmanager.entity.dao.impl.StandingDaoImpl;
import com.elliot.footballmanager.entity.Standing;
import com.elliot.footballmanager.match.MatchResult;

import java.util.HashMap;
import java.util.List;

/**
 * Given a MatchResult object, builds two Standing objects for the two FootballTeams that played
 *
 * @author Elliot
 */
public class StandingBuilder {

  private final int POINTS_FOR_WIN = 3;
  private final int POINTS_FOR_DRAW = 1;

  private final MatchResult matchResult;

  private Standing homeTeamStanding;
  private Standing awayTeamStanding;

  private final HashMap<Integer, Standing> standingList;

  public StandingBuilder(MatchResult matchResult, HashMap<Integer, Standing> standingList) {
    this.matchResult = matchResult;
    this.standingList = standingList;

//    FootballTeam homeTeam = matchResult.getFixture().getHomeTeam();
//    FootballTeam awayTeam = matchResult.getFixture().getAwayTeam();

//    homeTeamStanding = new Standing(homeTeam);
//    awayTeamStanding = new Standing(awayTeam);

//    updateFootballTeamIds();
  }

  private void updateFootballTeamIds() {
    homeTeamStanding.setFootballTeamId(
        matchResult.getHomeTeamMatchStats().getFootballTeam().getFootballTeamId());
    awayTeamStanding.setFootballTeamId(
        matchResult.getAwayTeamMatchStats().getFootballTeam().getFootballTeamId());
  }

  public void buildStandingsFromMatchResult() {
//    getStandingObjectsFromDatabase();
    getStandingsFromMap();

    addLeagueId();
    addFootballTeamIds();
    addGoalsForAndAgainst();
    addGoalDifferences();
    addWinLossDraw();
    addPoints();
    incrementGamesPlayed();

    standingList.put(matchResult.getFixture().getHomeTeam().getFootballTeamId(), homeTeamStanding);
    standingList.put(matchResult.getFixture().getAwayTeam().getFootballTeamId(), awayTeamStanding);
//    calculateTablePositions();
  }

  private void getStandingsFromMap(){
    if(standingList.containsKey(matchResult.getFixture().getHomeTeam().getFootballTeamId()))
      homeTeamStanding = standingList.get(matchResult.getFixture().getHomeTeam().getFootballTeamId());
    if(standingList.containsKey(matchResult.getFixture().getAwayTeam().getFootballTeamId()))
      awayTeamStanding = standingList.get(matchResult.getFixture().getAwayTeam().getFootballTeamId());
  }

//  private void getStandingObjectsFromDatabase() {
////    StandingDao standingDao = new StandingDaoImpl();
//
//    this.homeTeamStanding = standingDao
//        .getStandingByFootballTeamId(homeTeamStanding.getFootballTeamId());
//    this.awayTeamStanding = standingDao
//        .getStandingByFootballTeamId(awayTeamStanding.getFootballTeamId());
//  }

//  public void updateStandingsInDatabase() {
//    persistUpdatedTable();
//  }

  private void addLeagueId() {
    Integer leagueId = matchResult.getFixture().getLeagueId();
    homeTeamStanding.setLeagueId(leagueId);
    awayTeamStanding.setLeagueId(leagueId);
  }

  private void addFootballTeamIds() {
    homeTeamStanding.setFootballTeamId(
        matchResult.getHomeTeamMatchStats().getFootballTeam().getFootballTeamId());
    awayTeamStanding.setFootballTeamId(
        matchResult.getAwayTeamMatchStats().getFootballTeam().getFootballTeamId());
  }

  private void addGoalsForAndAgainst() {
    homeTeamStanding.setGoalsFor(
        homeTeamStanding.getGoalsFor() + matchResult.getHomeTeamMatchStats().getGoals());
    awayTeamStanding.setGoalsFor(
        awayTeamStanding.getGoalsFor() + matchResult.getAwayTeamMatchStats().getGoals());

    homeTeamStanding.setGoalsAgainst(
        homeTeamStanding.getGoalsAgainst() + matchResult.getAwayTeamMatchStats().getGoals());
    awayTeamStanding.setGoalsAgainst(
        awayTeamStanding.getGoalsAgainst() + matchResult.getHomeTeamMatchStats().getGoals());
  }

  private void addGoalDifferences() {
    homeTeamStanding
        .setGoalDifference(homeTeamStanding.getGoalsFor() - homeTeamStanding.getGoalsAgainst());
    awayTeamStanding
        .setGoalDifference(awayTeamStanding.getGoalsFor() - awayTeamStanding.getGoalsAgainst());
  }

  private void addWinLossDraw() {
    switch (matchResult.getResult()) {
      case H:
        homeTeamStanding.setWins(homeTeamStanding.getWins() + 1);
        awayTeamStanding.setLosses(awayTeamStanding.getLosses() + 1);
        break;
      case A:
        homeTeamStanding.setLosses(homeTeamStanding.getLosses() + 1);
        awayTeamStanding.setWins(awayTeamStanding.getWins() + 1);
        break;
      case D:
        homeTeamStanding.setDraws(homeTeamStanding.getDraws() + 1);
        awayTeamStanding.setDraws(awayTeamStanding.getDraws() + 1);
        break;
      default:
        throw new IllegalArgumentException("Invalid Match Result!");
    }
  }

  private void addPoints() {
    switch (matchResult.getResult()) {
      case H:
        homeTeamStanding.setPoints(homeTeamStanding.getPoints() + POINTS_FOR_WIN);
        break;
      case A:
        awayTeamStanding.setPoints(awayTeamStanding.getPoints() + POINTS_FOR_WIN);
        break;
      case D:
        homeTeamStanding.setPoints(homeTeamStanding.getPoints() + POINTS_FOR_DRAW);
        awayTeamStanding.setPoints(awayTeamStanding.getPoints() + POINTS_FOR_DRAW);
        break;
      default:
        throw new IllegalArgumentException("Invalid Match Result!");
    }
  }

  private void incrementGamesPlayed() {
    homeTeamStanding.setGamesPlayed(homeTeamStanding.getGamesPlayed() + 1);
    awayTeamStanding.setGamesPlayed(awayTeamStanding.getGamesPlayed() + 1);
  }

//  private void calculateTablePositions() {
//    Integer leagueId = homeTeamStanding.getLeagueId();
////    StandingDao standingDao = new StandingDaoImpl();
////    outdatedTable = standingDao.getOrderedTableByLeagueId(leagueId);
//
//    updateTableStanding(homeTeamStanding);
//    updateTableStanding(awayTeamStanding);
//
//    StandingComparator.orderTableByPoints(outdatedTable);
//  }
//
//  private void updateTableStanding(Standing standingToUpdate) {
//    for (int i = 0; i <= outdatedTable.size(); i++) {
//      if (outdatedTable.get(i).getFootballTeamId().equals(standingToUpdate.getFootballTeamId())) {
//        outdatedTable.set(i, standingToUpdate);
//        return;
//      }
//    }
//  }
//
//  private void persistUpdatedTable() {
////    StandingDao standingDao = new StandingDaoImpl();
//    for (Standing standing : outdatedTable) {
//      standingDao.updateStandingRecord(standing);
//    }
//  }
}
