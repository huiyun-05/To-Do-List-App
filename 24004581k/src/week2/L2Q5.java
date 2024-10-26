
package week2;

import java.util.Random;
public class L2Q5 {
    public static void main(String[] args){
        Random random = new Random();
        int number = random.nextInt(10001);
        System.out.println("The random number: "+number);
        
        int num=number;
        int sum=0;
        int rem;
        
        while(num>0){
        rem=num%10;
        sum=sum+rem;
        num=num/10; 
        }
        System.out.println("The sum of all the digits " +sum);
    }
    
}

