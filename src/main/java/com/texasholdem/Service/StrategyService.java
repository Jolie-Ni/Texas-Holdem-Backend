package com.texasholdem.Service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.texasholdem.Entity.Strategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface StrategyService {
    public List<Strategy> getAll();
    public Optional<Strategy> getStrategyWithGameState(String gameState, boolean allow_check);

    public void saveStrategy(Strategy strategy);
}
