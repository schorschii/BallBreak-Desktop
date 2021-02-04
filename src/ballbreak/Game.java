/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballbreak;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import javax.swing.JFrame;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;

/**
 *
 * @author georg
 */
public class Game {
    /***
     * Global Variables
     */
    Point mousePos = new Point(0, 0);
    Timer gameTimer = new Timer();
    GamePanel displayPanel;
    
    public ArrayList<GameObject> balls = new ArrayList();
    public ArrayList<GameObject> blocks = new ArrayList();
    public ArrayList<GameObject> splitter = new ArrayList();
    
    int balls_count = 1;
    int ballSpeed = 6;
    int defaultBallsize = 10;
    int ballsize = 10;
    int padding = 10;
    
    int defaultSpeed = 20;
    int speed = 20;
    
    int defaultCanvasSize = 400;
    
    int pointFontSize = 50;
    int maxPointFontSize = 100;
    
    public GameState gs = GameState.WelcomeScreen;
    public enum GameState {
        WelcomeScreen, Running, Pause, GameOver
    }
    
    
    Game() {
        displayPanel = new GamePanel(this);
    }
    
    /***
     * 
     * MATHEMATICS
     */
    
    // Generate random int between min and max
    public static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
    public static int randIntWithoutZero(int min, int max) {
        Random rand = new Random();
        while (true) {
            int result = rand.nextInt((max - min) + 1) + min;
            if (result != 0) {
                return result;
            }
        }
    }
    
    
    /***
     * 
     * GAME 
     */
    
    // Clear field and set new blocks and balls
    private void newBallSet(int amount) {
        // remove all balls and blocks
        balls.clear();
        blocks.clear();
        
        // create new balls and blocks as often as given in amount parameter
        int i; for (i=0; i!=amount; i++) {
            // set random color of the new ball
            Color c = new Color(randInt(40,255), randInt(40,255), randInt(40,255));
            
            // select side fro where the ball comes
            switch (randInt(1,4)) {
                case 1: // top
                    balls.add(new GameObject(randInt(0,displayPanel.getSize().width), 0, ballsize, ballsize, c));
                    break;
                case 2: // left
                    balls.add(new GameObject(0,randInt(0,displayPanel.getSize().height), ballsize, ballsize, c));
                    break;
                case 3: // bottom
                    balls.add(new GameObject(randInt(0,displayPanel.getSize().width),displayPanel.getSize().height, ballsize, ballsize, c));
                    break;
                case 4: // right
                    balls.add(new GameObject(displayPanel.getSize().width,randInt(0,displayPanel.getSize().height), ballsize, ballsize, c));
                    break;
            }
            
            // create same amount of blocks
            newRect();
        }
    }
    
    // add one new block which does not appear under the cursor
    private void newRect() {
        // blocks should be bigger than balls
        int blocksize = ballsize + 7;
        
        // random color
        Color c = new Color(randInt(40,255), randInt(40,255), randInt(40,255));
        
        // random location
        int b_x = randInt(padding, displayPanel.getSize().width - blocksize - (padding * 2));
        int b_y = randInt(padding, displayPanel.getSize().height - blocksize - (padding * 2));
        
        // rectangles for intersection tests
        Rectangle newBlock = new Rectangle(b_x, b_y, blocksize, blocksize);
        Rectangle mouseRect = new Rectangle(mousePos.x, mousePos.y, blocksize, blocksize);
        
        if (!(newBlock.intersects(mouseRect))) {
            // okay! found good new block
            blocks.add(new GameObject(b_x, b_y, blocksize, blocksize, c));
        } else {
            // try again, block is at current cursor position
            newRect();
        }
    }
    
