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
    
    public void disMap(){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(phyMap[i][j] == '=')
                    System.out.print(c.CYAN + c.BBLUE + phyMap[i][j] + c.RESET);
                else if(phyMap[i][j] == 'x')
                    System.out.print(c.GREEN + c.BCYAN + phyMap[i][j] + c.RESET);
                else if(phyMap[i][j] == '0')
                    System.out.print(c.BLACK + c.BBLACK + phyMap[i][j] + c.RESET);
                else
                    System.out.print(c.GREEN + c.BGREEN + phyMap[i][j] + c.RESET);
            }
            System.out.print("\n");
        }
    }
}

class lMap{
    public String Location;
    public int year;
    public boolean era;//0 BC, 1 AD
    public char[][] map = new char[18][18];
    public ArrayList<lCiv> civs = new ArrayList();
    
    public void disMap(){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map[i][j] == '=')
                    System.out.print(c.BLUE + map[i][j] + c.RESET);
                else if(map[i][j] == 'x')
                    System.out.print(c.CYAN + map[i][j] + c.RESET);
                else if(map[i][j] == 'X')
                    System.out.print(c.GREEN + map[i][j] + c.RESET);
                else if(map[i][j] == '0')
                    System.out.print(c.BLACK + c.BBLACK + map[i][j] + c.RESET);
                else
                    System.out.print(c.WHITE + map[i][j] + c.RESET);
            }
            System.out.print("\n");
        }
    }
    
    public void disMapCiv(int civ){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map[i][j] == '=')
                    System.out.print(c.BLUE + map[i][j] + c.RESET);
                else if(map[i][j] == 'x')
                    System.out.print(c.CYAN + map[i][j] + c.RESET);
                else if(map[i][j] == 'X')
                    System.out.print(c.GREEN + map[i][j] + c.RESET);
                else if(map[i][j] == '0')
                    System.out.print(c.BLACK + c.BBLACK + map[i][j] + c.RESET);
                else if(map[i][j] == civs.get(civ).sym 
                    || map[i][j] == Character.toLowerCase(civs.get(civ).sym))
                    System.out.print(c.RED + map[i][j] + c.RESET);
                else
                    System.out.print(c.PURPLE + map[i][j] + c.RESET);
            }
            System.out.print("\n");
        }
    }
}

class lCiv{
    public char sym;
    public String name;
    public ArrayList<String> attr = new ArrayList();
}

public class Loader {
    ArrayList<lLoc> locations = new ArrayList();
    
    public void loadWorld(){
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
                    temp.era = !title.endsWith("BC");
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
}
