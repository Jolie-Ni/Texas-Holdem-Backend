package com.texasholdem.Service;

import com.texasholdem.Entity.Strategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface StrategyService {
    public List<Strategy> getAll();
    public Optional<Strategy> getStrategyWithGameState(String gameState);

    public void saveStrategy(Strategy strategy);
}
