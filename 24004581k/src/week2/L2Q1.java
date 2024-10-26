
package week2;

import java.util.Scanner;

public class L2Q1 {
    public static void main(String[] args){
        
    Scanner scanner = new Scanner(System.in);
    Double Fahrenheit;
        System.out.println("Enter the temperature in degree Fahrenheit");
        Fahrenheit=scanner.nextDouble();
        Double Celsius = (Fahrenheit-32)/1.8;
        System.out.printf("Temperature : %.2f Celsius",Celsius);
    };
    
    
}
