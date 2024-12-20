package com.elliot.footballmanager.entity.dao.impl;

import com.elliot.footballmanager.entity.dao.PlayerDao;
import com.elliot.footballmanager.entity.Player;
import com.elliot.footballmanager.match.model.Position;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elliot.footballmanager.database.SqliteDatabaseConnector;
import com.elliot.footballmanager.entity.FootballTeam;
import com.elliot.footballmanager.entity.attributes.GoalkeeperAttributes;
import com.elliot.footballmanager.entity.attributes.MentalAttributes;
import com.elliot.footballmanager.entity.attributes.PhysicalAttributes;
import com.elliot.footballmanager.entity.attributes.TechnicalAttributes;

/**
 * @author Elliot
 */
public class PlayerDaoImpl implements PlayerDao {

  @Override
  public List<Player> getAllPlayersForFootballTeam(FootballTeam footballTeam) {
    String query = "SELECT * FROM PLAYER WHERE club_team_id = ?";

    try (Connection conn = SqliteDatabaseConnector.connect();
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setInt(1, footballTeam.getFootballTeamId());

      ResultSet rs = pstmt.executeQuery();

      if (rs.isAfterLast()) {
        return new ArrayList<Player>();
      }

      List<Player> squad = new ArrayList<Player>();
      while (rs.next()) {
        Player player = buildBasePlayerObject(rs, footballTeam);
        squad.add(player);
      }
      return squad;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new ArrayList<Player>();
  }

  private Player buildBasePlayerObject(ResultSet rs, FootballTeam footballTeam)
      throws SQLException {
    Set<Position> positions = getPositionsFromString(rs.getString("player_positions"));

    Player player = new Player(rs.getInt("player_id"), rs.getString("short_name"),
            rs.getString("long_name"), rs.getInt("age"),
        rs.getString("nationality_name"),
        rs.getInt("overall"), footballTeam, rs.getDouble("value_eur"), rs.getDouble("wage_eur"), positions);

    player.setGoalkeeperAttributes(buildGoalkeeperAttributes(rs));
    player.setMentalAttributes(buildMentalAttributes(rs));
    player.setPhysicalAttributes(buildPhysicalAttributes(rs));
    player.setTechnicalAttributes(buildTechnicalAttributes(rs));

    return player;
  }

  private Set<Position> getPositionsFromString(String positionsAsString) {
    String[] parts = positionsAsString.split(", ");

    if (parts.length == 0 || parts == null) {
      return new HashSet<Position>();
    }

    Set<Position> preferredPositions = new HashSet<Position>();
    for (String string : parts) {
      try {
        preferredPositions.add(Position.getPositionFromString(string));
      } catch (IllegalArgumentException exception) {
        exception.printStackTrace();
        preferredPositions.add(Position.CM);
      }
    }
    return preferredPositions;
  }

  private GoalkeeperAttributes buildGoalkeeperAttributes(ResultSet rs) throws SQLException {
    return new GoalkeeperAttributes(rs.getInt("goalkeeping_diving"), rs.getInt("goalkeeping_handling"),
        rs.getInt("goalkeeping_kicking"),
        rs.getInt("goalkeeping_positioning"), rs.getInt("goalkeeping_reflexes"));
  }

  private MentalAttributes buildMentalAttributes(ResultSet rs) throws SQLException {
    return new MentalAttributes(rs.getInt("mentality_positioning"), rs.getInt("mentality_vision"),
        rs.getInt("mentality_composure"),
        rs.getInt("mentality_interceptions"), rs.getInt("mentality_aggression"));
  }

  private PhysicalAttributes buildPhysicalAttributes(ResultSet rs) throws SQLException {
    return new PhysicalAttributes(rs.getInt("movement_acceleration"), rs.getInt("movement_sprint_speed"),
        rs.getInt("movement_agility"),
        rs.getInt("movement_balance"), rs.getInt("movement_reactions"), rs.getInt("power_jumping"),
        rs.getInt("power_stamina"), rs.getInt("power_strength"));
  }

  private TechnicalAttributes buildTechnicalAttributes(ResultSet rs) throws SQLException {
    return new TechnicalAttributes(rs.getInt("attacking_finishing"), rs.getInt("power_long_shots"),
        rs.getInt("mentality_penalties"),
        rs.getInt("power_shot_power"), rs.getInt("attacking_volleys"), rs.getInt("attacking_crossing"),
        rs.getInt("skill_curve"), rs.getInt("skill_fk_accuracy"), rs.getInt("skill_long_passing"),
        rs.getInt("attacking_short_passing"), rs.getInt("skill_ball_control"), rs.getInt("skill_dribbling"),
        rs.getInt("attacking_heading_accuracy"), rs.getInt("defending_marking_awareness"), rs.getInt("defending_sliding_tackle"),
        rs.getInt("defending_standing_tackle"));
  }
}
