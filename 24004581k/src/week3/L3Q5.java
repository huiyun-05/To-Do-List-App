
package week3;
import java.util.Scanner;

public class L3Q5 {
    public static void main(String[] args){
        Scanner scanner= new Scanner(System.in);
        System.out.println("Enter a. ");
        int a=scanner.nextInt();
        System.out.println("Enter b. ");
        int b=scanner.nextInt();
        System.out.println("Enter c. ");
        int c=scanner.nextInt();
        System.out.println("Enter d. ");
        int d=scanner.nextInt();
        System.out.println("Enter e. ");
        int e=scanner.nextInt();
        System.out.println("Enter f. ");
        int f=scanner.nextInt();
        
        if((a*d)-(b*c)==0){
            System.out.println("The equation has no solution");
        }
        else{
            int x = (e*d-b*f)/(a*d-b*c);
        int y = (a*f-e*c)/(a*d-b*c);
        System.out.println("x: "+x);
        System.out.println("y: "+y);
        }
    }
    
}
