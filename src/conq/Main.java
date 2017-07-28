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
        
        try{
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
        catch(Exception e){
            e.printStackTrace();
            System.console().readLine();
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
        save(theGame);
    }
    
    static void loadGame(){
        System.out.println("Loading game...");
        Game theGame = new Game();
        String fileName = "";
        
        Menu menu = new Menu();
        menu.clearScreen();
            
        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();
        System.out.println("Found savefiles: ");
        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].getName().endsWith(".ser")) {
            System.out.println("  " + listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4));
          }
        }
    
        
            System.out.println("Please write the name of the savefile: ");
                fileName = System.console().readLine();
        
        try {
             FileInputStream fileIn = new FileInputStream(fileName + ".ser");
             ObjectInputStream in = new ObjectInputStream(fileIn);
             theGame = (Game) in.readObject();
             in.close();
             fileIn.close();
          }catch(IOException i) {
             i.printStackTrace();
             return;
          }catch(ClassNotFoundException c) {
             System.out.println("Employee class not found");
             c.printStackTrace();
             return;
          }
        
        theGame.playFromSave();
        theGame.quitGame();
        save(theGame);
    }
    
    static void options(){
        System.out.println("Showing options...");
    }
    
    static void save(Game game){
    try {
         FileOutputStream fileOut =
         new FileOutputStream(game.fileName + ".ser");
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(game);
         out.close();
         fileOut.close();
         System.out.println("Game is saved as " + game.fileName + ".ser");
      }catch(IOException i) {
         i.printStackTrace();
      }
    }
    
    static void quit(){
        System.out.println("Qutting game... Good bye!");
    }
    
}
