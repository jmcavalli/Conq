/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.ArrayList;

/**
 *
 * @author bible_000
 */

class Trade {
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

public class Game {
    
    Map map;
    int year;
    int month;
    String era;
    //int playerID;
    int [] players;
    Civ[] civs;
    ArrayList<Trade> trades = new ArrayList();
    int lookX;
    int lookY;
    int turnNum;
    int numTurn;
    Random rand1 = new Random();
    
    
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
        }
        map = new Map();
        map.setup(loader, loc, time);
        year = map.year;
        month = 1;
        era = map.era;
        //playerID = civ;
        players = chooseNations(loader, loc, time);
        if(players[0] == -1)
            return -1;
        return 0;
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
            chooseRandomSpotInNation(player);
            civs[player].capX = lookX;
            civs[player].capY = lookY;
            map.map[lookY][lookX].buildings = "*";
            return 0;
        }
        
        int input = 0;
        Menu menu = new Menu();
        for(lookX = 0; lookX < 17; lookX++){
            for(lookY = 0; lookY < 17; lookY++){
                if(map.map[lookY][lookX].ownerID == player)
                    break;
            }
            if(map.map[lookY][lookX].ownerID == player)
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
        civs[previous].dFood -= numFarms(map.map[terrY][terrX].buildings) * 10;
        civs[recieverID].dFood += numFarms(map.map[terrY][terrX].buildings) * 10;
        
        civs[previous].dGold -= numMines(map.map[terrY][terrX].buildings) * 10;
        civs[recieverID].dGold += numMines(map.map[terrY][terrX].buildings) * 10;
        
        civs[previous].dMil -= numBases(map.map[terrY][terrX].buildings) * 10;
        civs[recieverID].dMil += numBases(map.map[terrY][terrX].buildings) * 10;
        
        if(map.map[terrY][terrX].buildings.contains("*")){
            civs[recieverID].bonusPoints += 20;
            civs[previous].bonusPoints -= 20;
        }
    }
    
    int dieRoll(int sides){
        int ans = 0;
        Random rand = new Random();
        
        ans = rand.nextInt(sides) + 1;
        return ans;
    }
    int attackRound(int attackID, int defendID, boolean fort){
        int [] attacker = {dieRoll(6), dieRoll(6), dieRoll(6)};
        int [] defender = {dieRoll(6), dieRoll(6)};
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
            civs[defendID].mil--;
        else{
            civs[attackID].mil--;
            result++;
        }
        
        if(attacker[1] > defender[1])
            civs[defendID].mil--;
        else{
            civs[attackID].mil--;
            result++;
        }
            return result;
    }
    boolean battle(int attacker, int defender, boolean base){
        //true = attacker wins; false = defender wins
        if(civs[defender].human){
        int input = 0;
        Menu menu = new Menu();
        while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "BATTLE",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                c.LRED + "Attacker's Units: " + civs[attacker].mil,
                c.LGREEN + "Defender's Units: " + civs[defender].mil + c.RESET,
                "", 
                "", 
                "", 
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
            int calcCost = civs[defender].mil/landArea(defender);
        int input = 0;
        Menu menu = new Menu();
        while(input != 'q'){
            menu.clearScreen();
            Color c = new Color();
            String[] sidemenu = {
                "BATTLE",
                "",
                map.map[lookY][lookX].ownerID >= 0 ? "Controlled by: " + civs[map.map[lookY][lookX].ownerID].name : "No controller",
                c.LRED + "Attacker's Units: " + civs[attacker].mil,
                c.LGREEN + "Defender's Units: " + civs[defender].mil + c.RESET,
                "", 
                "", 
                "", 
                "",
                "",
                "",
                c.LRED + "a- ATTACK" + c.RESET,
                "",
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
            if(input == 'q')
                return false;
            
            
            }
        
        return true;
        }
    }
    
    void conquer(int player, int terrX, int terrY){
        boolean winner = battle(player, map.map[terrY][terrX].ownerID, (numBases(map.map[terrY][terrX].buildings) > 0));
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
                (map.map[lookY][lookX].ownerID == player  && civs[player].gold >= 100
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LGREEN + "#- Farm" + c.RESET)
                    : (c.RED + "#- Farm (You need to place it in your own territory and have 100 gold)" + c.RESET)),
                (map.map[lookY][lookX].ownerID == player  && civs[player].gold >= 100
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LYELLOW + "^- Mine" + c.RESET)
                    : (c.RED + "^- Mine (You need to place it in your own territory and have 100 gold)" + c.RESET)),
                (map.map[lookY][lookX].ownerID == player  && civs[player].gold >= 100
                    && map.map[lookY][lookX].type != -1 
                    ? (c.LRED + "@- Fort (Can make navies in sea tiles adjacent to @)" + c.RESET)
                    : (c.RED + "@- Fort (You need to place it in your own territory and have 100 gold)" + c.RESET)),
                (canBuildNavy(player, lookX, lookY)   && map.map[lookY][lookX].navyID == -1
                    && civs[player].gold >= 10
                    ? (c.LBLUE + "n- Navy" + c.RESET)
                    : (c.RED + "n- Navy (You need to build it in a sea tile adjacent to a @ and have 10 gold)" + c.RESET)),
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
                if(map.map[lookY][lookX].ownerID == player && civs[player].gold >= 100){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "#";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("#");
                    civs[player].dFood += 10;
                    civs[player].gold -= 100;
                }
            }
            if(input == '^'){
                if(map.map[lookY][lookX].ownerID == player && civs[player].gold >= 100){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "^";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                    civs[player].dGold += 10;
                    civs[player].gold -= 100;
                }
            }
            if(input == '@'){
                if(map.map[lookY][lookX].ownerID == player && civs[player].gold >= 100){
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "@";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                    civs[player].dMil += 10;
                    civs[player].gold -= 100;
                }
            }
            if(input == 'n'){
                if(map.map[lookY][lookX].type == -1 && map.map[lookY][lookX].navyID == -1 && civs[player].gold >= 10){
                    map.map[lookY][lookX].navyID = player;
                    civs[player].gold -= 10;
                }
            }
            
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
                    int limit = 3;
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
                map.map[lookY][lookX].ownerID > -1 && civs[map.map[lookY][lookX].ownerID].human && map.map[lookY][lookX].ownerID != player ? (Bconf ? c.GREEN + "Signed by player B" : c.RED + "p- player B confirm") : "",
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
            while(input <= 0){
            menu.clearScreen();
            System.out.println("Please select the number of turns (defult 10): ");
            try{
                input = Integer.parseInt(System.console().readLine());
            }catch(NumberFormatException e){
                input = 0;
            }
            
            }
            numTurn = input;
        
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
            for(int i = 0; i < civs.length;i++){
                if(!civs[i].human)
                    takeTurn(i);
            }
            
            for(int i = 0; i < players.length; i++){
                int end = takeTurn(players[i]);
                if (end == 1)
                    return;
            }
            yearIncrement();
            turnNum++;
        }
        
        gameResults();
    }
    
    
    void roundUpdate(){
        for(int i = 0; i < trades.size(); i++){
            if(trades.get(i).timer < 1)
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
        if(landArea(player) == 0)
            return 0;
        
        if(!civs[player].human){
            //AI goes HERE!
            //Random rand = new Random();
            //building
            while(civs[player].gold >= 100){
                if(civs[player].dGold < 30){
                    chooseRandomSpotInNation(player);
                    if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                        map.map[lookY][lookX].buildings = "^";
                    else
                        map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                    civs[player].dGold += 10;
                    civs[player].gold -= 100;
                }else{
                    int choice = rand1.nextInt(3);
                    chooseRandomSpotInNation(player);
                    switch(choice){
                        case 0: //farm
                            if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                map.map[lookY][lookX].buildings = "#";
                            else
                                map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("#");
                            civs[player].dFood += 10;
                            civs[player].gold -= 100;
                            break;
                        case 1: //mine
                            if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                map.map[lookY][lookX].buildings = "^";
                            else
                                map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("^");
                            civs[player].dGold += 10;
                            civs[player].gold -= 100;
                            break;
                        case 2: //base
                            if(map.map[lookY][lookX].buildings.charAt(0) == '-')
                                map.map[lookY][lookX].buildings = "@";
                            else
                                map.map[lookY][lookX].buildings = map.map[lookY][lookX].buildings.concat("@");
                            civs[player].dMil += 10;
                            civs[player].gold -= 100;
                            break;
                        default:
                    }
                }
            }
            
            return 0;
        }
        
        lookX = civs[player].capX;
        lookY = civs[player].capY;
        int input = 0;
        Menu menu = new Menu();
        //String[] sidemenu = {"w- up", "d- right", "s- down", "a- left"};
        try {
            while(input != 'z'){
            menu.clearScreen();
            String[] sidemenu = {
                "a- look around",
                "b- build",
                "t- trade",
                "c- conquer",
                "n- move navy",
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
                "z-end turn",
                "q-quit game",
                };
            display(player, sidemenu, 0, false, lookX, lookY);
            input = System.in.read();
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
            if(input == 'q')
                return 1;
            
            }
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }
    
    int countPoints(int player){
        return civs[player].food + civs[player].bonusPoints 
                + (mostIncome(player) ? 50 : 0) + (mostLand(player) ? 50 : 0);
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
        System.out.println(" points" + c.LGREEN + " Food" + c.YELLOW + " Income" + c.LRED + " Area" + c.LCYAN + " Bonus");
        
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
                "",
                "",
                "",
                "",
                "",
                "q- go back to menu"
                };
            Color c = new Color();
            if(map.map[lookY][lookX].type == -1)
                sidemenu[0] = c.BLUE + "Water" + c.RESET;
            else if(map.map[lookY][lookX].type == 0)
                sidemenu[0] = c.LCYAN + "Coastal" + c.RESET;
            else
                sidemenu[0] = c.GREEN + "Land" + c.RESET;
            display(player, sidemenu, mode, true, lookX, lookY);
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
                  || (map.map[Y][X].navyID == player && map.map[Y + 1][X].move))){
              
            return true;
        }else
            return false;
    }
    
    boolean canBuildNavy(int player, int X, int Y){
        if(map.map[Y][X].type == -1
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
    
    void chooseRandomSpotInNation(int player){
        //Random rand = new Random();
        int spot = rand1.nextInt(landArea(player)) + 1;
        
        for(lookX = 0; lookX < 17; lookX++){
            for(lookY = 0; lookY < 17; lookY++){
                if(map.map[lookY][lookX].ownerID == player)
                    spot--;
                if(spot < 1)
                    break;
            }
            if(map.map[lookY][lookX].ownerID == player)
                spot--;
            if(spot < 1)
                break;
        }
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
        for(int i = 0; i < 18; i++){
            if(showLooker)
                map.displayLine(i, mode, lookX, lookY);
            else
                map.displayLine(i, mode);
            if(i < list.length)
                System.out.println("||" + list[i]);
            else
                System.out.println("||");
        }
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
                map.displayLineNavyRange(i, lookX, lookY, navX, navY, limit);
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
