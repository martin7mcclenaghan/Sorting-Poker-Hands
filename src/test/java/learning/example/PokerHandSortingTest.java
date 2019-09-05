package learning.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PokerHandSortingTest {

    // == static fields ==
    private static List<PokerHand> expected = createOrderedList();

    // == instance fields ==
    private List<PokerHand> randomList;

    // == constructor ==
    public PokerHandSortingTest(List<PokerHand> randomList) {
        this.randomList = randomList;
    }

    // == Parameters ==
    @Parameterized.Parameters
    public static Collection<Object> testConditions (){
        return Arrays.asList((Object []) new Object [][] {

                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},
                {createRandomList()},

        });
    }

    // == private methods ==

    //method returns an ordered list of poker hands which will always be the expected for the test
    private static List<PokerHand> createOrderedList() {
        List<PokerHand> orderedList = new ArrayList<>();
        orderedList.add(new PokerHand("JH AH TH KH QH")); // Royal Flush
        orderedList.add(new PokerHand("8S 6S 5S 7S 4S")); // Straight Flush
        orderedList.add(new PokerHand("2D 6D 3D 4D 5D")); // Straight Flush
        orderedList.add(new PokerHand("AH KH AS AC AD")); // Four of a Kind
        orderedList.add(new PokerHand("AC QH AS AD AH")); // Four of a Kind
        orderedList.add(new PokerHand("QH QC QS 3H 3D")); // Full House
        orderedList.add(new PokerHand("QD QH QH 2C 2D")); // Full House
        orderedList.add(new PokerHand("4C 5C 9C 8C KC")); // Flush
        orderedList.add(new PokerHand("8C 9C 5C 3C TC")); // Flush
        orderedList.add(new PokerHand("JS QS 9H TS KH")); // Straight
        orderedList.add(new PokerHand("3C 5C 4C 2C 6H")); // Straight
        orderedList.add(new PokerHand("AC KH QH AH AS")); // Three of a Kind
        orderedList.add(new PokerHand("5C 5S 3S 7H 5S")); // Three of a Kind
        orderedList.add(new PokerHand("AS 3C KH AD KC")); // Two Pair
        orderedList.add(new PokerHand("8S 8D 2C KH KC")); // Two Pair
        orderedList.add(new PokerHand("KD 4S KD 3H 8S")); // Pair
        orderedList.add(new PokerHand("KS 9D 4D 9S 5S")); // Pair
        orderedList.add(new PokerHand("6C 7D JC 2D AS")); // High Card
        orderedList.add(new PokerHand("3C 5D 9D TH JC")); // High Card

        return orderedList;

    }

    // method returns a list of poker hands containing all the hands from the expected answer but in a random order
    private static List<PokerHand> createRandomList (){
        List<PokerHand> stockList = new ArrayList<>(expected);
        List<PokerHand> randomList = new ArrayList<>();
        Random random = new Random();

        while(stockList.size() != 0){

            int handSelected =  random.nextInt(stockList.size());
            PokerHand handAdded = stockList.get(handSelected);
            randomList.add(handAdded);
            stockList.remove(handAdded);

        }

        return randomList;

    }

    // == Test Methods ==
    @Test
    public void sortTest (){
        Collections.sort(randomList);
        assertEquals(expected, randomList);

    }


}
