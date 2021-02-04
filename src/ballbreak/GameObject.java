/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballbreak;

import java.awt.Point;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author georg
 */
public class GameObject {
    
    // constructor for balls, blocks
    GameObject(double _x, double _y, int _width, int _height, Color _col) {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        col = _col;
    }
    
    // constructor for splitter
    GameObject(double _x, double _y, int _width, int _height, Color _col, Point _direction, int _stepMax) {
        x = _x;
        y = _y;
        width = _width;
        height = _height;
        col = _col;
        directionVector = _direction;
        stepMax = _stepMax;
    }
    
    // balls, blocks, splitter
    public double x;
    public double y;
    public int width;
    public int height;
    public Color col;
    
    // splitter only
    public Point directionVector;
    public int step;
    public int stepMax;
    
    // create rectangle object from values for intersection checking
    public Rectangle2D toRectangle() {
        return new Rectangle2D.Double(x, y, width, height);
    }
}
