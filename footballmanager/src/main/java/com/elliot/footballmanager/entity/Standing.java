package com.elliot.footballmanager.entity;

/**
 * Represents a FootballTeams current standing in a league table.
 *
 * @author Elliot
 */
public class Standing {

  private int standingId;
  private int leagueId;
  private int footballTeamId;
  private String footballTeamName;
  private int wins;
  private int losses;
  private int draws;
  private int goalsFor;
  private int goalsAgainst;
  private int goalDifference;
  private int points;
  private int tablePosition;
  private int gamesPlayed;

  public Standing(FootballTeam footballTeam) {
    this.footballTeamName = footballTeam.getTeamName();
    this.footballTeamId = footballTeam.getFootballTeamId();
    this.leagueId = footballTeam.getLeagueId();
    this.standingId = 0;
  }

  public Standing(int standingId, int leagueId, int footballTeamId, String footballTeamName, int wins,
      int losses, int draws, int goalsFor, int goalsAgainst,
      int goalDifference, int points, int tablePosition, int gamesPlayed) {
    this.standingId = standingId;
    this.leagueId = leagueId;
    this.footballTeamId = footballTeamId;
    this.footballTeamName = footballTeamName;
    this.wins = wins;
    this.losses = losses;
    this.draws = draws;
    this.goalsFor = goalsFor;
    this.goalsAgainst = goalsAgainst;
    this.goalDifference = goalDifference;
    this.points = points;
    this.tablePosition = tablePosition;
    this.gamesPlayed = gamesPlayed;
  }

  public int getStandingId() {
    return standingId;
  }

  public void setStandingId(int standingId) {
    this.standingId = standingId;
  }

  public int getLeagueId() {
    return leagueId;
  }

  public void setLeagueId(int leagueId) {
    this.leagueId = leagueId;
  }

  public int getFootballTeamId() {
    return footballTeamId;
  }

  public void setFootballTeamId(int footballTeamId) {
    this.footballTeamId = footballTeamId;
  }

  public String getFootballTeamName() {
    return footballTeamName;
  }

  public void setFootballTeamName(String footballTeamName) {
    this.footballTeamName = footballTeamName;
  }

  public int getWins() {
    return wins;
  }

  public void setWins(int wins) {
    this.wins = wins;
  }

  public int getLosses() {
    return losses;
  }

  public void setLosses(int losses) {
    this.losses = losses;
  }

  public int getDraws() {
    return draws;
  }

  public void setDraws(int draws) {
    this.draws = draws;
  }

  public int getGoalsFor() {
    return goalsFor;
  }

  public void setGoalsFor(int goalsFor) {
    this.goalsFor = goalsFor;
  }

  public int getGoalsAgainst() {
    return goalsAgainst;
  }

  public void setGoalsAgainst(int goalsAgainst) {
    this.goalsAgainst = goalsAgainst;
  }

  public int getGoalDifference() {
    return goalDifference;
  }

  public void setGoalDifference(int goalDifference) {
    this.goalDifference = goalDifference;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public int getTablePosition() {
    return tablePosition;
  }

  public void setTablePosition(int tablePosition) {
    this.tablePosition = tablePosition;
  }

  public int getGamesPlayed() {
    return gamesPlayed;
  }

  public void setGamesPlayed(int gamesPlayed) {
    this.gamesPlayed = gamesPlayed;
  }
}
