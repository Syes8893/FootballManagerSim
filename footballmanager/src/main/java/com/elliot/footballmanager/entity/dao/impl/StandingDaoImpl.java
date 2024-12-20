package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.StandingDao;
import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.FootballTeam;

import com.elliot.footballmanager.entity.Standing;
import com.elliot.footballmanager.standings.StandingComparator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Elliot
 */
public class StandingDaoImpl implements StandingDao {

  @Override
  public void createNewStandingForFootballTeams(List<FootballTeam> footballTeams) {
    try (Connection conn = SqliteDatabaseConnector.connect()) {
      conn.createStatement().execute("DELETE FROM STANDING");
      Statement statement = conn.createStatement();
      for(FootballTeam footballTeam : footballTeams){
        statement.addBatch("INSERT INTO STANDING (LEAGUE_ID, FOOTBALL_TEAM_ID, FOOTBALL_TEAM_NAME)"
              + " VALUES (" + footballTeam.getLeagueId() + ", " + footballTeam.getFootballTeamId() + ", '" + footballTeam.getTeamName().replace("'", "''") + "')");
      }
      statement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void updateStandingRecords(Collection<Standing> standings) {
    try (Connection conn = SqliteDatabaseConnector.connect()) {
      Statement statement = conn.createStatement();
      for(Standing standing : standings){
        statement.addBatch("UPDATE STANDING SET LEAGUE_ID = " + standing.getLeagueId()
                + ", FOOTBALL_TEAM_ID = " + standing.getFootballTeamId() + ", FOOTBALL_TEAM_NAME = '" + standing.getFootballTeamName()
                + "', WINS = " + standing.getWins() + ", LOSSES = " + standing.getLosses() + "," +
                " DRAWS = " + standing.getDraws() + ", GOALS_FOR = " + standing.getGoalsFor()
                + ", GOALS_AGAINST = " + standing.getGoalsAgainst() + ", GOAL_DIFFERENCE = " + standing.getGoalDifference() + "," +
                " POINTS = " + standing.getPoints() + ", GAMES_PLAYED = " + standing.getGamesPlayed() + " WHERE FOOTBALL_TEAM_ID = " + standing.getFootballTeamId());

      }
      statement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Standing getStandingByFootballTeamId(Integer footballTeamId) {
    String query = "SELECT * FROM STANDING WHERE FOOTBALL_TEAM_ID = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, footballTeamId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }

      return buildStandingObject(rs);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Standing> getOrderedTableByLeagueId(Integer leagueId) {
    List<Standing> table = new ArrayList<Standing>();
    String query = "SELECT * FROM STANDING WHERE LEAGUE_ID = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, leagueId);
      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return table;
      }

      while (rs.next()) {
        table.add(buildStandingObject(rs));
      }

      orderTableByPoints(table);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return table;
  }

  private Standing buildStandingObject(ResultSet rs) throws SQLException {
    return new Standing(rs.getInt("STANDING_ID"), rs.getInt("LEAGUE_ID"),
        rs.getInt("FOOTBALL_TEAM_ID"), rs.getString("FOOTBALL_TEAM_NAME"), rs.getInt("WINS"),
        rs.getInt("LOSSES"), rs.getInt("DRAWS"), rs.getInt("GOALS_FOR"), rs.getInt("GOALS_AGAINST"),
        rs.getInt("GOAL_DIFFERENCE"), rs.getInt("POINTS"), rs.getInt("TABLE_POSITION"),
        rs.getInt("GAMES_PLAYED"));
  }

  private List<Standing> orderTableByPoints(List<Standing> table) {
    return StandingComparator.orderTableByPoints(table);
  }
}
