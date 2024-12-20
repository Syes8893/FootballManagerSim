package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.ManagerDao;
import com.elliot.footballmanager.entity.Manager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.dao.FootballTeamDao;

/**
 * @author Elliot
 */
public class ManagerDaoImpl implements ManagerDao {

  @Override
  public void insertIntoManagerTable(Manager manager) {
    String query = "INSERT INTO MANAGER VALUES (?, ?, ?, ?, ?, ?)";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(2, manager.getFirstName());
      pstmt.setString(3, manager.getLastName());
      pstmt.setInt(4, manager.getCurrentFootballTeam().getFootballTeamId());
      pstmt.setInt(5, manager.getCountryId());
      pstmt.setInt(6, manager.getTransferBudget());

      // If count != 1 the statement did not successfully persist the Manager data into the database
      if (pstmt.executeUpdate() != 1) {
        throw new SQLException("The Manager was not successfully inserted into the database!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void updateManagerInTable(Manager manager) {
    String query = "UPDATE MANAGER SET ASSIGNED_FOOTBALL_CLUB = ?, COUNTRY_ID = ?, TRANSFER_BUDGET = ? WHERE MANAGER_ID = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, manager.getCurrentFootballTeam().getFootballTeamId());
      pstmt.setInt(2, manager.getCountryId());
      pstmt.setInt(3, manager.getManagerId());
      pstmt.setInt(3, manager.getTransferBudget());

      // If count != 1 the statement did not successfully persist the Manager data into the database
      if (pstmt.executeUpdate() != 1) {
        throw new SQLException("The Manager was not successfully inserted into the database!");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Manager getManagerById(Integer managerId) {
    String query = "SELECT * FROM MANAGER WHERE MANAGER_ID = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, managerId);

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }

      FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
      FootballTeam footballTeam = footballTeamDao
          .getFootballTeamById(rs.getInt("ASSIGNED_FOOTBALL_CLUB"));

      return new Manager(rs.getInt("MANAGER_ID"), rs.getString("FIRST_NAME"),
          rs.getString("LAST_NAME"),
          footballTeam, rs.getInt("COUNTRY_ID"), rs.getInt("TRANSFER_BUDGET"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

}
