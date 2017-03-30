/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;
import java.io.*;

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
        
        loadMap();
        try{
            System.in.read();
        }catch(IOException e){
        }
        
    }
    
    public static void loadMap(){
        try{
            String line;
           FileReader fileReader = new FileReader("Map.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("Done loading map...");
        }catch(IOException e){
            System.out.println("Error in reading in map!");
        }
    }
    
}
