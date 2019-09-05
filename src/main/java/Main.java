import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<PokerHand> handList = new ArrayList<>();

        handList.add(new PokerHand("AH 7C AD 10C 10S"));
        handList.add(new PokerHand("10C 10S 10D 4D 4C"));
        handList.add(new PokerHand("7S 7C 3S 3D 9H"));
        handList.add(new PokerHand("5D 6D 7D 8D 9D"));
        handList.add(new PokerHand("10H JH QH KH AH"));

        Collections.sort(handList);

        for(PokerHand hand : handList){

            System.out.println(hand.getUserDescription() + " is a " + hand.getClassification());

        }



    }
}
