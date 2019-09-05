import java.util.*;

public class PokerHand implements Comparable<PokerHand>{

    // == constants ==
    enum Result {WIN, LOSS, TIE}

    enum Strength {
        ROYAL_FLUSH,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIR,
        PAIR,
        HIGH_CARD,
    }

    // == fields ==
    private String userDescription;
    private String description;
    private Strength classification;
    private int value;
    private Map<Integer, Integer> uniqueTotals;
    private List<Integer> sortedCardValues;
    private boolean isFlush;
    private boolean isStraight;

    // == constructor ==
    public PokerHand(String userDescription) {
        this.userDescription = userDescription;
        this.description = faceToNumber(userDescription);
        this.sortedCardValues = listSortedCardValues(this.description);
        this.uniqueTotals = countUniqueTotals(this.sortedCardValues);
        this.isFlush =  isFlush(this.description);
        this.isStraight = isStraight(this.sortedCardValues);
        this.classification = classifyHand(this.description);
        this.value = assignValue(this.classification);

    }

    // == public methods ==
    @Override
    public int compareTo(PokerHand hand) {
        if(this.compareWith(hand) == Result.WIN){
            return -1;
        } else if(this.compareWith(hand) == Result.LOSS){
            return 1;
        } else {
            return 0;
        }
    }

    // method that compares one poker hand to another and returns an enum Result.
    public Result compareWith (PokerHand hand){

        if(this.value > hand.value){
            return Result.WIN;
        } else if(this.value < hand.value){
            return Result.LOSS;
        }else if ((this.classification == Strength.ROYAL_FLUSH) || (this.classification == Strength.FLUSH)
                || (this.classification == Strength.HIGH_CARD)) {

            return checkKicker(this.sortedCardValues, hand.sortedCardValues);

        }else if((this.classification == Strength.STRAIGHT) || (this.classification == Strength.STRAIGHT_FLUSH)){

            boolean myStraightLow = this.sortedCardValues.contains(14) && this.sortedCardValues.contains(5);
            boolean oppStraightLow = hand.sortedCardValues.contains(14) && hand.sortedCardValues.contains(5);

            if(myStraightLow && !oppStraightLow){
                return Result.LOSS;
            } else if(!myStraightLow && oppStraightLow){
                return Result.WIN;
            } else if(myStraightLow && oppStraightLow){
                return Result.TIE;
            } else {

                return checkKicker(this.sortedCardValues, hand.sortedCardValues);
            }
        } else if(this.classification == Strength.FULL_HOUSE){
            return fullHouseCompare(this.uniqueTotals, hand.uniqueTotals);

        } else if(this.classification == Strength.FOUR_OF_A_KIND){
            return compareFour(this.uniqueTotals, hand.uniqueTotals);

        } else if(this.classification == Strength.THREE_OF_A_KIND){
            return compareThree(this.uniqueTotals, hand.uniqueTotals);

        } else if(this.classification == Strength.TWO_PAIR){
            return twoPairCompare(this.uniqueTotals, hand.uniqueTotals);

        } else {
            return comparePair(this.uniqueTotals, hand.uniqueTotals);
        }
    }

    // Getter for description that is presented to User.
    public String getUserDescription() {
        return userDescription;
    }

    // Getter for Strength of hand that can be understood by User.
    public Strength getClassification() {
        return classification;
    }

    // == private methods ==

    //method takes the description of the Hand and returns a Strength enum
    private Strength classifyHand (String description){
        if(isStraight && isFlush && sortedCardValues.contains(13) && sortedCardValues.contains(14)){
            return Strength.ROYAL_FLUSH;
        } else if(isStraight && isFlush){
            return Strength.STRAIGHT_FLUSH;
        } else if(isFlush) {
            return Strength.FLUSH;
        }else if(isStraight){
            return Strength.STRAIGHT;
        } else {

            if(uniqueTotals.containsValue(4)){
                return Strength.FOUR_OF_A_KIND;
            } else if(uniqueTotals.containsValue(3) && uniqueTotals.containsValue(2)){
                return Strength.FULL_HOUSE;
            } else if(uniqueTotals.containsValue(3)){
                return Strength.THREE_OF_A_KIND;
            } else {

                int count = 0;
                for(Integer i : uniqueTotals.values()){
                    if(i == 2) count++;
                }

                if(count == 2){
                    return Strength.TWO_PAIR;
                } else if(count == 1){
                    return Strength.PAIR;
                } else{
                    return Strength.HIGH_CARD;
                }
            }
        }
    }

