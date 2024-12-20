package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.FootballTeamDao;
import com.elliot.footballmanager.entity.FootballTeam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;

/**
 * @author Elliot
 */
public class FootballTeamDaoImpl implements FootballTeamDao {

  @Override
  public ArrayList<FootballTeam> getAllFootballTeams() {
    ArrayList<FootballTeam> footballTeams = new ArrayList<FootballTeam>();
    String query = "SELECT DISTINCT team_id, team_name, league_id, home_stadium, club_worth_eur FROM TEAM";

    try (Connection conn = SqliteDatabaseConnector.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        FootballTeam footballTeam = new FootballTeam(rs.getInt("team_id"),
                rs.getString("team_name"),
                rs.getInt("league_id"), "PLACEHOLDER LOCATION",
                rs.getString("home_stadium"), 0, rs.getInt("club_worth_eur"));
        footballTeams.add(footballTeam);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return footballTeams;
  }

  @Override
  public ArrayList<FootballTeam> getAllFootballTeams(Integer leagueId) {
    ArrayList<FootballTeam> footballTeams = new ArrayList<FootballTeam>();
    String query = "SELECT team_id, team_name, league_id, home_stadium, club_worth_eur FROM TEAM WHERE league_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setInt(1, leagueId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        FootballTeam footballTeam = new FootballTeam(rs.getInt("team_id"),
            rs.getString("team_name"),
            rs.getInt("league_id"), "PLACEHOLDER LOCATION",
            rs.getString("home_stadium"), 0, rs.getInt("club_worth_eur"));
        footballTeams.add(footballTeam);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return footballTeams;
  }

  @Override
  public FootballTeam getFootballTeamById(Integer footballTeamId) {
    String query = "SELECT team_id, team_name, league_id, home_stadium, club_worth_eur FROM TEAM WHERE team_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, footballTeamId);

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }
      return new FootballTeam(rs.getInt("team_id"),
              rs.getString("team_name"),
              rs.getInt("league_id"), "PLACEHOLDER LOCATION",
              rs.getString("home_stadium"), 0, rs.getInt("club_worth_eur"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public FootballTeam getFootballTeamByName(String footballTeamName) {
    String query = "SELECT team_id, team_name, league_id, home_stadium, club_worth_eur FROM TEAM WHERE team_name LIKE ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, footballTeamName);

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }
      return new FootballTeam(rs.getInt("team_id"),
              rs.getString("team_name"),
              rs.getInt("league_id"), "PLACEHOLDER LOCATION",
              rs.getString("home_stadium"), 0, rs.getInt("club_worth_eur"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
