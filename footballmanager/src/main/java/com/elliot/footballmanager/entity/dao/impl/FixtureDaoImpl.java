package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.GameManager;
import com.elliot.footballmanager.entity.dao.FixtureDao;
import com.elliot.footballmanager.entity.Fixture;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.elliot.footballmanager.DateUtils;
import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.dao.FootballTeamDao;

public class FixtureDaoImpl implements FixtureDao {

  @Override
  public void insertFixturesIntoDatabase(List<Fixture> allFixtures) {
    try (Connection conn = SqliteDatabaseConnector.connect()) {
      conn.createStatement().execute("DELETE FROM FIXTURE");
      Statement stmt = conn.createStatement();
      int index = 0;
      for (Fixture fixture : allFixtures) {
//        System.out.println(index++);
//        System.out.println(fixture.getFixtureId());
//        System.out.println(fixture.getHomeTeam().getTeamName());
//        System.out.println(fixture.getAwayTeam().getTeamName());
//        System.out.println(fixture.getDateOfFixture());
//        System.out.println(fixture.getLeagueId());
//        System.out.println(fixture.getCupLevel());
        stmt.addBatch("INSERT INTO FIXTURE (FIXTURE_UUID, HOME_TEAM, AWAY_TEAM, DATE_OF_MATCH, LEAGUE_ID, CUP_LEVEL) "
          + "VALUES ('" + fixture.getFixtureId() + "', '" + fixture.getHomeTeam().getTeamName().replace("'", "''") + "', '" + fixture.getAwayTeam().getTeamName().replace("'", "''") + "'"
          + ", '" + DateUtils.FIXTURE_DATE_FORMAT.format(fixture.getDateOfFixture()) + "', " + fixture.getLeagueId()
          + ", " + fixture.getCupLevel() + ")");
      }
      stmt.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Queue<Fixture> getFootballTeamsUpcomingFixtures(FootballTeam footballTeam, Date currentDate) {
    String query = "SELECT * FROM FIXTURE WHERE HOME_TEAM = ? OR AWAY_TEAM = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      if (footballTeam == null) {
        return new LinkedList<Fixture>();
      }

      pstmt.setString(1, footballTeam.getTeamName());
      pstmt.setString(2, footballTeam.getTeamName());

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return new LinkedList<Fixture>();
      }

      ArrayList<Fixture> nextFixtures = new ArrayList<>();
      FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
      while (rs.next()) {
        FootballTeam homeTeam = footballTeamDao.getFootballTeamByName(rs.getString("HOME_TEAM"));
        FootballTeam awayTeam = footballTeamDao.getFootballTeamByName(rs.getString("AWAY_TEAM"));

        String dateString = rs.getString("DATE_OF_MATCH");
        Date date = DateUtils.FIXTURE_DATE_FORMAT.parse(dateString);
        //Check if fixture date is after current date
        if(date.after(currentDate))
           nextFixtures.add(new Fixture(UUID.fromString(rs.getString("FIXTURE_ID")), homeTeam, awayTeam,
              date, rs.getInt("LEAGUE_ID"), rs.getInt("CUP_LEVEL")));
      }
      nextFixtures.sort(Comparator.comparing(Fixture::getDateOfFixture));
      Queue<Fixture> upcomingFixtures = new LinkedList<>();
      for(Fixture f : nextFixtures)
        upcomingFixtures.offer(f);
      return upcomingFixtures;
    } catch (SQLException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return new LinkedList<>();
  }

  @Override
  public List<Fixture> getFixturesForGivenDate(Date date) {
    List<Fixture> fixtures = new ArrayList<Fixture>();

    String query = "SELECT * FROM FIXTURE WHERE DATE_OF_MATCH = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {

      pstmt.setString(1, DateUtils.FIXTURE_DATE_FORMAT.format(date));

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return fixtures;
      }

      FootballTeamDao footballTeamDao = new FootballTeamDaoImpl();
      while (rs.next()) {
        FootballTeam homeTeam = footballTeamDao.getFootballTeamByName(rs.getString("HOME_TEAM"));
        FootballTeam awayTeam = footballTeamDao.getFootballTeamByName(rs.getString("AWAY_TEAM"));

        fixtures.add(new Fixture(UUID.fromString(rs.getString("FIXTURE_ID")), homeTeam, awayTeam,
            date, rs.getInt("LEAGUE_ID"), rs.getInt("CUP_LEVEL")));
      }


    } catch (SQLException e) {
      e.printStackTrace();
    }
    return fixtures;
  }
}
