package com.texasholdem.Service;

import com.texasholdem.Entity.Strategy;
import com.texasholdem.Repository.StrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StrategyServiceImpl implements StrategyService{
    @Autowired
    private StrategyRepository strategyRepository;

    public List<Strategy> getAll(){
        return strategyRepository.findAll();
    }

    public Optional<Strategy> getStrategyWithGameState(String gameState){
        return strategyRepository.findById(gameState);
    }

    public void saveStrategy(Strategy strategy){
        strategyRepository.save(strategy);
    }
}
