package com.texasholdem.Service;

import com.texasholdem.Entity.Strategy;
import com.texasholdem.Repository.StrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import handranking.Card;
import handranking.Hand;
import handranking.RankableHand;
import handranking.util.HandRanker;
import handranking.util.HandRankingException;


@Service
public class StrategyServiceImpl implements StrategyService{

    // Convert a list of cards represented in integer to a hashset of Cards object
    // prepare for input to hand evaluator
    private HashSet<Card> convertToCard(ArrayList<Integer> arr) {
        HashSet<Card> res = new HashSet<>();
        for (int i : arr) {
            int rank = i / 4 + 2;
            int suit;
            if (i % 4 == 0)
                suit = Card.SUIT_SPADES;
            else if (i % 4 == 1)
                suit = Card.SUIT_HEARTS;
            else if (i % 4 == 2)
                suit = Card.SUIT_CLUBS;
            else
                suit = Card.SUIT_DIAMONDS;
            res.add(new Card(suit, rank));
        }
        return res;
    }

    private Strategy oddsCalculator(String gameState, int num_iter, boolean allowCheck) {
        // split up gameState into hole cards, community cards
        String[] cards = gameState.split(";");
        // hole cards, 2 char per card
        assert cards[0].length() == 4 : "number of hole cards must be 2";
        assert cards.length <= 1 || cards[1].length() == 0 || (cards[1].length() >= 6 && cards[1].length() <= 10) : "number of community cards can either be 0 or 3 to 5";
        ArrayList<Integer> holeCards = new ArrayList<>();
        try {
            holeCards.add(Integer.parseInt(cards[0].substring(0, 2)));
            holeCards.add(Integer.parseInt(cards[0].substring(2, 4)));
        } catch (NumberFormatException e) {
            System.out.println("String: " + cards[0] + " can not be converted to int" );
        }

        ArrayList<Integer> communityCards = new ArrayList<>();
        if (cards.length > 1) {
            try {
                while (cards[1].length() > 0) {
                    assert cards[1].length() % 2 == 0 : "invalid community card string: " + cards[1];
                    communityCards.add(Integer.parseInt(cards[1].substring(0, 2)));
                    cards[1] = cards[1].substring(2);
                }
            } catch (NumberFormatException e) {
                System.out.println("String: " + cards[1] + " can not be converted to int");
            }
        }

        // find all the unused cards
        ArrayList<Integer> pool = new ArrayList<>();
        for (int i = 0; i < 52; i ++) {
            if (!holeCards.contains(i) && !communityCards.contains(i))
                pool.add(i);
        }
        int cnt = 0, bet = 0, fold = 0, check = 0;
        for (int i = 0; i < num_iter; i ++) {
            int curSituation = 0; // -1 lose, 0 draw, 1 win
            int finalSituation = 0;
            ArrayList<Integer> myHand = new ArrayList<>(communityCards);
            ArrayList<Integer> oppHand = new ArrayList<>(communityCards);
            myHand.addAll(holeCards);
            Collections.shuffle(pool);
            // opponent hole cards
            oppHand.add(pool.get(0));
            oppHand.add(pool.get(1));
            if (communityCards.size() > 0 && communityCards.size() < 5) {
                assert myHand.size() == oppHand.size() : String.format("myHand has size = %d, oppHand has size = %d", myHand.size(), oppHand.size());
                try {
                    Hand myRankableHand = new RankableHand(convertToCard(myHand));
                    Hand oppRankableHand = new RankableHand(convertToCard(oppHand));
                    int myScore = myRankableHand.getHandValue();
                    int oppScore = oppRankableHand.getHandValue();
                    if (myScore > oppScore)
                        curSituation = 1;
                    else if (myScore < oppScore)
                        curSituation = -1;
                } catch (Exception e) {
                    System.out.println("Can not convert cards to rankableHand");
                }
            }
            // get all 5 community cards
            for (int k = 0; k < 7 - myHand.size(); k ++) {
                myHand.add(pool.get(k + 2));
                oppHand.add(pool.get(k + 2));
            }
            try {
                Hand myRankableHand = new RankableHand(convertToCard(myHand));
                Hand oppRankableHand = new RankableHand(convertToCard(oppHand));
                int myScore = myRankableHand.getHandValue();
                int oppScore = oppRankableHand.getHandValue();
                if (myScore > oppScore) {
                    finalSituation = 1;
                }
                else if (myScore < oppScore) {
                    finalSituation = -1;
                }
                if (curSituation == 1) {
                    if (finalSituation == 1)
                        bet ++;
                    else
                        check ++;
                }
                else if (curSituation == -1) {
                    if (finalSituation == 1)
                        check ++;
                    else
                        fold ++;
                }
                else if (communityCards.size() < 5) {
                    if (finalSituation == 1)
                        check ++;
                    else
                        fold ++;
                }
                else if (communityCards.size() == 5) {
                    if (finalSituation == 1)
                        bet ++;
                    else
                        fold ++;
                }
                cnt ++;
            } catch (Exception e) {
                System.out.println("Can not convert cards to rankableHand");
            }
        }
        System.out.println(String.format("Odds_calculator sampling results: bet=%d, check=%d, fold=%d, total_sampling_count=%d\n", bet, check, fold, cnt));
        double b = bet / (double) cnt;
        double f  = fold / (double) cnt;
        double c = check / (double) cnt;
        if (!allowCheck) {
            b += c;
            c = 0;
        }
        if (communityCards.size() == 0) {
            if (b >= 0.5) {
                b = 1; f = 0; c = 0;
            }
            else if (b > 0.4) {
                b = 0.9; f = 0.1; c = 0;
            }
            else if (b > 0.3) {
                b = 0.85; f = 0.15; c = 0;
            }
        }
        return new Strategy(gameState, f, c, b);
    }

    @Autowired
    private StrategyRepository strategyRepository;

    public List<Strategy> getAll(){
        return strategyRepository.findAll();
    }

    public Optional<Strategy> getStrategyWithGameState(String gameState){
        if (strategyRepository.existsById(gameState))
            return strategyRepository.findById(gameState);
        else
            return Optional.of(oddsCalculator(gameState, 10000, true));
    }

    public void saveStrategy(Strategy strategy){
        strategyRepository.save(strategy);
    }
}
