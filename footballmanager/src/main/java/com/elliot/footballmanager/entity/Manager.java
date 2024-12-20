package com.elliot.footballmanager.entity;

import com.elliot.footballmanager.ColorUtils;
import com.elliot.footballmanager.entity.FootballTeam;

/**
 * The Manager object is used to represent the person playing the game. Stores information about the
 * current <link>FootballTeam</link> the Manager is assigned to.
 *
 * @author Elliot
 */
public class Manager {

  private Integer managerId;
  private String firstName;
  private String lastName;
  private FootballTeam currentFootballTeam;
  private int countryId;
  private int transferBudget;

  public Manager() {

  }

  public Manager(Integer managerId, String firstName, String lastName,
      FootballTeam currentFootballTeam, int countryId, int transferBudget) {
    this.setManagerId(managerId);
    this.firstName = firstName;
    this.lastName = lastName;
    this.currentFootballTeam = currentFootballTeam;
    this.countryId = countryId;
    this.transferBudget = transferBudget;
  }

  public Integer getManagerId() {
    return managerId;
  }

  public void setManagerId(Integer managerId) {
    this.managerId = managerId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public FootballTeam getCurrentFootballTeam() {
    return currentFootballTeam;
  }

  public void setCurrentFootballTeam(FootballTeam currentFootballTeam) {
    this.currentFootballTeam = currentFootballTeam;
  }

  public int getCountryId() {
    return countryId;
  }

  public void setCountryId(int countryId) {
    this.countryId = countryId;
  }

  public int getTransferBudget() {
    return transferBudget;
  }

  public void setTransferBudget(int transferBudget) {
    this.transferBudget = transferBudget;
  }
}
