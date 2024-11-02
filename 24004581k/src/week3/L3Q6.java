
package week3;

import java.util.Scanner;
public class L3Q6 {
    public static void main(String[] args){
    Scanner scanner= new Scanner(System.in);
        System.out.println("Enter the radius of a circle");
        double radius=scanner.nextDouble();
        System.out.println("Enter a coordinate point x and y");
        double x=scanner.nextDouble();
        double y=scanner.nextDouble();
        
        double z = Math.sqrt((x*x)+(y*y));
        if(z<radius){
            System.out.printf("The point (%.2f, %.2f) is inside the circle\n", x, y);
        }
        else if(z>radius){
            System.out.printf("The point (%.2f,%.2f) is outside the circle\n",x,y);
        }
        else{
            System.out.printf("The point (%.2f, %.2f) is on the circle's circumference\n", x, y);
      
    }
            
        
    }
    
}
