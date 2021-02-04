/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballbreak;

/**
 *
 * @author georg
 */
public class BallBreaker {
    
    /**
     * main entry point of application
     * -> start game
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game ballbreaker = new Game();
        ballbreaker.RunGame();
    }
    
}
