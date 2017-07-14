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
    
    int capX = 0;
    int capY = 0;
    
    int food = 0;
    int dFood = 0;
    
    int gold = 500;
    int dGold = 0;
    
    int mil = 50;
    int dMil = 0;
    
}
