package com.elliot.footballmanager.entity;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * The Fixture class provides information about an upcoming Football match. Information about the
 * two FootballTeams, competition and date are stored.
 *
 * @author Elliot
 */
public class Fixture {

  private UUID fixtureId;
  private FootballTeam homeTeam;
  private FootballTeam awayTeam;
  private Date dateOfFixture;
  private Integer leagueId;

  private int cupLevel;

  public Fixture() {

  }

  public Fixture(UUID fixtureId, FootballTeam homeTeam, FootballTeam awayTeam,
                 Date dateOfFixture, Integer leagueId, int cupLevel) {
    this.fixtureId = fixtureId;
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.dateOfFixture = dateOfFixture;
    this.leagueId = leagueId;
    this.cupLevel = cupLevel;
  }

  public Fixture(FootballTeam homeTeam, FootballTeam awayTeam,
      Date dateOfFixture, Integer leagueId, int cupLevel) {
    this.fixtureId = UUID.randomUUID();
    this.homeTeam = homeTeam;
    this.awayTeam = awayTeam;
    this.dateOfFixture = dateOfFixture;
    this.leagueId = leagueId;
    this.cupLevel = cupLevel;
  }

  public UUID getFixtureId() {
    return fixtureId;
  }

  public void setFixtureId(UUID fixtureId) {
    this.fixtureId = fixtureId;
  }

  public FootballTeam getHomeTeam() {
    return homeTeam;
  }

  public void setHomeTeam(FootballTeam homeTeam) {
    this.homeTeam = homeTeam;
  }

  public FootballTeam getAwayTeam() {
    return awayTeam;
  }

  public void setAwayTeam(FootballTeam awayTeam) {
    this.awayTeam = awayTeam;
  }

  public Date getDateOfFixture() {
    return dateOfFixture;
  }

  public void setDateOfFixture(Date dateOfFixture) {
    this.dateOfFixture = dateOfFixture;
  }

  public Integer getLeagueId() {
    return leagueId;
  }

  public void setLeagueId(Integer leagueId) {
    this.leagueId = leagueId;
  }

  public int getCupLevel() {
    return cupLevel;
  }

  public boolean isCup(){
    return cupLevel > -1;
  }

  public void setCupLevel(int cupLevel) {
    this.cupLevel = cupLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Fixture fixture = (Fixture) o;
//    return (this.getHomeTeam().getFootballTeamId() == fixture.getHomeTeam().getFootballTeamId()
//    && this.getAwayTeam().getFootballTeamId() == fixture.getAwayTeam().getFootballTeamId())
//    && this.getDateOfFixture().equals(fixture.getDateOfFixture())
////    && this.getLeagueId().equals(fixture.getLeagueId())
//    && this.getCupLevel() == fixture.getCupLevel();
    //TODO - resolve fixture IDS (make them UUIDS or sum instead of using fixture arraylist size)
    return Objects.equals(fixtureId, fixture.fixtureId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fixtureId, homeTeam, awayTeam, dateOfFixture, leagueId);
  }

  @Override
  public String toString() {
    return "Fixture: [Home Team: " + homeTeam + ", Away Team:" + awayTeam + ", Fixture Date="
        + dateOfFixture + "]";
  }
}
