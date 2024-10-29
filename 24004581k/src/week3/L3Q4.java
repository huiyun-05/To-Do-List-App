
package week3;
import java.util.Random;

public class L3Q4 {
    public static void main(String[]Args){
        Random random=new Random();
        int player1=random.nextInt(6)+1;
        System.out.println("Dice of player 1: "+player1);
        int player2=random.nextInt(6)+1;
        System.out.println("Dice of player 2: "+player2);
        if(player1>player2){
            System.out.println("Player 1 wins");
        }
        else if(player2>player1){
            System.out.println("Player 2 wins");
        }
        else{
            System.out.println("Tie");
        }
        
    }
    
}
