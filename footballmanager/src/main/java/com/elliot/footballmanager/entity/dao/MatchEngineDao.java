package com.elliot.footballmanager.entity.dao;

import com.elliot.footballmanager.match.MatchResult;

import java.util.Collection;
import java.util.List;

/**
 * Outlays the interactivity with the database
 *
 * @author Elliot
 */
public interface MatchEngineDao {

  public void persistResultsToDatabase(Collection<MatchResult> matchResults);

}
