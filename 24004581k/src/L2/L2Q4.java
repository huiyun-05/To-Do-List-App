
package week2;

import java.util.Scanner;
public class L2Q4 {
    public static void main(String[] args){
        Scanner scanner= new Scanner(System.in);
        int time;
        System.out.println("Enter the time in seconds");
        time=scanner.nextInt();
        int hours=time/3600;
        int temp=time%3600;
        int minutes=temp/60;
        int seconds=temp%60;

        System.out.println(hours+" hours ,"+minutes+" minutes and "+seconds + " seconds");
    }    
}
