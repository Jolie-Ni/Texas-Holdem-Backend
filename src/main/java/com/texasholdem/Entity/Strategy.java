package com.texasholdem.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
public class Strategy {
    @Id
    private String gameState;
    private Double fold;
    private Double check;
    private Double bet;
}
