/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conq;

import java.util.ArrayList;

/**
 *
 * @author bible_000
 */
public class Civ implements java.io.Serializable{
    public char sym;
    public String name;
    public ArrayList<String> attr = new ArrayList();
    boolean human = false;
    int bonusPoints = 0;
    double agressiveness;
    
    int capX = 0;
    int capY = 0;
    
    int food = 0;
    int dFood = 0;
    
    int gold = 500;
    int dGold = 0;
    
    int mil = 50;
    int dMil = 0;
    
    int farmCost = 100;
    int mineCost = 100;
    int baseCost = 100;
    int navyCost = 10;
    
    int farmOut = 10;
    int mineOut = 10;
    int baseOut = 10;
    
    int capitalPoints = 20;
    
    int dieSides = 6;
    int attackBonus1 = 0;
    int attackBonus2 = 0;
    int attackBonus3 = 0;
    
    int defendBonus1 = 0;
    int defendBonus2 = 0;
    
    int navyLimit = 3;
    int navyMin = 3;
    int incomeMin = 10;
    
    int mostLandBonus = 50;
    int mostIncomeBonus = 50;
    
}
