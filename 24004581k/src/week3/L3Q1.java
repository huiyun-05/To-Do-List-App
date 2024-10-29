
package week3;
import java.util.Scanner;
public class L3Q1 {
    public static void main(String[] args){
    Scanner scanner = new Scanner(System.in);
        System.out.println("Enter first integer number");
        int num1=scanner.nextInt();
        System.out.println("Enter second integer number");
        int num2=scanner.nextInt();
        System.out.println("Enter the operand");
        char operator = scanner.next().charAt(0);
        switch(operator){
        case '+' :
            System.out.println("Sum:"+num1+num2);
            break;
        case '-':
            System.out.println("Difference:"+(num1-num2));
            break;
        case '*':
            System.out.println("Multiplication:"+(num1*num2)); 
            break;
        case '/':
            System.out.println("Quotient:"+(num1/num2));
            break;
        case '%':
            System.out.println("Remainder:"+(num1%num2));
            break;
    }
        
    }
}
