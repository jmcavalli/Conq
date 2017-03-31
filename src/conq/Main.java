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
class lLoc{
    public String name;
    public ArrayList<lMap> maps = new ArrayList();
    public char phyMap[][] = new char[18][18];
}

class lMap{
    public String Location;
    public int year;
    public boolean era;//0 BC, 1 AD
    public char[][] map = new char[18][18];
    public ArrayList<lCiv> civs = new ArrayList();
}

class lCiv{
    public char sym;
    public String name;
    public ArrayList<String> attr = new ArrayList();
}

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        ArrayList<lLoc> locations = new ArrayList();
        loadWorld(locations);
        try{
            System.in.read();
        }catch(IOException e){
        }
        
    }
    
    static void loadWorld(ArrayList<lLoc> locations){
        try{
            String line;
            FileReader fileReader = new FileReader("Map.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            if((line = bufferedReader.readLine()) != null) //Title
                System.out.println("Welcome to " + line.substring(2, line.length() - 2) + "!");
            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(line.substring(0, 2).matches("==")){ //Beginning of Location
                    lLoc temp = new lLoc();
                    temp.name = line.substring(2, line.length() - 2);
                    if((line = bufferedReader.readLine()) != null);
                    for(int i = 0; i < 18; i++){
                        if((line = bufferedReader.readLine()) != null){
                            String data = line.substring(2);
                            for(int j = 0; j < 18; j++){
                                temp.phyMap[i][j] = data.charAt(2 * j);
                            }
                        }
                    }
                    locations.add(temp);
                    //System.out.println(locations.get(locations.size() - 1).name);
                }else if(line.substring(0, 2).matches("--")){ //New Map
                    lMap temp = new lMap();
                    String title = line.substring(2, line.length() - 2);
                    if(title.endsWith("BC"))
                        temp.era = false;
                    else
                        temp.era = true;
                    String title1 = title.substring(0, title.length() - 2);
                    temp.year = Integer.parseInt(title1.substring(locations.
                        get(locations.size() - 1).name.length() + 1));
                    temp.Location = (locations.get(locations.size() - 1)).name;
                    if((line = bufferedReader.readLine()) != null);
                    for(int i = 0; i < 18; i++){
                        if((line = bufferedReader.readLine()) != null){
                            String data = line.substring(2);
                            for(int j = 0; j < 18; j++){
                                temp.map[i][j] = data.charAt(2 * j);
                            }
                        }
                    }
                    locations.get(locations.size() - 1).maps.add(temp);
                    //System.out.println("Where: " + temp.Location + " When: " + temp.year + " " + temp.era);
                }else{ //It is a civ on the map
                    lCiv temp = new lCiv();
                    temp.sym = line.charAt(0);
                    String info = line.substring(2);
                    int index = info.indexOf("--");
                    if(index > 0){
                        temp.name = info.substring(0, index);
                        while(index > 0){
                            String attr;
                            if(info.indexOf(" ", index) > 0){
                                attr = info.substring(index + 2, info.indexOf(" ", index));
                            }else{
                                attr = info.substring(index + 2);
                            }
                            temp.attr.add(attr);
                            index = info.indexOf("--", index + 1);
                        }
                    }else{
                        temp.name = info;
                    }
                    locations.get(locations.size() - 1).maps.
                        get(locations.get(locations.size() - 1).maps.size() - 1).civs.add(temp);
                    //if(temp.attr.size() > 0){System.out.println(temp.attr.get(0) + " ");}
                    //System.out.println(temp.name);
                    
                    
                }
            }
            System.out.println("Done loading map...");
        }catch(IOException e){
            System.out.println("Error in reading in map!");
        }
    }
    
    static void loadLoc(){
        
    }
    
    static void loadMap(){
        
    }
    
}
