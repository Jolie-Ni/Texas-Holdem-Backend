package com.texasholdem.Repository;

import com.texasholdem.Entity.Strategy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StrategyRepository extends MongoRepository<Strategy, String>{

}
