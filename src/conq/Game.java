/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bible_000
 */
public class Game {
    int setUp(Loader loader){
        //Pick a location
        Menu menu = new Menu();
        int loc = menu.chooseLoc(loader, "Please pick your region");
        if(loc == -1){
            return -1;
        }
        menu.clearScreen();
        System.out.println(loader.locations.get(loc).name + ":");
        
        //Pick a year
        int time = menu.chooseTime(loader, loc, "Please pick a time period");
        if(time == -1){
            return - 1;
        }
        menu.clearScreen();
        System.out.println(loader.locations.get(loc).name + " in " +
            loader.locations.get(loc).maps.get(time).year + 
            ((loader.locations.get(loc).maps.get(time).era)
            ? "AD" : "BC") + ":");
        
        //Pick a nation
        int civ = menu.chooseCiv(loader, loc, time, "Please pick a nation");
        if(civ == -1){
            return -1;
        }
        menu.clearScreen();
        System.out.print("You chose the nation of " 
            + loader.locations.get(loc).maps.get(time).civs.get(civ).name + " in ");
        System.out.println(loader.locations.get(loc).name + " in the year " +
            loader.locations.get(loc).maps.get(time).year + 
            ((loader.locations.get(loc).maps.get(time).era)
            ? "AD" : "BC") + ":");
        
        return 0;
    }
    
    void play(){
        System.out.println("Press enter to go back to the mainmenu...");
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void quitGame(){
        
    }
    
    void exportGame(){
        
    }
    
    void importGame(){
        
    }
}
