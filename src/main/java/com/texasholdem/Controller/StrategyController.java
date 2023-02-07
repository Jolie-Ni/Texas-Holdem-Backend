package com.texasholdem.Controller;

import com.texasholdem.Entity.Strategy;
import com.texasholdem.Service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;
    @PutMapping("/save")
    public void save(@RequestBody Strategy strategy) {
        strategyService.saveStrategy(strategy);
    }

    @GetMapping("/getall")
    public List<Strategy> getAll(){
        return strategyService.getAll();
    }

    @GetMapping("/get")
    public Strategy get(@RequestParam("gamestate") String gameState){
        if(strategyService.getStrategyWithGameState(gameState).isPresent()){
            return strategyService.getStrategyWithGameState(gameState).get();
        }else{
            Strategy strategy = new Strategy(gameState, 0.5, 0.0, 0.5);
            return strategy;
        }
    }

}
