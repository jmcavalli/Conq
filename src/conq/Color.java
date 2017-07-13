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
public class Color {
    public final String RESET = "\u001B[0m";
    public final String BLACK = "\u001B[30m";
    public final String RED = "\u001B[31m";
    public final String GREEN = "\u001B[32m";
    public final String YELLOW = "\u001B[33m";
    public final String BLUE = "\u001B[34m";
    public final String PURPLE = "\u001B[35m";
    public final String CYAN = "\u001B[36m";
    public final String WHITE = "\u001B[37m";
    public final String BBLACK = "\u001B[40m";
    public final String BRED = "\u001B[41m";
    public final String BGREEN = "\u001B[42m";
    public final String BYELLOW = "\u001B[43m";
    public final String BBLUE = "\u001B[44m";
    public final String BPURPLE = "\u001B[45m";
    public final String BCYAN = "\u001B[46m";
    public final String BWHITE = "\u001B[47m";
    
    public final String LBLACK = "\u001B[30;1m";
    public final String LRED = "\u001B[31;1m";
    public final String LGREEN = "\u001B[32;1m";
    public final String LYELLOW = "\u001B[33;1m";
    public final String LBLUE = "\u001B[34;1m";
    public final String LPURPLE = "\u001B[35;1m";
    public final String LCYAN = "\u001B[36;1m";
    public final String LWHITE = "\u001B[37;1m";
    public final String BLBLACK = "\u001B[40;1m";
    public final String BLRED = "\u001B[41;1m";
    public final String BLGREEN = "\u001B[42;1m";
    public final String BLYELLOW = "\u001B[43;1m";
    public final String BLBLUE = "\u001B[44;1m";
    public final String BLPURPLE = "\u001B[45;1m";
    public final String BLCYAN = "\u001B[46;1m";
    public final String BLWHITE = "\u001B[47;1m";
    
    String civColor(int civ){
        if (civ == -1)
            return LBLACK + BBLACK;
        if (civ == -2)
            return LBLUE + BBLUE;
        
        switch(civ % 14){
            case 0:
                return RED;
            case 1:
                return GREEN;
            case 2:
                return YELLOW;
            case 3:
                return BLUE;
            case 4:
                return PURPLE;
            case 5:
                return CYAN;
            case 6:
                return WHITE;
            case 7:
                return LRED;
            case 8:
                return LGREEN;
            case 9:
                return LYELLOW;
            case 10:
                return LBLUE;
            case 11:
                return LPURPLE;
            case 12:
                return LCYAN;
            case 13:
                return LWHITE;
            default:
                return BLACK;
        }
    }
}
