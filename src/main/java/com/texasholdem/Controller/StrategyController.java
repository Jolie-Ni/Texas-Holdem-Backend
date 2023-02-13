package com.texasholdem.Controller;

import com.texasholdem.Entity.ErrorResponse;
import com.texasholdem.Entity.Strategy;
import com.texasholdem.Service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/strategy")
public class StrategyController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse err = new ErrorResponse();
        err.setErrorCode(-1);
        err.setMsg(ex.getMessage());
        return new ResponseEntity<>(err, HttpStatus.I_AM_A_TEAPOT);
    }
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
    public Strategy get(@RequestParam("gamestate") String gameState, @RequestParam("allowCheck") boolean allowCheck) throws Exception{
        Optional<Strategy> strategy = strategyService.getStrategyWithGameState(gameState, allowCheck);
        if (!strategy.isPresent()) throw new Exception("Strategy should not be null");
        return strategy.get();
    }
}

