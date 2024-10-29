
package week3;
import java.util.Scanner;
public class L3Q3 {
    public static void main(String[] args){
        Scanner scanner= new Scanner(System.in);
        System.out.println("Enter total sales");
        double sales=scanner.nextDouble();
        if(sales<=100){
            System.out.printf("Commission:%.2f",0.5*sales);
        }
        else if(sales>100&&sales<=500){
            System.out.printf("Commission:%.2f ",0.075*sales);     
        }
        else if(sales>500&&sales<=1000){
            System.out.printf("Commission: %.2f",0.1*sales);
        }
        else 
            System.out.printf("Commission:%.2f ",0.125*sales);
    }
    
}
