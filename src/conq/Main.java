/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;
import java.io.*;
import java.util.*;

/**
 *
 * @author bible_000
 */


public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Menu mainmenu = new Menu();
        Loader loader = new Loader();
        loader.loadWorld();
        int ans = -1;
        
        while(ans != 4){
        ans = mainmenu.disMainMenu();
        
        switch(ans){
            case 1:
                newGame(loader);
                break;
            case 2:
                loadGame();
                break;
            case 3:
                options();
                break;
            case 4:
                quit();
                break;
        }
        }
        
    }
    
    static void newGame(Loader loader){
        System.out.println("Creating a new game...");
        Game theGame = new Game();
        int choice = theGame.setUp(loader);
        if(choice == -1){
            return;
        }
        theGame.play();
        theGame.quitGame();
    }
    
    static void loadGame(){
        System.out.println("Loading game...");
    }
    
    static void options(){
        System.out.println("Showing options...");
    }
    
    static void quit(){
        System.out.println("Qutting game... Good bye!");
    }
    
}
