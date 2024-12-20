package com.elliot.footballmanager.entity;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.match.model.Football;
import com.elliot.footballmanager.match.model.Position;
import java.io.Serializable;
import java.util.Set;
import java.util.Stack;

import com.elliot.footballmanager.match.model.Movement;
import com.elliot.footballmanager.entity.attributes.GoalkeeperAttributes;
import com.elliot.footballmanager.entity.attributes.MentalAttributes;
import com.elliot.footballmanager.entity.attributes.PhysicalAttributes;
import com.elliot.footballmanager.entity.attributes.TechnicalAttributes;

/**
 * Provides information about a given Player object and stores information about how good they are
 * via the different attributes.
 *
 * @author Elliot
 */
public class Player implements Serializable {

  private Integer id;
  private String name, fullName;
  private Integer age;
  private String nationality;
  private Integer overallRating;
  private FootballTeam currentClub;
  private Double value;
  private Double wages;
  private Set<Position> preferredPositions;

  private PhysicalAttributes physicalAttributes;
  private TechnicalAttributes technicalAttributes;
  private MentalAttributes mentalAttributes;
  private GoalkeeperAttributes goalkeeperAttributes;

  private Integer preferredXCoordinate;
  private Integer preferredYCoordinate;

  private Integer currentXCoordinate;
  private Integer currentYCoordinate;

  private Stack<Movement.Direction> directionsBackToPreferredCoordinates = new Stack<Movement.Direction>();
  private Integer[] opposingTeamsGoal = new Integer[2];

  public Integer gameTicksUntilRecoveredFromTackle = 0;

  public Player() {

  }

  public Player(Integer id, String name, String fullName, Integer age, String nationality, Integer overallRating,
      FootballTeam currentClub, Double value, Double wages, Set<Position> preferredPositions) {
    this.id = id;
    this.fullName = fullName;
    this.name = name;
    this.age = age;
    this.nationality = nationality;
    this.overallRating = overallRating;
    this.currentClub = currentClub;
    this.value = value;
    this.wages = wages;
    this.preferredPositions = preferredPositions;
  }

