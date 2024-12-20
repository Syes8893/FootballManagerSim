package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.LeagueDao;
import com.elliot.footballmanager.entity.League;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;

/**
 * @author Elliot
 */
public class LeagueDaoImpl implements LeagueDao {

  public List<League> getAllLeagues() {
    String query = "SELECT league_id, league_name FROM LEAGUE";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      ResultSet rs = pstmt.executeQuery();

      List<League> allLeagues = new ArrayList<League>();
      while (rs.next()) {
        League league = new League(rs.getInt("league_id"), rs.getString("league_name"),
            -1);
        allLeagues.add(league);
      }
      return allLeagues;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<League> getAllLeaguesById(Integer countryId, boolean interlands) {
    List<League> allLeagues = new ArrayList<>();
    String query = "SELECT DISTINCT league_id, league_name FROM TEAM WHERE nationality_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, countryId);
      ResultSet rs = pstmt.executeQuery();

      while (rs.next()) {
        if(!interlands && rs.getInt("league_id") == 78)
          continue;
        League league = new League(rs.getInt("league_id"), rs.getString("league_name"),
            countryId);
        allLeagues.add(league);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return allLeagues;
  }

  public League getLeagueById(Integer leagueId) {
    String query = "SELECT league_id, league_name FROM LEAGUE WHERE league_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, leagueId);

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }
      return new League(leagueId, rs.getString("league_name"),
          0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
