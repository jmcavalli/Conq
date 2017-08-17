/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.ArrayList;


/**
 *
 * @author bible_000
 */

class Trade implements java.io.Serializable{
    int playerA;
    int playerB;
    int foodA;
    int goldA;
    int milA;
    int foodB;
    int goldB;
    int milB;
    
    int timer;
}

public class Game implements java.io.Serializable{
    
    Map map;
    int year;
    int month;
    String era;
    int playerID = 0;
    int [] players;
    Civ[] civs;
    ArrayList<Trade> trades = new ArrayList();
    int lookX;
    int lookY;
    int turnNum;
    int numTurn;
    String fileName = "";
    Random rand1 = new Random();
    boolean colonizationGame = false;
    boolean playWithProperties = true;
    
    
    int setUp(Loader loader){
        //Set file name
        
        Menu menu = new Menu();
        int input = 0;
        while(input <= 0){
            menu.clearScreen();
            System.out.println("Please set a name for the savefile: ");
            try{
                fileName = System.console().readLine();
                input = 1;
            }catch(NumberFormatException e){
                input = 0;
            }
            
         }
        
        //Pick a location
        int loc = menu.chooseLoc(loader, "Please pick your region");
        if(loc == -1){
            return -1;
        }
        menu.clearScreen();
        System.out.println(loader.locations.get(loc).name + ":");
        
        //Pick a year
        int time = menu.chooseTime(loader, loc, "Please pick a time period");
        if(time == -1){
            return -1;
        }
        menu.clearScreen();
        System.out.println(loader.locations.get(loc).name + " in " +
            loader.locations.get(loc).maps.get(time).year + 
            ((loader.locations.get(loc).maps.get(time).era)
            ? "AD" : "BC") + ":");
        
        //Pick a nation
        //int civ = 0; //menu.chooseCiv(loader, loc, time, "Please pick a nation");
        
        //if(civ == -1){
        //    return -1;
        //}
        menu.clearScreen();
//        System.out.print("You chose the nation of " 
//            + loader.locations.get(loc).maps.get(time).civs.get(civ).name + " in ");
//        System.out.println(loader.locations.get(loc).name + " in the year " +
//            loader.locations.get(loc).maps.get(time).year + 
//            ((loader.locations.get(loc).maps.get(time).era)
//            ? "AD" : "BC") + ":");
        civs = new Civ[loader.locations.get(loc).maps.get(time).civs.size()];
        
        for(int i = 0; i < loader.locations.get(loc).maps.get(time).civs.size(); i++){
            civs[i] = new Civ();
            for(int j = 0; j < loader.locations.get(loc).maps.get(time).civs.get(i).attr.size(); j++)
                civs[i].attr.add(loader.locations.get(loc).maps.get(time).civs.get(i).attr.get(j));
            civs[i].sym = loader.locations.get(loc).maps.get(time).civs.get(i).sym;
            civs[i].name = loader.locations.get(loc).maps.get(time).civs.get(i).name;
            civs[i].culture = loader.locations.get(loc).maps.get(time).civs.get(i).culture;
        }
        map = new Map();
        map.setup(loader, loc, time);
        setCultures();
        year = map.year;
        month = 1;
        era = map.era;
        setGameType();
        if(playWithProperties){
            for(int i = 0; i < civs.length; i++){
                civs[i].setProperties();
                if(civs[i].gainsFromLand){
                    int area = landArea(i);
                        civs[i].dFood += area;
                        civs[i].dGold += area;
                        civs[i].dMil += area;
                    }
            }
        }
        
        //playerID = civ;
        players = chooseNations(loader, loc, time);
        if(players[0] == -1)
            return -1;
        return 0;
    }
    
