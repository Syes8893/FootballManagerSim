package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.CountryDao;
import com.elliot.footballmanager.entity.Country;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;

/**
 * Implementation of the <link>CountryDao</link> class.
 *
 * @author Elliot
 */
public class CountryDaoImpl implements CountryDao {

  public List<Country> getAllCountries() {
    List<Country> allCountries = new ArrayList<>();
    String query = "SELECT nationality_id, nationality_name FROM COUNTRY";

    try (Connection conn = SqliteDatabaseConnector.connect();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

      while (rs.next()) {
        Country country = new Country(rs.getInt("nationality_id"), rs.getString("nationality_name"));
        allCountries.add(country);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return allCountries;
  }

  @Override
  public Country getCountryById(Integer countryId) {
    String query = "SELECT nationality_name FROM COUNTRY WHERE nationality_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, countryId);

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return null;
      }

      return new Country(countryId, rs.getString("nationality_name"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

}
