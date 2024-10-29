
package week3;

import java.util.Scanner;
public class L3Q6 {
    public static void main(String[] args){
    Scanner scanner= new Scanner(System.in);
        System.out.println("Enter the radius of a circle");
        double radius=scanner.nextDouble();
        System.out.println("Enter a coordinate point(x)");
        double x=scanner.nextDouble();
        System.out.println("Enter a coordinate point(y)");
        double y=scanner.nextDouble();
        double z = Math.sqrt((x*x)+(y*y));
        if(z<=radius){
            System.out.println("The is inside the circle centered at(0,0)");
        }
        else{
            System.out.println("The point is outside the circle centered at(0,0)");
      
    }
            
        
    }
    
}