    void setCultures(){
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[i][j].type != -1 && map.map[i][j].ownerID > -1)
                    map.map[i][j].culture = civs[map.map[i][j].ownerID].culture;
            }
        }
    }
    
    
    int [] chooseNations(Loader loader, int loc, int time){
        Menu menu = new Menu();
        int input = 0;
        while(input <= 0){
            menu.clearScreen();
            System.out.println("Please select the number of players: ");
            try{
                input = Integer.parseInt(System.console().readLine());
            }catch(NumberFormatException e){
                input = 0;
            }
            if(input > civs.length)
                input = 0;
            }
        int [] players1 = new int[input];
        int [] err = {-1};
        for(int i = 0; i < input; i++){
            boolean chosen;
            do{
                chosen = false;
                players1[i] = menu.chooseCiv(loader, loc, time, "Player " + (i + 1) + ", please select a nation");
                if(players1[i] == -1)
                    return err;
                for(int j = 0; j < i; j++){
                    if(players1[j] == players1[i])
                        chosen = true;
                }
            }while(chosen == true);
            civs[players1[i]].human = true;
        }
        
        return players1;
    }
    
    int chooseCapital(int player){

        if(!civs[player].human){
            chooseRandomSafeSpotInNation(player);
            civs[player].capX = lookX;
            civs[player].capY = lookY;
            map.map[lookY][lookX].buildings = "*";
            return 0;
        }
        
        int input = 0;
        Menu menu = new Menu();
        for(lookX = 0; lookX < 18; lookX++){
            for(lookY = 0; lookY < 18; lookY++){
                if(map.map[lookY][lookX].ownerID == player)
                    break;
            }
            if(lookY < 18 && map.map[lookY][lookX].ownerID == player)
                    break;
        }
        while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please pick a spot for your capital in your territory",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Religion: " + map.map[lookY][lookX].religion,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                (map.map[lookY][lookX].ownerID == player ? c.GREEN : c.RED) + "y- place capital here" + c.RESET,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            display(player, sidemenu, 0, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == 'y'){
                if(map.map[lookY][lookX].ownerID == player){
                    civs[player].capX = lookX;
                    civs[player].capY = lookY;
                    map.map[lookY][lookX].buildings = "*";
                    return 0;
                }
            }
            if(input == 'q'){
                return 1;
            }
            
            }
        return 0;
    }
    
    void getTerr(int recieverID, int terrX, int terrY){
        int previous = map.map[terrY][terrX].ownerID;
        map.map[terrY][terrX].ownerID = recieverID;
        if(map.map[terrY][terrX].type == 1)
            map.map[terrY][terrX].pol = civs[recieverID].sym;
        else
            map.map[terrY][terrX].pol = Character.toLowerCase(civs[recieverID].sym);
        map.map[terrY][terrX].move = false;
        civs[previous].dFood -= numFarms(map.map[terrY][terrX].buildings) * civs[previous].farmOut;
        civs[recieverID].dFood += numFarms(map.map[terrY][terrX].buildings) * civs[recieverID].farmOut;
        if(civs[previous].gainsFromLand)
            civs[previous].dFood -= 1;
        if(civs[recieverID].gainsFromLand)
            civs[recieverID].dFood += 1;
        
        civs[previous].dGold -= numMines(map.map[terrY][terrX].buildings) * civs[previous].mineOut;
        civs[recieverID].dGold += numMines(map.map[terrY][terrX].buildings) * civs[recieverID].mineOut;
        if(civs[previous].gainsFromLand)
            civs[previous].dGold -= 1;
        if(civs[recieverID].gainsFromLand)
            civs[recieverID].dGold += 1;
        
        civs[previous].dMil -= numBases(map.map[terrY][terrX].buildings) * civs[previous].baseOut;
        civs[recieverID].dMil += numBases(map.map[terrY][terrX].buildings) * civs[recieverID].baseOut;
        if(civs[previous].gainsFromLand)
            civs[previous].dMil -= 1;
        if(civs[recieverID].gainsFromLand)
            civs[recieverID].dMil += 1;
        
        if(map.map[terrY][terrX].buildings.contains("*")){
            civs[recieverID].bonusPoints += civs[recieverID].capitalPoints;
            civs[previous].bonusPoints -= civs[previous].capitalPoints;
        }
    }
    
    int dieRoll(int sides){
        int ans = 0;
        Random rand = new Random();
        
        ans = rand.nextInt(sides) + 1;
        return ans;
    }
    int attackRound(int attackID, int defendID, boolean fort){
        int [] attacker = {dieRoll(civs[attackID].dieSides) + civs[attackID].attackBonus1, dieRoll(civs[attackID].dieSides)  + civs[attackID].attackBonus2, dieRoll(civs[attackID].dieSides) + civs[attackID].attackBonus3};
        int [] defender = {dieRoll(civs[defendID].dieSides)  + civs[defendID].defendBonus1, dieRoll(civs[defendID].dieSides) + civs[defendID].defendBonus2};
        int result = 0; //0- defender loses 2, 1- attacker and defender lose 1, 2- attacker loses 2
        
        if(fort){
            if(defender[0] > defender[1]){
                defender[0]++;
            }else{
                defender[1]++;
            }
        }
        
        if(defender[1] > defender[0]){
            int temp = defender[1];
            defender[1] = defender[0];
            defender[0] = temp;
        }
        
        int i, j, temp;
        for(i = 1; i < attacker.length; i++){
            temp = attacker[i];
            for(j = i - 1; j >= 0 && attacker[j] < temp; j--){
                attacker[j + 1] = attacker[j];
            }
            attacker[j + 1] = temp;
        }
        
        if(attacker[0] > defender[0])
            //if(civs[defendID].mil > 0)
                civs[defendID].mil--;
        else{
            //if(civs[attackID].mil > 0)
                civs[attackID].mil--;
            result++;
        }
        
        if(attacker[1] > defender[1])
            //if(civs[defendID].mil > 0)
                civs[defendID].mil--;
        else{
            //if(civs[attackID].mil > 0)
                civs[attackID].mil--;
            result++;
        }
            return result;
    }
    boolean battle(int attacker, int defender, boolean base, String buildings){
        //true = attacker wins; false = defender wins
        if(civs[defender].human){
        int input = 0;
        Menu menu = new Menu();
        while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "BATTLE",
                "Buildings: " + buildings,
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                c.LRED + "Attacker's Units: " + civs[attacker].mil + c.RESET,
                c.LGREEN + "Defender's Units: " + civs[defender].mil + c.RESET,
                "",
                c.RED + "Attack Bonuses: " + civs[attacker].attackBonus1 + " " + civs[attacker].attackBonus2 + " " + civs[attacker].attackBonus3 + c.RESET,
                c.GREEN + "Defense Bonuses: " + civs[defender].defendBonus1 + " " + civs[defender].defendBonus2 + c.RESET,
                "",
                "",
                "",
                c.LRED + "a- ATTACK" + c.RESET,
                "",
                "",
                "",
                "",
                "p- defender retreat",
                "q- attacker retreat"
                };
            
            
            display(attacker, sidemenu, 0, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'a'){
                if(civs[defender].mil < 2)
                    return true;
                
                
                
                attackRound(attacker, defender, base);
                
                if(civs[attacker].mil < 2)
                    return false;
            }
            if(input == 'p')
                return true;
            if(input == 'q')
                return false;
            
            
            }
        
        return true;
        
        }else{//Battling AI Player
            int cost = 0;
            double calcCost = calcCost(defender, buildings, false);
        int input = 0;
        Menu menu = new Menu();
        while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "BATTLE",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Buildings: " + map.map[lookY][lookX].buildings,
                c.LRED + "Attacker's Units: " + civs[attacker].mil + c.RESET,
                c.LGREEN + "Defender's Units: " + civs[defender].mil + c.RESET,
                "",
                c.LRED + " Attack Bonuses: " + civs[attacker].attackBonus1 + " " + civs[attacker].attackBonus2 + " " + civs[attacker].attackBonus3 + c.RESET,
                c.LGREEN + " Defense Bonuses: " + civs[defender].defendBonus1 + " " + civs[defender].defendBonus2 + c.RESET,
                "",
                "",
                c.LRED + "a- ATTACK" + c.RESET,
                c.RED + "f- Attack until retreat" + c.RESET,
                "",
                "",
                "",
                "",
                "q- attacker retreat"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            display(attacker, sidemenu, 0, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'a'){
                if(civs[defender].mil < 2)
                    return true;
                
                
                
                cost += 2 - attackRound(attacker, defender, base);
                
                if(civs[attacker].mil < 2)
                    return false;

                if(cost > calcCost)
                    return true;
            }
            if(input == 'f'){
                if(civs[defender].mil < 2)
                        return true;
                
                while(civs[attacker].mil >= 2 && civs[defender].mil >= 2 && cost <= calcCost){
                    if(civs[defender].mil < 2)
                        return true;

                    cost += 2 - attackRound(attacker, defender, base);

                    if(civs[attacker].mil < 2)
                        return false;

                    if(cost > calcCost)
                        return true;
                }
            }
            if(input == 'q')
                return false;
            
            
            }
        
        return true;
        }
    }
    
    void conquer(int player, int terrX, int terrY){
        boolean winner = battle(player, map.map[terrY][terrX].ownerID, (numBases(map.map[terrY][terrX].buildings) > 0), map.map[terrY][terrX].buildings);
        if (winner == true)
            getTerr(player, terrX, terrY);
    }
    
    
    void attackTerritory(int player){
        int input = 0;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please pick a spot to conquer",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Buildings: " + map.map[lookY][lookX].buildings,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                (canBeAttacked(player, lookX, lookY)
                    ? c.GREEN : c.RED) + "y- conquer here" + c.RESET,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            display(player, sidemenu, 2, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == 'y'){
                if(map.map[lookY][lookX].ownerID < 0 && canBeAttacked(player, lookX, lookY)){
                    map.map[lookY][lookX].ownerID = player;
                    if(civs[player].gainsFromLand){
                        civs[player].dFood += 1;
                        civs[player].dGold += 1;
                        civs[player].dMil += 1;
                    }
                    if(map.map[lookY][lookX].type == 1)
                        map.map[lookY][lookX].pol = civs[player].sym;
                    else
                        map.map[lookY][lookX].pol = Character.toLowerCase(civs[player].sym);
                    map.map[lookY][lookX].move = false;
                }
                else if(canBeAttacked(player, lookX, lookY)){
                    conquer(player, lookX, lookY);
                }
            }
            
            }
        
    }
    
    
    void buildBuilding(int player){
        int input = 0;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please pick a spot to build on",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Buildings: " + map.map[lookY][lookX].buildings,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                (civs[player].canBuild && map.map[lookY][lookX].ownerID == player  && civs[player].gold >= civs[player].farmCost
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LGREEN + "#- Farm" + c.RESET)
                    : (c.RED + "#- Farm (" + civs[player].farmCost + " gold)" + c.RESET)),
                (civs[player].canBuild && map.map[lookY][lookX].ownerID == player  && civs[player].gold >= civs[player].mineCost
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LYELLOW + "^- Mine" + c.RESET)
                    : (c.RED + "^- Mine (" + civs[player].mineCost + " gold)" + c.RESET)),
                (civs[player].canBuild && map.map[lookY][lookX].ownerID == player  && civs[player].gold >= civs[player].baseCost
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LRED + "@- Fort (Make navies in nearby sea)" + c.RESET)
                    : (c.RED + "@- Fort (" + civs[player].baseCost + " gold)" + c.RESET)),
                (canBuildNavy(player, lookX, lookY)   && map.map[lookY][lookX].navyID == -1
                    && civs[player].gold >= civs[player].navyCost
                    ? (c.LBLUE + "n- Navy" + c.RESET)
                    : (c.RED + "n- Navy (" + civs[player].navyCost + " gold)" + c.RESET)),
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            display(player, sidemenu, 1, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == '#'){
                if(civs[player].canBuild && map.map[lookY][lookX].ownerID == player && civs[player].gold >= civs[player].farmCost){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "#";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("#");
                    civs[player].dFood += civs[player].farmOut;
                    civs[player].gold -= civs[player].farmCost;
                }
            }
            if(input == '^'){
                if(civs[player].canBuild && map.map[lookY][lookX].ownerID == player && civs[player].gold >= civs[player].mineCost){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "^";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                    civs[player].dGold += civs[player].mineOut;
                    civs[player].gold -= civs[player].mineCost;
                }
            }
            if(input == '@'){
                if(civs[player].canBuild && map.map[lookY][lookX].ownerID == player && civs[player].gold >= civs[player].baseCost){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "@";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                    civs[player].dMil += civs[player].baseOut;
                    civs[player].gold -= civs[player].baseCost;
                }
            }
            if(input == 'n'){
                if(canBuildNavy(player, lookX, lookY) && map.map[lookY][lookX].type == -1 && map.map[lookY][lookX].navyID == -1 && civs[player].gold >= civs[player].navyCost){
                    map.map[lookY][lookX].navyID = player;
                    civs[player].gold -= civs[player].navyCost;
                }
            }
            
            }
        
    }
    
    void viewNations(int player){
        int input = 0;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            c.setCultureColor(map.map, 18 * 18);
            String[] sidemenu = {
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                map.map[lookY][lookX].ownerID >= 0 ? "Country Culture: " + c.cultureColor(civs[map.map[lookY][lookX].ownerID].culture) + civs[map.map[lookY][lookX].ownerID].culture : "",
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            int j = 0;
            if(playWithProperties)
            for(int i = 6; i < sidemenu.length && map.map[lookY][lookX].ownerID >= 0 && j < civs[map.map[lookY][lookX].ownerID].attr.size(); i++){
                while(j < civs[map.map[lookY][lookX].ownerID].attr.size() && civs[map.map[lookY][lookX].ownerID].attr.get(j).startsWith("c-"))
                    j++;
                if(j < civs[map.map[lookY][lookX].ownerID].attr.size())
                    sidemenu[i] = civs[map.map[lookY][lookX].ownerID].attr.get(j);
                j++;
            }
            display(player, sidemenu, 1, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            
            }
        
    }
    
    
    void moveNavy(int player, int navX, int navY, int limit){
        int input = 0;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please pick a space to move the navy to",
                "",
                map.map[lookY][lookX].navyID >= 0 ? "Navy: " + civs[map.map[lookY][lookX].navyID].name : "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Religion: " + map.map[lookY][lookX].religion,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                (map.connectedByWater(navX, navY, lookX, lookY, limit) && map.map[lookY][lookX].navyID != player
                    ? c.GREEN : c.RED) + "y-  move navy" + c.RESET,
                "",
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            displayNavy(player, sidemenu, 0, true, lookX, lookY, navX, navY, limit);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == 'y'){
                if(map.map[navY][navX].move && map.connectedByWater(navX, navY, lookX, lookY, limit) && map.map[lookY][lookX].navyID != player){
                    if(map.map[lookY][lookX].navyID < 0){
                        map.map[navY][navX].navyID = -1;
                        map.map[lookY][lookX].navyID = player;
                    }else{
                        int res = attackRound(player, map.map[lookY][lookX].navyID, false);
                        if(res == 0){
                            map.map[navY][navX].navyID = -1;
                            map.map[lookY][lookX].navyID = player;
                        }else if(res == 2){
                            map.map[navY][navX].navyID = -1;
                        }
                    }
                    map.map[lookY][lookX].move = false;
                    input = 'q';
                }
            }
            
            }
        
    }
    
    
    
    void moveNavy(int player){
        int input = 0;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please pick a navy to move",
                "",
                map.map[lookY][lookX].navyID >= 0 ? "Navy: " + civs[map.map[lookY][lookX].navyID].name : "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                "Religion: " + map.map[lookY][lookX].religion,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                (map.map[lookY][lookX].navyID == player && map.map[lookY][lookX].move
                    ? c.GREEN : c.RED) + "y- select navy" + c.RESET,
                "",
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            
            if(map.map[lookY][lookX].type == -1)
                sidemenu[1] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[1] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[1] = c.GREEN + "Land" + c.RESET;
            display(player, sidemenu, 0, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == 'y'){
                if(map.map[lookY][lookX].navyID == player && map.map[lookY][lookX].move){
                    int navX = lookX;
                    int navY = lookY;
                    int limit = civs[player].navyLimit;
                    moveNavy(player, navX, navY, limit);
                }
            }
            
            }
        
    }
    
    void makeTrade(int playerA, int playerB, int foodA, int goldA, int milA, int foodB, int goldB, int milB, int turns){
        Trade temp = new Trade();
        temp.playerA = playerA;
        temp.playerB = playerB;
        temp.foodA = foodA;
        temp.goldA = goldA;
        temp.milA = milA;
        temp.foodB = foodB;
        temp.goldB = goldB;
        temp.milB = milB;
        temp.timer = turns;
        
       
        civs[playerA].dFood -= foodA;
        civs[playerB].dFood += foodA;
        civs[playerA].dGold -= goldA;
        civs[playerB].dGold += goldA;  
        civs[playerA].dMil -= milA;
        civs[playerB].dMil += milA;
        
        civs[playerB].dFood -= foodB;
        civs[playerA].dFood += foodB;
        civs[playerB].dGold -= goldB;
        civs[playerA].dGold += goldB;  
        civs[playerB].dMil -= milB;
        civs[playerA].dMil += milB;
        
        
        
        trades.add(temp);
        
    }
    
    void cancelTrade(int ID){
        Trade temp = trades.remove(ID);
        
        civs[temp.playerA].dFood -= temp.foodB;
        civs[temp.playerB].dFood += temp.foodB;
        civs[temp.playerA].dGold -= temp.goldB;
        civs[temp.playerB].dGold += temp.goldB;  
        civs[temp.playerA].dMil -= temp.milB;
        civs[temp.playerB].dMil += temp.milB;
        
        civs[temp.playerB].dFood -= temp.foodA;
        civs[temp.playerA].dFood += temp.foodA;
        civs[temp.playerB].dGold -= temp.goldA;
        civs[temp.playerA].dGold += temp.goldA;  
        civs[temp.playerB].dMil -= temp.milA;
        civs[temp.playerA].dMil += temp.milA;
        
    }
    boolean worthItForBToTrade(int player, double foodA, double goldA, double milA, double foodB, double goldB, double milB){
        double total = civs[player].dFood + civs[player].dGold + civs[player].dMil;
        double costFood = 2 - (civs[player].dFood + total)/total;
        double costGold = 2 - (civs[player].dGold + total)/total;
        double costMil = 2 - (civs[player].dMil + total)/total;
        boolean canAfford = (goldB < 1 || civs[player].dGold - civs[player].incomeMin >= goldB) && civs[player].dFood >= foodB && civs[player].dMil >= milB;
        return canAfford && costFood * foodA + costGold * goldA + costMil * milA >= costFood * foodB + costGold * goldB + costMil * milB;
    }
    
    void conductTrade(int player){
        int input = 0;
        int input2 = 0;
        int time = 0;
        int foodA = 0;
        int foodB = 0;
        int goldA = 0;
        int goldB = 0;
        int milA = 0;
        int milB = 0;
        boolean Aconf = false;
        boolean Bconf = false;
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "Please hover over the desired trade partner",
                "to change: [letter][amount] example: \"r10\"",
                c.GREEN + "Player A: " + c.civColor(player) + civs[player].name + c.RESET,
                c.BLUE + "Player B: " 
                    + (map.map[lookY][lookX].ownerID >= 0 ? 
                        (c.civColor(map.map[lookY][lookX].ownerID) 
                        + civs[map.map[lookY][lookX].ownerID].name 
                        + " " + c.LGREEN + civs[map.map[lookY][lookX].ownerID].dFood 
                        + " " + c.LYELLOW +  civs[map.map[lookY][lookX].ownerID].dGold 
                        + " " + c.LRED + civs[map.map[lookY][lookX].ownerID].dMil + c.RESET) : ""),
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                "r- " + c.GREEN + "Player A provides " + c.LGREEN + foodA + " food", //foodA
                "f- " + c.GREEN + "Player A provides " + c.LYELLOW + goldA + " gold", //goldA
                "v- " + c.GREEN + "Player A provides " + c.LRED + milA + " mil", //milA
                "y- " + c.BLUE + "Player B provides " + c.LGREEN + foodB + " food", //foodB
                "h- " + c.BLUE + "Player B provides " + c.LYELLOW + goldB + " gold", //goldB
                "n- " + c.BLUE + "Player B provides " + c.LRED + milB + " mil", //milB
                "g- " + "every turn for " + time + " turns", //time
                map.map[lookY][lookX].ownerID > -1 && map.map[lookY][lookX].ownerID != player ? (Aconf ? c.GREEN + "Signed by player A" : c.RED + "o- player A confirm") : "",
                map.map[lookY][lookX].ownerID > -1 && map.map[lookY][lookX].ownerID != player ? 
                    (civs[map.map[lookY][lookX].ownerID].human ? 
                        (Bconf ? c.GREEN + "Signed by player B" 
                            : c.RED + "p- player B confirm") 
                    : (worthItForBToTrade(map.map[lookY][lookX].ownerID, foodA, goldA, milA, foodB, goldB, milB) ? c.GREEN + "Player B will sign" : c.RED + "Player B won't sign")) 
                    : "",
                "q- go back to menu"
                };
            
            display(player, sidemenu, 1, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0){
                input = input1.charAt(0);
                if(input1.length() > 1)
                    try{
                        input2 = Integer.parseInt(input1.substring(1));
                    }catch(NumberFormatException e){
                        input2 = 0;
                    }
            }
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == 'r')
                foodA = input2;
            if(input == 'f')
                goldA = input2;
            if(input == 'v')
                milA = input2;
            if(input == 'y')
                foodB = input2;
            if(input == 'h')
                goldB = input2;
            if(input == 'n')
                milB = input2;
            if(input == 'g')
                time = input2;
            if (input == 'o' && map.map[lookY][lookX].ownerID != player){
                if(!civs[map.map[lookY][lookX].ownerID].human && worthItForBToTrade(map.map[lookY][lookX].ownerID, foodA, goldA, milA, foodB, goldB, milB))
                    Bconf = true;
                   
                if(civs[player].dFood >= foodA
                        && civs[player].dGold >= goldA
                        && civs[player].dMil >= milA)
                    Aconf = true;
                
                if(Bconf && Aconf){
                    makeTrade(player, map.map[lookY][lookX].ownerID, foodA, goldA, milA, foodB, goldB, milB, time);
                    input = 'q';
                }
            }
            if (input == 'p' && civs[map.map[lookY][lookX].ownerID].human && map.map[lookY][lookX].ownerID != player){
                if(civs[map.map[lookY][lookX].ownerID].dFood >= foodB
                        && civs[map.map[lookY][lookX].ownerID].dGold >= goldB
                        && civs[map.map[lookY][lookX].ownerID].dMil >= milB)
                    Bconf = true;
                
                if(Aconf && Bconf){
                    makeTrade(player, map.map[lookY][lookX].ownerID, foodA, goldA, milA, foodB, goldB, milB, time);
                    input = 'q';
                }
            }
            
            }
        
    }
    
    void play(){
        int input = 0;
        Menu menu = new Menu();
        Random rand = new Random();
            while(input <= 0){
            menu.clearScreen();
            System.out.println("Please select the number of turns (default 10): ");
            try{
                input = Integer.parseInt(System.console().readLine());
            }catch(NumberFormatException e){
                input = 0;
            }
            
            }
            numTurn = input;
            
            input = 0;
            Color c1 = new Color();
            int agg = 50;
            boolean ran = false;
            while(input != 'y'){
            menu.clearScreen();
            System.out.println("Please enter the AI aggressiveness percentage: ");
            System.out.print("AI Aggessiveness [");
            if(agg < 25)
                System.out.print(c1.LBLUE);
            else if(agg < 50)
                System.out.print(c1.LGREEN);
            else if(agg < 75)
                System.out.print(c1.LYELLOW);
            else if(agg <= 100)
                System.out.print(c1.LRED);
            
            if(ran)
                System.out.print(c1.LBLACK);
            
            for(int i = 0; i < agg/10; i++)
                System.out.print("=");
            for(int i = 0; i < 10 - agg/10; i++)
                System.out.print(" ");
            System.out.println(c1.RESET + "] " + agg + "%");
            System.out.println((ran ? c1.GREEN + "r- random distribution on": "r- random distribution off") + c1.RESET);
            System.out.println("y- confirm\n");
            try{
                String input1 = System.console().readLine();
                if(input1.charAt(0) == 'r'){
                    if(ran)
                        ran = false;
                    else
                        ran = true;
                }else if(input1.charAt(0) == 'y'){
                    input = 'y';
                }else{
                    agg = Integer.parseInt(input1);
                    if(agg > 100 || agg < 0)
                        agg = 50;
                }
            }catch(NumberFormatException e){
                agg = 50;
            }
            
            }
            
            for(int i = 0; i < civs.length;i++){
                if(!civs[i].human){
                    if (ran)
                        civs[i].agressiveness = rand.nextDouble();
                    else
                        civs[i].agressiveness = (double)agg/100;
                }
                if(landArea(i) < 5){
                    //civs[i].mil = 10 * landArea(i);
                    //civs[i].gold = 100 * landArea(i);
                    //civs[i].agressiveness *= Math.pow(10, landArea(i) - 5);
                    if(!ran)
                        civs[i].agressiveness /= 10;
                }
            }
            
        
        for(int i = 0; i < players.length; i++){
            int end = chooseCapital(players[i]);
            if (end == 1)
                return;
        }
        
        for(int i = 0; i < civs.length;i++){
                if(!civs[i].human)
                    chooseCapital(i);
            }
        
        while(turnNum < numTurn){
            roundUpdate();
            
            for(int i = 0; i < players.length; i++){
                playerID = i;
                int end = takeTurn(players[i]);
                if (end == 1)
                    return;
            }
            
            for(int i = 0; i < civs.length;i++){
                if(!civs[i].human)
                    takeTurn(i);
            }
            yearIncrement();
            turnNum++;
        }
        
        gameResults();
    }
    
    void playFromSave(){
        int input = 0;
        
        for(int i = playerID; i < players.length; i++){
                playerID = i;
                int end = takeTurn(players[i]);
                if (end == 1)
                    return;
            }
        
        for(int i = 0; i < civs.length;i++){
                if(!civs[i].human)
                    takeTurn(i);
            }
            yearIncrement();
            turnNum++;
        
        
        while(turnNum < numTurn){
            roundUpdate();
            
            for(int i = 0; i < players.length; i++){
                playerID = i;
                int end = takeTurn(players[i]);
                if (end == 1)
                    return;
            }
            
            for(int i = 0; i < civs.length;i++){
                if(!civs[i].human)
                    takeTurn(i);
            }
            yearIncrement();
            turnNum++;
        }
        
        gameResults();
    }
    
    
    void roundUpdate(){
        for(int i = 0; i < trades.size(); i++){
            if(trades.get(i).timer < 1 
                    || landArea(trades.get(i).playerA) < 1
                    || landArea(trades.get(i).playerB) < 1)
                cancelTrade(i);
            else
                trades.get(i).timer--;
        }
        
        for(int i = 0; i < civs.length; i++){
            civs[i].food += civs[i].dFood;
            civs[i].gold += civs[i].dGold;
            civs[i].mil += civs[i].dMil;
        }
        
        for(int x = 0; x < 18; x++){
            for(int y = 0; y < 18; y++){
                map.map[y][x].move = true;
            }
        }
    }
    
    
    int takeTurn(int player){
        Menu menu = new Menu();
        
        if(landArea(player) == 0)
            return 0;
        
        if(!civs[player].human){
            //AI goes HERE!
            //Random rand = new Random();
            //building
            while(civs[player].canBuild && (civs[player].gold >= civs[player].farmCost || civs[player].gold >= civs[player].mineCost || civs[player].gold >= civs[player].baseCost)){
                if(civs[player].dGold < civs[player].incomeMin && civs[player].gold >= civs[player].mineCost){
                    chooseRandomSafeSpotInNation(player);
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "^";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                    civs[player].dGold += civs[player].mineOut;
                    civs[player].gold -= civs[player].mineCost;
                }else{
                    int choice = rand1.nextInt(3);
                    double prob = rand1.nextDouble();
                    chooseRandomSafeSpotInNation(player);
                    switch(choice){
                        case 0: //farm
                            if(prob < civs[player].agressiveness){
                                if(civs[player].gold >= civs[player].baseCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "@";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                                    civs[player].dMil += civs[player].baseOut;
                                    civs[player].gold -= civs[player].baseCost;
                                }
                            }else{
                                if(civs[player].gold >= civs[player].farmCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "#";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("#");
                                    civs[player].dFood += civs[player].farmOut;
                                    civs[player].gold -= civs[player].farmCost;
                                }
                            }
                            break;
                        case 1: //mine
                            if(prob < civs[player].agressiveness){
                                if(civs[player].gold >= civs[player].baseCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "@";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                                    civs[player].dMil += civs[player].baseOut;
                                    civs[player].gold -= civs[player].baseCost;
                                }
                            }else{
                                if(civs[player].gold >= civs[player].mineCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "^";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                                    civs[player].dGold += civs[player].mineOut;
                                    civs[player].gold -= civs[player].mineCost;
                                }
                            }
                            break;
                        case 2: //base
                            if(prob > civs[player].agressiveness){
                                if(civs[player].gold >= civs[player].baseCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "@";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                                    civs[player].dMil += civs[player].baseOut;
                                    civs[player].gold -= civs[player].baseCost;
                                }
                            }else{
                                if(civs[player].gold >= civs[player].farmCost){
                                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                        map.map[lookY][lookX].buildings = "#";
                                    else
                                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("#");
                                    civs[player].dFood += civs[player].farmOut;
                                    civs[player].gold -= civs[player].farmCost;
                                }
                            }
                            break;
                        default:
                    }
                }
            }
            
            if(civs[player].gold >= civs[player].navyCost && numNavies(player) < civs[player].navyMin){//navies
                int total = 0;
                for(int i = 0; i < 18; i++){
                    for(int j = 0; j < 18; j++){
                        if(canBuildNavy(player, i, j)){
                            total++;
                        }
                    }
                }
                Random rand = new Random();
                int num = 0;
                if(total > 0)
                    num = rand.nextInt(total);
                int i, j;
                for(i = 0; i < 18; i++){
                    for(j = 0; j < 18; j++){
                        if(canBuildNavy(player, i, j)){
                            num--;
                            if(total > 0 && num < 1 && canBuildNavy(player, i, j)){
                                break;
                            }
                        }
                    }
                    if(j < 18 && total > 0 && num < 1 && canBuildNavy(player, i, j)){
                        map.map[j][i].navyID = player;
                        civs[player].gold -= civs[player].navyCost;
                        break;
                    }
                }
                
            }
            
            //Attacking
            Random rand = new Random();
            for(int x = 0; x < 18; x++){
                for(int y = 0; y < 18; y++){
                    if(canBeAttacked(player, x, y)){
                        if(map.map[y][x].pol != '0' && map.map[y][x].type != -1) 
                        if(map.map[y][x].ownerID < 0){
                            map.map[y][x].ownerID = player;
                            if(civs[player].gainsFromLand){
                                civs[player].dFood += 1;
                                civs[player].dGold += 1;
                                civs[player].dMil += 1;
                            }
                            if(map.map[y][x].type == 1)
                                map.map[y][x].pol = civs[player].sym;
                            else
                                map.map[y][x].pol = Character.toLowerCase(civs[player].sym);
                            map.map[y][x].move = false;
                        }else if(rand.nextDouble() < civs[player].agressiveness){
                            double calcCost = calcCost(player, map.map[y][x].buildings, true);
                            double eCalcCost = calcCost(map.map[y][x].ownerID, map.map[y][x].buildings, false);
                            if(eCalcCost < calcCost){
                                //attack!
                                int cost = 0;
                                int eCost = 0;
                                int batCost = 0;
                                while(civs[player].mil >= 2 && cost < calcCost){
                                    
                                    if(!civs[map.map[y][x].ownerID].human && (eCost > eCalcCost || civs[map.map[y][x].ownerID].mil <= 1)){
                                        getTerr(player, x, y);
                                        break;
                                    }else if(civs[map.map[y][x].ownerID].human){
                                        menu.clearScreen();
                                        int input = 0;
                                        Color c = new Color();
                                        int attacker = player;
                                        int defender = map.map[y][x].ownerID;
                                        while(input == 0){
                                        String[] sidemenu = {
                                            "DEFENDING against " + c.civColor(player) + civs[player].name + c.RESET,
                                            "",
                                            "Buildings: " + map.map[y][x].buildings,
                                            c.LRED + "Attacker's Units: " + civs[attacker].mil + c.RESET,
                                            c.LGREEN + "Defender's Units: " + civs[defender].mil + c.RESET,
                                            "",
                                            c.RED + "Attack Bonuses: " + civs[attacker].attackBonus1 + " " + civs[attacker].attackBonus2 + " " + civs[attacker].attackBonus3 + c.RESET,
                                            c.GREEN + "Defense Bonuses: " + civs[defender].defendBonus1 + " " + civs[defender].defendBonus2 + c.RESET,
                                            "",
                                            c.GREEN + "d- defend",
                                            c.WHITE + "r- retreat",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            "",
                                            ""
                                            };
                                        display(map.map[y][x].ownerID, sidemenu, 2, true, x, y);
                                        String input1 = System.console().readLine();
                                        if(input1.length() > 0)
                                            input = input1.charAt(0);
                                        else
                                            input = ' ';
                                        if(input == 'd'){
                                            
                                        }else if(input == 'r'){
                                            
                                        }else{
                                            input = 0;
                                        }
                                    }
                                        if(input == 'r' || civs[map.map[y][x].ownerID].mil < 2){
                                            getTerr(player, x, y);
                                            break;
                                        }
                                    }
                                    
                                    batCost = attackRound(player, map.map[y][x].ownerID, (numBases(map.map[y][x].buildings) > 0));
                                    cost += batCost;
                                    eCost += 2 - batCost;
                                }
                            }
                        }
                    }
                }
            }
            
            //Moving Navies
            for(int x = 0; x < 18; x++){
                for(int y = 0; y < 18; y++){
                    if(map.map[y][x].navyID == player && map.map[y][x].move){
                        int total = 0;
                        for(int i = 0; i < 18; i++){
                            for(int j = 0; j < 18; j++){
                                if(map.connectedByWater(x, y, i, j, civs[player].navyLimit))
                                    total++;
                            }
                        }
                        int move = 0;
                        if(total > 0)
                            move = rand.nextInt(total);
                        int i, j;
                        for(i = 0; i < 18; i++){
                            for(j = 0; j < 18; j++){
                                if(map.connectedByWater(x, y, i, j, civs[player].navyLimit)){
                                    move--;
                                    if(move < 1){
                                        break;
                                    }
                                }
                            }
                            if(j < 18 && total > 0 && move < 1 && map.connectedByWater(x, y, i, j, civs[player].navyLimit)){
                                if(map.map[j][i].navyID < 0){
                                    map.map[y][x].navyID = -1;
                                    map.map[j][i].navyID = player;
                                }else{
                                    int res = attackRound(player, map.map[j][i].navyID, false);
                                    if(civs[map.map[j][i].navyID].mil < 0)
                                        civs[map.map[j][i].navyID].mil = 0;
                                    if(civs[player].mil < 0)
                                        civs[player].mil = 0;
                                    if(res == 0){
                                        map.map[y][x].navyID = -1;
                                        map.map[j][i].navyID = player;
                                    }else if(res == 2){
                                        map.map[y][x].navyID = -1;
                                    }
                                }
                                map.map[j][i].move = false;
                                break;
                            }
                        }
                    }
                }
            }
            
            return 0;
        }
        
        lookX = civs[player].capX;
        lookY = civs[player].capY;
        int input = 0;
        
        //String[] sidemenu = {"w- up", "d- right", "s- down", "a- left"};
            while(input != 'z'){
            menu.clearScreen();
            String[] sidemenu = {
                "a- look around",
                "b- build",
                "t- trade",
                "c- conquer",
                "n- move navy",
                "v- view nations",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "z-end turn",
                "q-save and quit game",
                };
            display(player, sidemenu, 0, false, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'a')
                lookAround(player);
            if(input == 'c')
                if(civs[player].mil >= 2)
                    attackTerritory(player);
            if(input == 'b')
                buildBuilding(player);
            if(input == 'n')
                moveNavy(player);
            if(input == 't')
                conductTrade(player);
            if(input == 'v')
                viewNations(player);
            if(input == 'q')
                return 1;
            
            }
        return 0;
    }
    
    int countPoints(int player){
        civs[player].bonusPoints += 
                (unitedCulture(player) ? civs[player].unitedCultureBonus : 0)
                + (droveOutColonizers(player) ? civs[player].droveOutColonizersBonus : 0);
        
        return civs[player].food + civs[player].bonusPoints 
                + (mostIncome(player) ? civs[player].mostIncomeBonus : 0)
                + (mostLand(player) ? civs[player].mostLandBonus : 0);
    }
    
    boolean mostIncome(int player){
        boolean ans = true;
        for(int i = 0; i < civs.length; i++){
            if(civs[i].dGold > civs[player].dGold)
                ans = false;
        }
        return ans;
    }
    
    boolean mostLand(int player){
        boolean ans = true;
        for(int i = 0; i < civs.length; i++){
            if(landArea(i) > landArea(player))
                ans = false;
        }
        return ans;
    }
    
    int[] findWinners(int[] results){
        int[] winners = new int[results.length];
        for(int i = 0; i < winners.length; i++){
            int highest = 0;
            
            for(int j = 0; j < results.length;j++)
                if(results[highest] < results[j])
                    highest = j;
            
            winners[i] = highest;
            results[highest] = Integer.MIN_VALUE;
        }
        return winners;
    }
    
    void gameResults(){
        Color c = new Color();
        int [] results = new int[civs.length];
        int [] results1;
        for(int i = 0; i < civs.length; i++){
            results[i] = countPoints(i);
        }
        Menu menu = new Menu();
        results1 = results.clone();
        int [] winners = findWinners(results);
        menu.clearScreen();
        
        int maxLength = 0;
        int maxLengthIndex = 0;
        for(int i = 0; i < civs.length; i++){
            if(civs[maxLengthIndex].name.length() < civs[i].name.length()){
                maxLengthIndex = i;
                maxLength = civs[i].name.length();
            }
        }
        System.out.print("#  Nation");
        for(int j = 0; j < (maxLength - 6); j++)
                System.out.print(" ");
        System.out.println(" points" + c.LGREEN + " Food" + c.YELLOW + " Income" + c.LRED + " Area" + c.LCYAN + " Bonus" + c.RESET);
        
        for(int i = 0; i < winners.length; i++){
            System.out.print((i + 1) + ( (i + 1) < 10 ? "  " : " ") 
                    + c.civColor(winners[i]) + civs[winners[i]].name + c.RESET);
            
            for(int j = 0; j < (maxLength - civs[winners[i]].name.length()); j++)
                System.out.print(" ");
            
            System.out.print("  " + results1[winners[i]]);
            
            System.out.println("  "
                    + c.LGREEN + civs[winners[i]].food + c.RESET + "  "
                    + (mostIncome(winners[i]) ? c.BYELLOW + c.BLACK : c.LYELLOW) + "+" + civs[winners[i]].dGold + c.RESET + "  "
                    + (mostLand(winners[i]) ? c.BRED + c.BLACK : c.LRED) + landArea(winners[i]) + c.RESET + "  "
                    + c.LCYAN + civs[winners[i]].bonusPoints + c.RESET + " ");
        }
        System.out.println("\nPress enter to quit...");
        System.console().readLine();
    }
    
    void quitGame(){
        
    }
    
    void exportGame(){
        
    }
    
    void importGame(){
        
    }
    
    void lookAround(int player){
        int input = 0;
        int mode = 0;
        int seeplayer = player;
        Color c = new Color();
        Menu menu = new Menu();
            while(input != 'q'){
            menu.clearScreen();
            String[] sidemenu = {
                "",
                (map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller"),
                map.map[lookY][lookX].type == -1 || map.map[lookY][lookX].type == 0 ?
                    (map.map[lookY][lookX].navyID >= 0 ? "Navy: " + civs[map.map[lookY][lookX].navyID].name : "") : "",
                "Buildings: " + map.map[lookY][lookX].buildings,
                "Culture: " + map.map[lookY][lookX].culture,
                "w- up", 
                "d- right", 
                "s- down", 
                "a- left",
                "0- Political Mode",
                "1- Building Mode",
                "2- Military Mode",
                "3- Culture Mode",//3-Safety Mode",
                "",
                "", //map.map[lookY][lookX].ownerID >= 0 ? ("CalcCost: " + calcCost(map.map[lookY][lookX].ownerID, map.map[lookY][lookX].buildings, false)) : "",
                "", //map.map[lookY][lookX].ownerID >= 0 ? ("Current: " + c.GREEN + civs[map.map[lookY][lookX].ownerID].food + c.YELLOW + civs[map.map[lookY][lookX].ownerID].gold + c.RED + civs[map.map[lookY][lookX].ownerID].mil + c.RESET) : "",
                "", //map.map[lookY][lookX].ownerID >= 0 ? ("Gain: " + c.GREEN + civs[map.map[lookY][lookX].ownerID].dFood + c.YELLOW + civs[map.map[lookY][lookX].ownerID].dGold + c.RED + civs[map.map[lookY][lookX].ownerID].dMil + c.RESET) : "",
                "q- go back to menu"
                };
            //Color c = new Color();
            if(map.map[lookY][lookX].type == -1)
                sidemenu[0] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[0] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[0] = c.GREEN + "Land" + c.RESET;
            display(seeplayer, sidemenu, mode, true, lookX, lookY);
            String input1 = System.console().readLine();
            if(input1.length() > 0)
                input = input1.charAt(0);
            else
                input = ' ';
            //input = System.in.read();
            if(input == 'w')
                moveLooker(0);
            if(input == 'd')
                moveLooker(1);
            if(input == 's')
                moveLooker(2);
            if(input == 'a')
                moveLooker(3);
            if(input == '0')
                mode = 0;
            if(input == '1')
                mode = 1;
            if(input == '2')
                mode = 2;
            if(input == '3')
                mode = 3;
//            if(input == 'l' && seeplayer < civs.length - 1)
//                seeplayer++;
//            if(input == 'k' && seeplayer > 0)
//                seeplayer--;
            
            }
        
    }
    
    void monthIncrement(){
        if(month == 12){
            month = 1;
            yearIncrement();
        }else
            month++;
    }
    
    void yearIncrement(){
        switch (era) {
            case "BC":
                year--;
                if(year == 0)
                    era = "AD";
                break;
            case "AD":
                year++;
                break;
        }
    }
    
    void displayDate(){
        String mon;
        switch(month){
            case 1:
                mon = "JAN";
                break;
            case 2:
                mon = "FEB";
                break;
            case 3:
                mon = "MAR";
                break;
            case 4:
                mon = "APR";
                break;
            case 5:
                mon = "MAY";
                break;
            case 6:
                mon = "JUN";
                break;
            case 7:
                mon = "JUL";
                break;
            case 8:
                mon = "AUG";
                break;
            case 9:
                mon = "SEP";
                break;
            case 10:
                mon = "OCT";
                break;
            case 11:
                mon = "NOV";
                break;
            case 12:
                mon = "DEC";
                break;
            default:
                mon = "MON";
                break;
        }
        System.out.print(mon + ", " + year + " " + era);
    }
    
    void moveLooker(int dir){
        //0 up, 1, right, 2 down, 3 left
        switch(dir){
            case 0:
                if(lookY > 0)
                    lookY--;
                break;
            case 1:
                if(lookX < 17)
                    lookX++;
                break;
            case 2:
                if(lookY < 17)
                    lookY++;
                break;
            case 3:
                if(lookX > 0)
                    lookX--;
                break;
        }
    }
    
    boolean boardersPlayer(int player, int X, int Y){
        if(map.map[Y][X].ownerID != player && map.map[lookY][lookX].type != -1
          && ((X - 1 >= 0 && map.map[Y][X - 1].ownerID == player)
                  || (X + 1 <= 17 && map.map[Y][X + 1].ownerID == player)
                  || (Y - 1 >= 0 && map.map[Y - 1][X].ownerID == player)
                  || (Y + 1 <= 17 && map.map[Y + 1][X].ownerID == player))){
              
            return true;
        }else
            return false;
    }
    
    boolean canBeAttacked(int player, int X, int Y){
        if(canBeAttackedByNavy(player, X, Y))
            return true;
        
        if(map.map[Y][X].ownerID != player && map.map[Y][X].type != -1 && civs[player].mil >= 2
          && ((X - 1 >= 0 && map.map[Y][X - 1].ownerID == player && map.map[Y][X - 1].move)
                  || (X + 1 <= 17 && map.map[Y][X + 1].ownerID == player && map.map[Y][X + 1].move)
                  || (Y - 1 >= 0 && map.map[Y - 1][X].ownerID == player && map.map[Y - 1][X].move)
                  || (Y + 1 <= 17 && map.map[Y + 1][X].ownerID == player && map.map[Y + 1][X].move))){
              
            return true;
        }else
            return false;
    }
    
    boolean canBeAttackedByNavy(int player, int X, int Y){
        if(map.map[Y][X].ownerID != player && map.map[Y][X].type != -1 && civs[player].mil >= 2
          && ((X - 1 >= 0 && map.map[Y][X - 1].navyID == player && map.map[Y][X - 1].move)
                  || (X + 1 <= 17 && map.map[Y][X + 1].navyID == player && map.map[Y][X + 1].move)
                  || (Y - 1 >= 0 && map.map[Y - 1][X].navyID == player && map.map[Y - 1][X].move)
                  || (Y + 1 <= 17 && map.map[Y + 1][X].navyID == player && map.map[Y + 1][X].move)
                  || (map.map[Y][X].navyID == player && map.map[Y][X].move))){
              
            return true;
        }else
            return false;
    }
    
    boolean canBuildNavy(int player, int X, int Y){
        if(map.map[Y][X].type == -1 && map.onMap(X, Y)
          && ((X - 1 >= 0 && map.map[Y][X - 1].ownerID == player && numBases(map.map[Y][X - 1].buildings) > 0)
                  || (X + 1 <= 17 && map.map[Y][X + 1].ownerID == player && numBases(map.map[Y][X + 1].buildings) > 0)
                  || (Y - 1 >= 0 && map.map[Y - 1][X].ownerID == player && numBases(map.map[Y - 1][X].buildings) > 0)
                  || (Y + 1 <= 17 && map.map[Y + 1][X].ownerID == player && numBases(map.map[Y + 1][X].buildings) > 0))){
              
            return true;
        }else
            return false;
    }
    
    int landArea(int player){
        int area = 0;
        
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[j][i].ownerID == player)
                    area++;
            }
        }
        
        return area;
    }
    
    int totalBuildings(int player){
        int total = 0;
        
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[j][i].ownerID == player){
                    total += numFarms(map.map[j][i].buildings) 
                            + numMines(map.map[j][i].buildings) 
                            + numBases(map.map[j][i].buildings);
                    if(map.map[j][i].buildings.charAt(0) == '*')
                        total++;
                }
            }
        }
        
        return total;
    }
    
    int[][] safetyMap(int player){
        int[][] safe = new int[18][18];
        int[][] danger = new int[18][18];
        
        for(int i = 0; i < 18; i++)
            for(int j = 0; j < 18; j++)
                if(map.map[i][j].ownerID >0 && map.map[i][j].ownerID != player)
                    danger[i][j] = 1;
                else
                    danger[i][j] = 0;
        
        for(int k = 0; k < 36; k++){
            for(int i = 0; i < 18; i++)
                for(int j = 0; j < 18; j++)
                    if(danger[i][j] == 0 && map.map[i][j].type != -1 &&
                            ( (j > 0 && danger[i][j - 1] == 1)
                            || (j < 17 && danger[i][j + 1] >= 1)
                            || (i > 0 && danger[i - 1][j] >= 1)
                            || (i < 17 && danger[i + 1][j] >= 1)))
                        danger[i][j] = 1;
        }
        
        for(int i = 0; i < 18; i++)
            for(int j = 0; j < 18; j++)
                if(map.map[i][j].type != -1 && map.map[i][j].ownerID == player)
                    safe[i][j] = 1;
                else
                    safe[i][j] = 0;
        
        boolean done = false;
        int level = 1;
        
        while(!done && level < 36){
            done = true;
            for(int x = 0; x < 18; x++)
                for(int y = 0; y < 18; y++)
                    if(safe[y][x] == level
                            && (x == 0 || map.map[y][x - 1].ownerID < 0)
                            && (x == 17 || map.map[y][x + 1].ownerID < 0)
                            && (y == 0 || map.map[y - 1][x].ownerID < 0)
                            && (y == 17 || map.map[y + 1][x].ownerID < 0)){
                        safe[y][x] = 1;
                        continue;
                    }
                    else if(safe[y][x] == level && danger[y][x] == 1
                            && (x == 0 || safe[y][x - 1] >= level || map.map[y][x - 1].ownerID < 0)
                            && (x == 17 || safe[y][x + 1] >= level || map.map[y][x + 1].ownerID < 0)
                            && (y == 0 || safe[y - 1][x] >= level  || map.map[y - 1][x].ownerID < 0)
                            && (y == 17 || safe[y + 1][x] >= level || map.map[y + 1][x].ownerID < 0)){
                        done = false;
                        safe[y][x] = level + 1;
                    }else if(safe[y][x] == level && danger[y][x] == 0
                            && (x == 0 || safe[y][x - 1] >= level)
                            && (x == 17 || safe[y][x + 1] >= level)
                            && (y == 0 || safe[y - 1][x] >= level)
                            && (y == 17 || safe[y + 1][x] >= level)){
                        done = false;
                        safe[y][x] = level + 1;
                    }
            level++;
        }
        
        return safe;
    }
    
    int findHighestonMap(int[][] mat){
        int max = 0;
        
        for(int i = 0; i < 18; i++)
            for(int j = 0; j < 18; j++)
                if(mat[i][j] > max)
                    max = mat[i][j];
        
        return max;
    }
    
    int findNumonMap(int[][] mat, int num){
        int total = 0;
        
        for(int i = 0; i < 18; i++)
            for(int j = 0; j < 18; j++)
                if(mat[i][j] == num)
                    total++;
        
        return total;
    }
    
    double calcCost(int player, String buildings, boolean attack){
        int[][] mat = safetyMap(player);
        int depth = findHighestonMap(mat) - 1;
        
        double calcCost = ((double)civs[player].mil + (double)(depth * civs[player].dMil))/(double)(landArea(player) + (attack ? 1 : 0) + totalBuildings(player));      
        //double calcCost = ((double)civs[player].mil)/(double)(landArea(player) + (attack ? 1 : 0) + totalBuildings(player));
            calcCost += (buildings.charAt(0) == '*' ? calcCost : 0)  
                    + calcCost*((double)numFarms(buildings))
                    + calcCost*((double)numMines(buildings))
                    + calcCost*((double)numBases(buildings));
            
            return calcCost;
    }
    
    int numFarms(String str){
        int ans = 0;
        int index = 0;
        while(-1 != (index = str.indexOf('#', index))){
            ans++;
            index++;
        }
        return ans;
    }
    
    int numMines(String str){
        int ans = 0;
        int index = 0;
        while(-1 != (index = str.indexOf('^', index))){
            ans++;
            index++;
        }
        return ans;
    }
    
    int numBases(String str){
        int ans = 0;
        int index = 0;
        while(-1 != (index = str.indexOf('@', index))){
            ans++;
            index++;
        }
        return ans;
    }
    
    int numNavies(int player){
        int total = 0;
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[j][i].navyID == player)
                    total++;
            }
        }
        
        return total;
    }
    
    void chooseRandomSpotInNation(int player){
        //Random rand = new Random();
        int spot = rand1.nextInt(landArea(player)) + 1;
        
        for(lookX = 0; lookX < 18; lookX++){
            for(lookY = 0; lookY < 18; lookY++){
                if(map.map[lookY][lookX].ownerID == player)
                    spot--;
                if(spot < 1)
                    break;
            }
            if(lookY < 18 && map.map[lookY][lookX].ownerID == player)
                spot--;
            if(spot < 1)
                break;
        }
    }
    
    void chooseRandomSafeSpotInNation(int player){
        //Random rand = new Random();
        int[][] mat = safetyMap(player);
        int max = findHighestonMap(mat);
        int num = findNumonMap(mat, max);
        
        int spot = rand1.nextInt(num) + 1;
        
        for(lookX = 0; lookX < 18; lookX++){
            for(lookY = 0; lookY < 18; lookY++){
                if(mat[lookY][lookX] == max)
                    spot--;
                if(spot < 1)
                    break;
            }
            if(lookY < 18 && mat[lookY][lookX] == max)
                spot--;
            if(spot < 1)
                break;
        }
    }
    
    boolean unitedCulture(int ID){
        if(civs[ID].culture.matches(""))
            return false;
        
        String cul = civs[ID].culture;
        
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[i][j].culture.matches(cul) && map.map[i][j].ownerID != ID)
                    return false;
            }
        }
        
        return true;
    }
    
    void setGameType(){
        for(int i = 0; i < civs.length; i++){
            if(civs[i].attr.contains("Colonizer"))
                colonizationGame = true;
        }
    }
    
    boolean droveOutColonizers(int ID){
        if(!colonizationGame)
            return false;
        
        if(civs[ID].attr.contains("Colonizer"))
            return false;
        
        for(int i = 0; i < 18; i++){
            for(int j = 0; j < 18; j++){
                if(map.map[i][j].ownerID > -1 && civs[map.map[i][j].ownerID].attr.contains("Colonizer"))
                    return false;
            }
        }
        
        return true;
    }
    
    void display(int player, String[] list, int mode, boolean showLooker, int x, int y){
        Color c = new Color();
        System.out.println(c.civColor(player) + civs[player].name + c.RESET);
        System.out.print(c.LGREEN + "FOOD " + civs[player].food + c.RESET + " ");
        if(civs[player].dFood >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dFood + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dFood + c.RESET + "  ");
        System.out.print(c.LYELLOW + "GOLD " + civs[player].gold + c.RESET + " ");
        if(civs[player].dGold >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dGold + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dGold + c.RESET + "  ");
        System.out.print(c.LRED + "ARMY " + civs[player].mil + c.RESET + " ");
        if(civs[player].dMil >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dMil + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dMil + c.RESET + "  ");
        System.out.println();
        displayDate();
        System.out.print("  " + (turnNum + 1) + "/" + numTurn);
        System.out.println();
        System.out.println("========================================================");
        //if(mode != 3){
            for(int i = 0; i < 18; i++){
                if(showLooker)
                    map.displayLine(i, mode, x, y);
                else
                    map.displayLine(i, mode);
                if(i < list.length)
                    System.out.println("||" + list[i]);
                else
                    System.out.println("||");
            }
//        }else{
//            int[][] mat = safetyMap(player);
//            for(int i = 0; i < 18; i++){
//                for(int j = 0; j < 18; j++)
//                    System.out.print( (map.map[i][j].type == -1 ? c.BLUE : "") + mat[i][j] + " " + c.RESET);
//                if(i < list.length)
//                    System.out.println("||" + list[i]);
//                else
//                    System.out.println("||");
//            }
//        }
        System.out.println("========================================================");
    }
    
    void displayNavy(int player, String[] list, int mode, boolean showLooker, int x, int y, int navX, int navY, int limit){
        Color c = new Color();
        System.out.println(c.civColor(player) + civs[player].name + c.RESET);
        System.out.print(c.LGREEN + "FOOD " + civs[player].food + c.RESET + " ");
        if(civs[player].dFood >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dFood + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dFood + c.RESET + "  ");
        System.out.print(c.LYELLOW + "GOLD " + civs[player].gold + c.RESET + " ");
        if(civs[player].dGold >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dGold + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dGold + c.RESET + "  ");
        System.out.print(c.LRED + "ARMY " + civs[player].mil + c.RESET + " ");
        if(civs[player].dMil >= 0)
            System.out.print(c.GREEN + "+" + civs[player].dMil + c.RESET + "  ");
        else
            System.out.print(c.RED + civs[player].dMil + c.RESET + "  ");
        System.out.println();
        displayDate();
        System.out.print("  " + (turnNum + 1) + "/" + numTurn);
        System.out.println();
        System.out.println("========================================================");
        for(int i = 0; i < 18; i++){
            if(showLooker)
                map.displayLineNavyRange(i, x, y, navX, navY, limit);
            else
                map.displayLine(i, mode);
            if(i < list.length)
                System.out.println("||" + list[i]);
            else
                System.out.println("||");
        }
        System.out.println("========================================================");
    }
}