    // method assigns a numerical to each enum Strength so they can be compared more efficiently
    private int assignValue(Strength strength) {

        int strengthValue = 0;

        switch (strength) {

            case ROYAL_FLUSH:
                strengthValue = 10;
                break;

            case STRAIGHT_FLUSH:
                strengthValue = 9;
                break;

            case FOUR_OF_A_KIND:
                strengthValue = 8;
                break;

            case FULL_HOUSE:
                strengthValue = 7;
                break;

            case FLUSH:
                strengthValue = 6;
                break;

            case STRAIGHT:
                strengthValue = 5;
                break;

            case THREE_OF_A_KIND:
                strengthValue = 4;
                break;

            case TWO_PAIR:
                strengthValue = 3;
                break;

            case PAIR:
                strengthValue = 2;
                break;

            case HIGH_CARD:
                strengthValue = 1;
                break;
        }

        return strengthValue;
    }

    //method converts description entered by user into a String comprised solely of numbers
    private String faceToNumber(String description) {
        String allNumbers = description.replaceAll("T", "10").replaceAll("J", "11")
                .replaceAll("Q", "12").replaceAll("K", "13")
                .replaceAll("A", "14");

        return allNumbers;
    }

    // method returns list of card values (2,3...14) sorted in ascending order
    private List<Integer> listSortedCardValues(String description) {

        String justValues = description.replaceAll("[A-Z]", ",").replaceAll("\\s+", "");
        String[] array = justValues.split(",");
        List<Integer> valueList = new ArrayList<>();
        for (String string : array) {
            valueList.add(Integer.parseInt(string));
        }
        Collections.sort(valueList);
        return valueList;
    }

    //method produces map of unique card values (2,3...14) and number of occurrences
    private Map<Integer, Integer> countUniqueTotals(List<Integer> cardValues) {

        Set<Integer> uniqueNumbers = new HashSet<>(cardValues);
        Map<Integer, Integer> totals = new HashMap<>();

        for (Integer i : uniqueNumbers) {
            int count = 0;

            for (Integer card : cardValues) {
                if (card.equals(i)) {
                    count++;
                }
            }
            totals.put(i, count);
        }
        return totals;
    }

    //checks the suits of the hand and returns true if the hand is a flush
    private boolean isFlush (String description){
        char[] suits = description.replaceAll("[^A-Z]", "").toCharArray();
        Set<Character> uniqueSuits = new HashSet<>();
        for(char c : suits){
            uniqueSuits.add(c);
        }
        return uniqueSuits.size() == 1;
    }

    //returns true if the hand is a straight - aces are counted as 1 or 14 depending upon situation
    private  boolean isStraight (List<Integer> cardValues){
        List<Integer> lowStraight = new ArrayList<>();
        lowStraight.add(14);
        lowStraight.add(2);
        lowStraight.add(3);
        lowStraight.add(4);
        lowStraight.add(5);
        if(cardValues.containsAll(lowStraight)){
            return true;
        }else {
            for(int i = 0; i <= 3; i++){
                if(!cardValues.contains(cardValues.get(i) + 1)){
                    return false;
                }
            }
        }
        return true;
    }

    //Method checks the 'kicker' card in the hand and is essential for the comparison of two hands of the same Strength.
    //This applies to hands with Strength Royal Flush, Straight Flush, Straight, Flush or High Card.
    private Result checkKicker (List<Integer> mySortedValues, List<Integer> oppSortedValues){

        for(int i = mySortedValues.size()-1; i >= 0; i--){
            int myKicker = mySortedValues.get(i);
            int oppKicker = oppSortedValues.get(i);

            if (myKicker > oppKicker) {
                return Result.WIN;

            } else if (myKicker < oppKicker) {
                return Result.LOSS;
            }
        }
        return Result.TIE;
    }

    //Method is essential in the comparison of two cards with Strength FOUR_OF_A_KIND
    private Result compareFour (Map<Integer, Integer> myTotals, Map<Integer, Integer> oppTotals){

        myTotals = new HashMap<>(myTotals);
        oppTotals = new HashMap<>(oppTotals);

        int myFour = 0;
        int oppFour = 0;
        for(Integer i : myTotals.keySet()){

            if(myTotals.get(i) == 4){
                myFour = i;
                break;
            }
        }

        for(Integer i : oppTotals.keySet()){

            if(oppTotals.get(i) == 4){
                oppFour = i;
                break;
            }
        }

        if(myFour > oppFour){
            return Result.WIN;
        } else if(myFour < oppFour){
            return Result.LOSS;
        } else{
            myTotals.remove(myFour);
            oppTotals.remove(oppFour);
            List<Integer> myKickers = new ArrayList<>(myTotals.keySet());
            List<Integer> oppKickers = new ArrayList<>(oppTotals.keySet());
            return checkKicker(myKickers, oppKickers);

        }
    }

