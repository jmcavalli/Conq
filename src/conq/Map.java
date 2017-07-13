/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

/**
 *
 * @author bible_000
 */

import java.util.*;

class Tile{
    int type; //-1 = water, 0 = coastal, 1 = land
    int ownerID = -1;
    int navyID = -1;
    String religion = "";
    String culture = "";
    //ArrayList<Building> buildings = new ArrayList();
    String buildings = "-";
    char phy;
    char pol;
    char res;
    boolean move = true;
}

class Building {
    
}


public class Map {
    int playerID;
    String location;
    int year;
    String era;
    Tile[][] map = new Tile[18][18];
    
    void setup(Loader loader, int loc, int time){
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                map[i][j] = new Tile();
                map[i][j].pol = loader.locations.get(loc).maps.get(time).map[i][j];
                if(loader.locations.get(loc).maps.get(time).map[i][j] == '='){
                    map[i][j].type = -1;
                }else if(Character.isLowerCase(loader.locations.get(loc).maps.get(time).map[i][j])){
                    map[i][j].type = 0;
                }else{
                    map[i][j].type = 1;
                }
                if(loader.locations.get(loc).maps.get(time).map[i][j] == '=')
                    map[i][j].ownerID = -2;
                if(loader.locations.get(loc).maps.get(time).map[i][j] != 'X' 
                    && loader.locations.get(loc).maps.get(time).map[i][j] != 'x'
                    && loader.locations.get(loc).maps.get(time).map[i][j] != '0'
                    && loader.locations.get(loc).maps.get(time).map[i][j] != '='){
                    int k = 0;
                    for(; k < loader.locations.get(loc).maps.get(time).civs.size(); k++){
                        if (Character.toUpperCase(loader.locations.get(loc).maps.get(time).map[i][j]) == loader.locations.get(loc).maps.get(time).civs.get(k).sym){
                            break;
                        }
                    }
                    map[i][j].ownerID = k;
                }
            }
        }
        //playerID = civ;
        location = loader.locations.get(loc).name;
        year = loader.locations.get(loc).maps.get(time).year;
        era = ((loader.locations.get(loc).maps.get(time).era) ? "AD" : "BC"); 
    }
    boolean onMap(int x, int y){
        if(x < 0 || x > 17 || y < 0 || y > 17)
            return false;
        else
            return true;
    }
    
    int distance(int ax, int ay, int bx, int by){
        return Math.abs(ay - by) + Math.abs(ax - bx);
    }
    
    boolean connectedByWater(int ax, int ay, int bx, int by, int limit){
        if(limit < 0)
            return false;
        
        if(!onMap(ax, ay) || map[ay][ax].type == 1)
            return false;
        
        if(!onMap(bx, by) || map[by][bx].type == 1)
            return false;
        
        if(ax == bx && ay == by)
            return true;
        
        return (connectedByWater(ax - 1, ay, bx, by, limit - 1)
                || connectedByWater(ax + 1, ay, bx, by, limit - 1)
                || connectedByWater(ax, ay - 1, bx, by, limit - 1)
                || connectedByWater(ax, ay + 1, bx, by, limit - 1));
    }
    
    void displayLine(int lineNum, int mode, int sx, int sy){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            if(sy == lineNum && sx == i){
                System.out.print(c.BLRED + c.LWHITE + "X");
                
                System.out.print(" " + c.RESET);
                
            }
            else{
                if(mode == 0){
                    System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].pol);
                }
                if(mode == 1){
                    System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].buildings.charAt(0));     
                }
                if(mode == 2){
                    if(map[lineNum][i].move)
                        System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].pol);
                    else
                        System.out.print(c.civColor(map[lineNum][i].ownerID) + "+"); 
                }
                
                if(map[lineNum][i].type == 1)
                    System.out.print(" " + c.RESET);
                if(map[lineNum][i].type == 0)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.CYAN)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BCYAN + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
                if(map[lineNum][i].type == -1)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.BLUE)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BBLUE + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
            }
            
            
        }
    }
    
    void displayLine(int lineNum, int mode){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            if(mode == 0){
                    System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].pol);
                }
                if(mode == 1){
                    System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].buildings.charAt(0));     
                }
                if(mode == 2){
                    if(map[lineNum][i].move)
                        System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].pol);
                    else
                        System.out.print(c.civColor(map[lineNum][i].ownerID) + "+"); 
                }
                
                if(map[lineNum][i].type == 1)
                    System.out.print(" " + c.RESET);
                if(map[lineNum][i].type == 0)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.CYAN)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BCYAN + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
                if(map[lineNum][i].type == -1)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.BLUE)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BBLUE + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
                        
        }
    }
    
    void displayLineNavyRange(int lineNum, int sx, int sy, int navX, int navY, int limit){
        Color c = new Color();
        for(int i = 0; i < 18; i++){
            if(sy == lineNum && sx == i){
                System.out.print(c.BLRED + c.LWHITE + "X");
                System.out.print(" " + c.RESET);
            }
            else{
                if(connectedByWater(navX, navY, i, lineNum, limit)){
                    System.out.print(c.BCYAN + c.LBLUE + map[lineNum][i].pol);
                if(map[lineNum][i].type == 1)
                    System.out.print(" " + c.RESET);
                if(map[lineNum][i].type == 0)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.CYAN)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BCYAN + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
                if(map[lineNum][i].type == -1)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.CYAN)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BCYAN + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
            
                }
                else{
                    System.out.print(c.civColor(map[lineNum][i].ownerID) + map[lineNum][i].pol);
                if(map[lineNum][i].type == 1)
                    System.out.print(" " + c.RESET);
                if(map[lineNum][i].type == 0)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.CYAN)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BCYAN + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
                if(map[lineNum][i].type == -1)
                    System.out.print((!(c.civColor(map[lineNum][i].navyID).contentEquals(c.BLUE)) ? c.civColor(map[lineNum][i].navyID) : c.BLACK) + c.BBLUE + (map[lineNum][i].navyID > -1 ? "-" : " ") + c.RESET);
            
                }
            }
            
           
            
        }
    }
    
    
}
