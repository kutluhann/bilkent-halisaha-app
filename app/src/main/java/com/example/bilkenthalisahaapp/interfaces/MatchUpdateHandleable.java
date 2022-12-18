package com.example.bilkenthalisahaapp.interfaces;

import com.example.bilkenthalisahaapp.appObjects.Match;
import com.example.bilkenthalisahaapp.appObjects.Player;

import java.util.HashMap;

public interface MatchUpdateHandleable {

    public void handleDataUpdate();
    public void setMatch(Match match);
    public void fetchUsers();
    public void setPositionMap(HashMap<Integer, Player> positionMap);
    public void updateButtonVisibilities();
    public void handleMatchRemove();

}
