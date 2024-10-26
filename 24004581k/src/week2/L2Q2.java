
package week2;

import java.util.Scanner;
public class L2Q2 {
    public static void main (String []args){
        Scanner scanner = new Scanner(System.in);
        double P,D,R,Y;
       
        System.out.println("Enter the price of the car");
        P=scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the down payment");
        D=scanner.nextDouble();
        scanner.nextLine(); 
        System.out.println("Enter the interest rate in %");
        R=scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Enter the loan duration in year");
        Y=scanner.nextDouble();
        scanner.nextLine();
        double M = (P-D)*(1+R*Y/100)/(Y*12);
        System.out.printf("Monthly payment: RM%.2f",M);
        
        
    }
  
}
