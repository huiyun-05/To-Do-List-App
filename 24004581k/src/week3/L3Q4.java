
package week3;
import java.util.Random;

public class L3Q4 {
    public static void main(String[]Args){
        Random random=new Random();
        int a = random.nextInt(6)+1;
        int b = random.nextInt(6)+1;
        int c = random.nextInt(6)+1;
        int d = random.nextInt(6)+1;
        
        System.out.println("Dice of player 1: "+a+","+b);
        System.out.println("Dice of player 1: "+c+","+d);
        
        if((a+b)>(c+d)){
            System.out.println("Player 1 wins");
        }
        else if((a+b)<(c+d)){
            System.out.println("Player 2 wins");
        }
        else{
            System.out.println("Draw");
        }
        
    }
    
}