  public Player(Integer id, String name, Integer age, String nationality, Integer overallRating,
      FootballTeam currentClub, Double value, Double wages, Set<Position> preferredPositions,
      PhysicalAttributes physicalAttributes, TechnicalAttributes technicalAttributes,
      MentalAttributes mentalAttributes, GoalkeeperAttributes goalkeeperAttributes) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.nationality = nationality;
    this.overallRating = overallRating;
    this.currentClub = currentClub;
    this.value = value;
    this.wages = wages;
    this.preferredPositions = preferredPositions;
    this.physicalAttributes = physicalAttributes;
    this.technicalAttributes = technicalAttributes;
    this.mentalAttributes = mentalAttributes;
    this.goalkeeperAttributes = goalkeeperAttributes;
  }

  public void setBothPreferredAndCurrentXCoordinate(Integer xCoordinate) {
    this.setPreferredXCoordinate(xCoordinate);
    this.setCurrentXCoordinate(xCoordinate);
  }

  public void setBothPreferredAndCurrentYCoordinate(Integer yCoordinate) {
    this.setPreferredYCoordinate(yCoordinate);
    this.setCurrentYCoordinate(yCoordinate);
  }

  public void addDirectionBackToPrefferedCoordinates(Movement.Direction direction) {
    this.getDirectionsBackToPreferredCoordinates().push(direction);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public String getNameAndOverall() {
    return name + " (" + overallRating + ")";
  }
  public String getNameAndOverallAndPosition() {
    return name + " (" + overallRating + ") " + ColorUtils.YELLOW_BOLD + preferredPositions + ColorUtils.RESET;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public Integer getOverallRating() {
    return overallRating;
  }

  public void setOverallRating(Integer overallRating) {
    this.overallRating = overallRating;
  }

  public FootballTeam getCurrentClub() {
    return currentClub;
  }

  public void setCurrentClub(FootballTeam currentClub) {
    this.currentClub = currentClub;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Double getWages() {
    return wages;
  }

  public void setWages(Double wages) {
    this.wages = wages;
  }

  public Set<Position> getPreferredPositions() {
    return preferredPositions;
  }

  public void setPreferredPositions(Set<Position> preferredPositions) {
    this.preferredPositions = preferredPositions;
  }

  public PhysicalAttributes getPhysicalAttributes() {
    return physicalAttributes;
  }

  public void setPhysicalAttributes(PhysicalAttributes physicalAttributes) {
    this.physicalAttributes = physicalAttributes;
  }

  public TechnicalAttributes getTechnicalAttributes() {
    return technicalAttributes;
  }

  public void setTechnicalAttributes(TechnicalAttributes technicalAttributes) {
    this.technicalAttributes = technicalAttributes;
  }

  public MentalAttributes getMentalAttributes() {
    return mentalAttributes;
  }

  public void setMentalAttributes(MentalAttributes mentalAttributes) {
    this.mentalAttributes = mentalAttributes;
  }

  public GoalkeeperAttributes getGoalkeeperAttributes() {
    return goalkeeperAttributes;
  }

  public void setGoalkeeperAttributes(GoalkeeperAttributes goalkeeperAttributes) {
    this.goalkeeperAttributes = goalkeeperAttributes;
  }

  public Integer getPreferredXCoordinate() {
    return preferredXCoordinate;
  }

  public void setPreferredXCoordinate(Integer preferredXCoordinate) {
    this.preferredXCoordinate = preferredXCoordinate;
  }

  public Integer getPreferredYCoordinate() {
    return preferredYCoordinate;
  }

  public void setPreferredYCoordinate(Integer preferredYCoordinate) {
    this.preferredYCoordinate = preferredYCoordinate;
  }

  public Integer getCurrentXCoordinate() {
//    if(currentXCoordinate == null){
//      System.out.println(name + " / " + currentClub.getTeamName() + "\n ");
//      for(Player p : currentClub.getMatchSetup().getSelectedFormation().getStartingLineup())
//        System.out.println(p.getName() + " / "+ p.getPreferredPositions() + " / " + p.getCurrentXCoordinate());
//    }
    return currentXCoordinate;
  }

  public void setCurrentXCoordinate(Integer xCoordinate) {
    this.currentXCoordinate = xCoordinate;
  }

  public Integer getCurrentYCoordinate() {
    return currentYCoordinate;
  }

  public void setCurrentYCoordinate(Integer yCoordinate) {
    this.currentYCoordinate = yCoordinate;
  }

  public Stack<Movement.Direction> getDirectionsBackToPreferredCoordinates() {
    return directionsBackToPreferredCoordinates;
  }

  public void setDirectionsBackToPreferredCoordinates(
      Stack<Movement.Direction> directionsBackToPreferredCoordinates) {
    this.directionsBackToPreferredCoordinates = directionsBackToPreferredCoordinates;
  }

  public Integer[] getOpposingTeamsGoal() {
    return opposingTeamsGoal;
  }

  public void setOpposingTeamsGoal(Integer[] opposingTeamsGoal) {
    this.opposingTeamsGoal = opposingTeamsGoal;
  }

  public Integer getGameTicksUntilRecoveredFromTackle() {
    return gameTicksUntilRecoveredFromTackle;
  }

  public void setGameTicksUntilRecoveredFromTackle(Integer gameTicksUntilRecoveredFromTackle) {
    this.gameTicksUntilRecoveredFromTackle = gameTicksUntilRecoveredFromTackle;
  }

  public double distanceToFootball(Football football){
    return Math.sqrt(Math.pow(football.getCurrentXCoordinate()-getCurrentXCoordinate(), 2)
      + Math.pow(football.getCurrentYCoordinate()-getCurrentYCoordinate(), 2));
  }

  public double distanceToPlayer(Player player){
    return Math.sqrt(Math.pow(player.getCurrentXCoordinate()-getCurrentXCoordinate(), 2)
            + Math.pow(player.getCurrentYCoordinate()-getCurrentYCoordinate(), 2));
  }

}