    // reset and start a new game
    public void newGame() {
        balls_count = 1;
        newBallSet(balls_count);
        gameTimer.cancel(); gameTimer.purge();
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask() { @Override public void run() { update(); } }, 400, speed);
        gs = GameState.Running;
    }
    
    // update loop, called by the timer
    // move balls, check collision, ...
    private void update() {
        // mouse position, relative to the upper left corner of the window
        mousePos = SubtractVector(MouseInfo.getPointerInfo().getLocation(), displayPanel.getLocationOnScreen()); //content.mousePos;
        
        // balls which should be removed from the screen because of collision or end of lifetime
        ArrayList<GameObject> trash_balls = new ArrayList();
        ArrayList<GameObject> trash_splitter = new ArrayList();
        
        // check each ball
        out: for (GameObject ball: balls) {
            // create vectors
            Point stuetzvektor = new Point((int)(ball.x + (ball.width/2)), (int)(ball.y + (ball.height/2)));
            Point richtungsvektor = new Point(mousePos.x - stuetzvektor.x, mousePos.y - stuetzvektor.y);
            
            // check collision with each block and remove ball if collides with a block
            for (GameObject block: blocks) {
                if (block.toRectangle().intersects(ball.toRectangle())) {
                    trash_balls.add(ball);
                }
            }
            
            // check collision with mouse pointer and end game if so, otherwise move ball in cursor direction
            if (LengthVector(richtungsvektor) > ballsize) {
                richtungsvektor = NormalizeVector(richtungsvektor, ballSpeed);
                ball.x += richtungsvektor.x;
                ball.y += richtungsvektor.y;
            } else {
                gameTimer.cancel();
                gs = GameState.GameOver;
                break;
            }
        }
        
        // move splitter
        for (GameObject split: splitter) {
            if (split.step <= split.stepMax) {
                Point richtungsvektor = NormalizeVector(split.directionVector, Math.max((split.stepMax - split.step) / 4, 3));
                split.x += richtungsvektor.x;
                split.y += richtungsvektor.y;
                split.step += 1;
            } else {
                trash_splitter.add(split);
            }
        }
        
        // check collision of each block with mouse pointer
        for (GameObject block: blocks) {
            Rectangle2D mouseRect = new Rectangle2D.Double(mousePos.x, mousePos.y, 1, 1);
            if (block.toRectangle().intersects(mouseRect)) {
                gs = GameState.GameOver;
                gameTimer.cancel();
            }
        }
        
        // now drop all balls which reached their end of lifetime
        for (GameObject ball: trash_balls) {
            Color s_color = new Color(Math.round(ball.col.getRed()*0.7f), Math.round(ball.col.getGreen()*0.7f), Math.round(ball.col.getBlue()*0.7f));
            
            int i; for (i = 0; i < 10; i++) {
                splitter.add(new GameObject(ball.x, ball.y, ballsize / 2, ballsize / 2, s_color, new Point(randIntWithoutZero(-10,10), randIntWithoutZero(-10,10)), randInt(10,45)));
            }
            balls.remove(ball);
        }
        
        // now drop all splitter which reached their end of lifetime
        for (GameObject ball: trash_splitter) {
            splitter.remove(ball);
        }
        
        // Check if all balls collided with a block. If so, increment ball count and set new blocks and balls.
        if (balls.isEmpty()) {
            balls_count ++;
            newBallSet(balls_count);
            pointFontSize = 5;
        }
        
        if (pointFontSize < maxPointFontSize) { pointFontSize += 3; }
        
        // repaint elements on canvas
        displayPanel.repaint();
    }
    
    // vector operations
    private double LengthVector(Point p) { return Math.sqrt(Math.pow(p.x, 2)+Math.pow(p.y, 2)); }
    private Point NormalizeVector(Point p, double multiplicator) { double length = LengthVector(p); return new Point((int)((p.x/length)*multiplicator), (int)((p.y/length)*multiplicator)); }
    private Point DivideVector(Point p, double divisor) { return new Point((int)(p.x/divisor), (int)(p.y/divisor)); }
    private Point MultiplyVector(Point p, double divisor) { return new Point((int)(p.x*divisor), (int)(p.y*divisor)); }
    private Point SubtractVector(Point p1, Point p2) { return new Point(p1.x-p2.x, p1.y-p2.y); }
    
    
    // start the game window
    public void RunGame() {
        // change cursor
        displayPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        
        // initialize and set window title
        JFrame window = new JFrame("BallBreaker Java Edition 1.0 | (c) Georg Sieber 2016");
        
        // exit application if window was closed
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        
        // register window resised event for resizing balls and blocks
        ComponentListener comp = new ComponentListener() {
            @Override public void componentHidden(ComponentEvent e) {}
            @Override public void componentMoved(ComponentEvent e) {}
            @Override public void componentResized(ComponentEvent e) {
                int newCanvasSize = (displayPanel.getSize().width + displayPanel.getSize().height) / 2;
                double ratioCanvasSize = (float)newCanvasSize / defaultCanvasSize;
                
                ballsize = (int)(defaultBallsize * ratioCanvasSize);
                int newSpeed = (int)(defaultSpeed * (1 / Math.max(ratioCanvasSize / 2, 1)));
                speed = Math.max(newSpeed, 10);
                
                //System.out.println(speed);
                //System.out.println(defaultBallsize + " * " + ratioCanvasSize + " (" + newCanvasSize + "/" + defaultCanvasSize + ") = " + ballsize);
            }
            @Override public void componentShown(ComponentEvent e) {}
        };
        displayPanel.addComponentListener(comp);
        
        // set window properties
        displayPanel.setBackground(Color.black);
        window.setContentPane(displayPanel);
        window.setSize(defaultCanvasSize, defaultCanvasSize);
        //window.setLocation(100,100);
        centreWindow(window);
        window.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
        window.setVisible(true);
        defaultCanvasSize = (displayPanel.getSize().width + displayPanel.getSize().height) / 2;
        
        // start first game
        newGame();
    }
    
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }
}
