/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bible_000
 */


public class Menu {
    
    public int disMenu(String[] args, String question){
        int answer = -1;
        clearScreen();
        while(answer < 0){
        Scanner scanner = new Scanner(System.in);
        System.out.println(question);//Ask the question
        for(int i = 1; i <= args.length; i++){
            System.out.println(i + "- " + args[i - 1]);
        }
        
        try{
        answer = scanner.nextInt();
        if(answer > args.length || answer < 1){
            answer = -1;
            clearScreen();
            System.out.println("I am sorry, please select a choice shown here.");
        }
        }catch(InputMismatchException e){
            clearScreen();
            System.out.println("I am sorry, please type a number");
            scanner.next();
        }
        }
        return answer;
    }
    
    public int disMainMenu(){
        String[] args = {"New Game", "Load Game", "Options", "Quit Game"};
        return disMenu(args, "Please Input the number of the choice you want.");
    }
    
    public int chooseLoc(Loader loader, String question){
        int ans = 0;
        boolean chosen = false;
        while(chosen == false){
        clearScreen();
        loader.locations.get(ans).disMap();
        System.out.println();
        System.out.println(question);
        System.out.println("Enter y to confirm or q to return to mainmenu");
        System.out.println("===================");
        System.out.println(((ans > 0) ? "" : "\u001B[30m") + "< z  " + "\u001B[0m" + loader.locations.get(ans).name + ((ans < loader.locations.size() - 1) ? "" : "\u001B[30m") + "  x >" + "\u001B[0m");
        System.out.println("===================");
        try {
            int input = System.in.read();
            if(input == 'z' && ans > 0)
                ans--;
            else if(input == 'x' && ans < loader.locations.size() - 1)
                ans++;
            else if(input == 'y')
                chosen = true;
            else if(input == 'q')
                return -1;
        }catch(IOException ex){
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        return ans;
    }
    
    public int chooseTime(Loader loader, int loc, String question){
        int ans = 0;
        boolean chosen = false;
        while(chosen == false){
        clearScreen();
        loader.locations.get(loc).maps.get(ans).disMap();
        System.out.println();
        System.out.println(question);
        System.out.println("Enter y to confirm or q to return to mainmenu");
        System.out.println(loader.locations.get(loc).name);
        System.out.println("===================");
        System.out.println(((ans > 0) ? "" : "\u001B[30m") + "< z  " + "\u001B[0m" + loader.locations.get(loc).maps.get(ans).year 
                + ((loader.locations.get(loc).maps.get(ans).era) ? "AD" : "BC") + ((ans < loader.locations.get(loc).maps.size() - 1) ? "" : "\u001B[30m") + "  x >" + "\u001B[0m");
        System.out.println("===================");
        try {
            int input = System.in.read();
            if(input == 'z' && ans > 0)
                ans--;
            else if(input == 'x' && ans < loader.locations.get(loc).maps.size() - 1)
                ans++;
            else if(input == 'y')
                chosen = true;
            else if(input == 'q')
                return -1;
        }catch(IOException ex){
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        return ans;
    }
    
    public int chooseCiv(Loader loader, int loc, int time, String question){
        int ans = 0;
        boolean chosen = false;
        while(chosen == false){
        clearScreen();
        loader.locations.get(loc).maps.get(time).disMapCiv(ans);
        System.out.println();
        System.out.println(question);
        System.out.println("Enter y to confirm or q to return to mainmenu");
        System.out.println(loader.locations.get(loc).name + " in " +
            loader.locations.get(loc).maps.get(time).year + 
            ((loader.locations.get(loc).maps.get(time).era)
            ? "AD" : "BC") + ":");
        System.out.println("===================");
        System.out.println(((ans > 0) ? "" : "\u001B[30m") + "< z  " + "\u001B[0m" + loader.locations.get(loc).maps.get(time).civs.get(ans).name + ((ans < loader.locations.get(loc).maps.get(time).civs.size() - 1) ? "" : "\u001B[30m") + "  x >" + "\u001B[0m");
        System.out.println("===================");
        System.out.print("Attributes:");
        for(int i = 0; i < loader.locations.get(loc).maps.get(time).civs.get(ans).attr.size(); i++){
            System.out.print(" " + loader.locations.get(loc).maps.get(time).civs.get(ans).attr.get(i));
        }
        System.out.println();
        try {
            int input = System.in.read();
            if(input == 'z' && ans > 0)
                ans--;
            else if(input == 'x' && ans < loader.locations.get(loc).maps.get(time).civs.size() - 1)
                ans++;
            else if(input == 'y')
                chosen = true;
            else if(input == 'q')
                return -1;
        }catch(IOException ex){
            Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        return ans;
    }
    
    public void clearScreen() {  
        try{
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        }catch(IOException | InterruptedException e){
        }
   }  
}
