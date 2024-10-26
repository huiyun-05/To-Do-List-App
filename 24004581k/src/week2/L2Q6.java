
package week2;

import java.util.Scanner;
public class L2Q6 {
    public static void main(String[] args){
        Scanner scanner= new Scanner(System.in);
        double M,f,i;

        System.out.println("Enter the amount of water in gram");
        M=scanner.nextDouble();
       
        System.out.println("Enter the initial temperature in Fahrenheit");
        i=scanner.nextDouble();
        
        System.out.println("Enter the final temperature in Fahrenheit");
        f=scanner.nextDouble();
        
        double initial_temperature=(i-32)/1.8;
        double final_temperature=(f-32)/1.8;
        double Q = (M/1000)*(final_temperature-initial_temperature)*4184;
        System.out.printf("%e",Q);

    }

}

