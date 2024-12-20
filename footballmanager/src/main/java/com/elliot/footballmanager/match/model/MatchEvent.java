package com.elliot.footballmanager.match.model;

import com.elliot.footballmanager.match.engine.MatchEngine;
import com.elliot.footballmanager.match.engine.MatchEngineMediator;
import com.elliot.footballmanager.match.engine.ShotConstants;

/**
 * @author Elliot
 */
public abstract class MatchEvent {

  protected void doesEventNeedToBeLogged() {
    if(!MatchEngine.isLoggingGameEvents())
      return;
    if((this instanceof Tackle && ((Tackle) this).getTackleOutcome() == 0) || this instanceof Movement || this instanceof Pass){
      System.out.print("\r" + getCurrentGameTime());
      return;
    }
    System.out.print("\r");
    outputMatchEventString(buildMatchEventString());
    System.out.print(getCurrentGameTime());
  }

  protected String getCurrentGameTime() {
    return "[" + ((int)MatchEngine.getCurrentTimeInGame()+1) + "\']";
//    return "[" + ((int)MatchEngine.getCurrentTimeInGame()) + ":" + String.format("%02d", ((int)((MatchEngine.getCurrentTimeInGame()%1)*60))) + "]";
  }

  protected abstract String buildMatchEventString();

  protected void outputMatchEventString(String message) {
    MatchEngineMediator.printMessageToConsole(message);
  }
}
