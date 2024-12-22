
package week2;

import java.util.Random;

public class L2Q3 {
public static void main(String [] args){
     Random random = new Random();
        
        int num1 = random.nextInt(41) + 10; 
        int num2 = random.nextInt(41) + 10;
        int num3 = random.nextInt(41) + 10;
        System.out.println("The random numbers : "+num1+"," +num2+","+ num3);
      
        int sum=num1+num2+num3;
        System.out.println("Sum of the numbers:"+sum);
        double average=sum/3;
        System.out.printf("The average: %.2f",average);
        
    }       
}
