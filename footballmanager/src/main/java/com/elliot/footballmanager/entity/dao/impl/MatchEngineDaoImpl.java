package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.MatchEngineDao;
import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.match.MatchResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Elliot
 */
public class MatchEngineDaoImpl implements MatchEngineDao {

  @Override
  public void persistResultsToDatabase(Collection<MatchResult> matchResults) {
    try (Connection conn = SqliteDatabaseConnector.connect()) {
      Statement statement = conn.createStatement();
      for(MatchResult matchResult : matchResults){
        statement.addBatch("INSERT INTO MATCH_RESULT (FIXTURE_ID, HOME_TEAM_GOALS, AWAY_TEAM_GOALS, MATCH_RESULT, HOME_TEAM_SHOOTOUT, AWAY_TEAM_SHOOTOUT) VALUES"
        + " ('" + matchResult.getFixture() + "'," + matchResult.getHomeTeamMatchStats().getGoals() + ", " + matchResult.getAwayTeamMatchStats().getGoals()
        + ", '" + matchResult.getResult() + "', " + matchResult.getHomeTeamMatchStats().getPenaltyShootout() + ", " + matchResult.getAwayTeamMatchStats().getPenaltyShootout() + ")");
      }
      statement.executeBatch();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