    //Method is essential in the comparison of two cards with Strength THREE_OF_A_KIND
    private Result compareThree(Map<Integer, Integer> myTotals, Map<Integer, Integer> oppTotals){
        myTotals = new HashMap<>(myTotals);
        oppTotals = new HashMap<>(oppTotals);

        int myThree = 0;
        int oppThree = 0;
        for(Integer i : myTotals.keySet()){
            if(myTotals.get(i) == 3){
                myThree = i;
                break;
            }

        }

        for(Integer i : oppTotals.keySet()){
            if(oppTotals.get(i) == 3){
                oppThree = i;
                break;
            }
        }

        if(myThree > oppThree){
            return Result.WIN;
        } else if(myThree < oppThree){
            return Result.LOSS;
        } else{
            myTotals.remove(myThree);
            oppTotals.remove(oppThree);
            List<Integer> myKickers = new ArrayList<>(myTotals.keySet());
            Collections.sort(myKickers);
            List<Integer> oppKickers = new ArrayList<>(oppTotals.keySet());
            Collections.sort(oppKickers);
            return checkKicker(myKickers, oppKickers);
        }
    }

    //Method is essential in the comparison of two cards with Strength PAIR
    private Result comparePair (Map<Integer, Integer> myTotals, Map<Integer, Integer> oppTotals){
        myTotals = new HashMap<>(myTotals);
        oppTotals = new HashMap<>(oppTotals);

        int myTwo = 0;
        int oppTwo = 0;
        for(Integer i : myTotals.keySet()){
            if(myTotals.get(i) == 2){
                myTwo = i;
                break;
            }
        }

        for(Integer i : oppTotals.keySet()){
            if(oppTotals.get(i) == 2){
                oppTwo = i;
                break;
            }
        }

        if(myTwo > oppTwo){
            return Result.WIN;
        } else if(myTwo < oppTwo){
            return Result.LOSS;
        } else{
            myTotals.remove(myTwo);
            oppTotals.remove(oppTwo);
            List<Integer> myKickers = new ArrayList<>(myTotals.keySet());
            Collections.sort(myKickers);
            List<Integer> oppKickers = new ArrayList<>(oppTotals.keySet());
            Collections.sort(oppKickers);
            return checkKicker(myKickers, oppKickers);

        }
    }

    //Method is essential in the comparison of two cards with Strength FULL_HOUSE
    private Result fullHouseCompare (Map<Integer, Integer> myTotals, Map<Integer, Integer> oppTotals){

        int myThreeCards = 0;
        int myTwoCards = 0;

        for (Integer card : myTotals.keySet()) {
            if (myTotals.get(card) == 3) {
                myThreeCards = card;

            } else {
                myTwoCards = card;
            }

        }

        int oppThreeCards = 0;
        int oppTwoCards = 0;

        for (Integer card : oppTotals.keySet()) {
            if (oppTotals.get(card) == 3) {
                oppThreeCards = card;

            } else {
                oppTwoCards = card;
            }

        }

        if (myThreeCards > oppThreeCards) {
            return Result.WIN;

        } else if (myThreeCards < oppThreeCards) {
            return Result.LOSS;

        } else {

            if (myTwoCards > oppTwoCards) {
                return Result.WIN;

            } else if (myTwoCards < oppTwoCards) {
                return Result.LOSS;
            } else {
                return Result.TIE;
            }
        }
    }

    //Method is essential in the comparison of two cards with Strength TWO_PAIR
    private Result twoPairCompare (Map<Integer, Integer> myTotals, Map<Integer, Integer> oppTotals){

        List<Integer> myPairList = new ArrayList<>();
        Integer myKicker = 0;

        for (Integer card : myTotals.keySet()) {

            if (myTotals.get(card) == 2) {
                myPairList.add(card);
            } else {
                myKicker = card;
            }
        }

        List<Integer> oppPairList = new ArrayList<>();
        Integer oppKicker = 0;
        for (Integer card : oppTotals.keySet()) {

            if (oppTotals.get(card) == 2) {
                oppPairList.add(card);
            } else {
                oppKicker = card;
            }

        }

        Collections.sort(myPairList);
        Collections.sort(oppPairList);

        if (myPairList.get(1) > oppPairList.get(1)) {
            return Result.WIN;

        } else if (myPairList.get(1) < oppPairList.get(1)) {
            return Result.LOSS;

        } else {

            if (myPairList.get(0) > oppPairList.get(0)) {
                return Result.WIN;

            } else if (myPairList.get(0) < oppPairList.get(0)) {
                return Result.LOSS;

            } else {

                if (myKicker > oppKicker) {
                    return Result.WIN;

                } else if (myKicker < oppKicker) {
                    return Result.LOSS;

                } else {
                    return Result.TIE;
                }
            }
        }
    }


}
