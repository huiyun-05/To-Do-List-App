
package week3;
import java.util.Scanner;
public class L3Q1 {
    public static void main(String[] args){
    Scanner scanner = new Scanner(System.in);
        System.out.print("Enter first integer number: ");
        int num1=scanner.nextInt();
        System.out.print("Enter second integer number: ");
        int num2=scanner.nextInt();
        System.out.print("Enter the operand (+, -, /, *,%): ");
        char operator = scanner.next().charAt(0);
        
        if(operator=='+'){
           System.out.println(num1+"+"+num2+"="+num1+num2); 
        }
        if(operator=='-'){
           System.out.println(num1+"-"+num2+"="+(num1-num2)); 
        }
        if(operator=='*'){
           System.out.println(num1+"*"+num2+"="+(num1*num2)); 
        }
        if(operator=='/'){
            if(num2!=0){
                System.out.println(num1+"/"+num2+"="+(num1/num2));
            }
            else{
                System.out.println("Error: division by zero");
            }
        }
        if(operator=='%'){
            if(num2!=0){
                System.out.println(num1+"%"+num2+"="+(num1%num2));
            }
            else{
                System.out.println("Error: division by zero");
            }
        }
        scanner.close(); 
    }
         
}
