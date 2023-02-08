package com.texasholdem.Controller;

import com.texasholdem.Entity.Strategy;
import com.texasholdem.Service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        Optional<Strategy> strategy = strategyService.getStrategyWithGameState(gameState);
        return strategy.orElse(null);
    }

}
