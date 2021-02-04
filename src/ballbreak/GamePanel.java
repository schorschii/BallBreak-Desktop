/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ballbreak;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import java.awt.RadialGradientPaint;
import java.awt.Font;
import java.awt.FontMetrics;

/**
 *
 * @author georg
 */
    class GamePanel extends JPanel implements MouseMotionListener, MouseListener {
        
        public Point mousePos = new Point(0,0);
        Game mainGame;
        
        GamePanel(Game _g) {
            addMouseMotionListener(this);
            addMouseListener(this);
            mainGame = _g;
        }
           
        @Override
        public void mouseClicked(MouseEvent e) {
            mainGame.newGame();
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            
        }
        
        @Override
        public void mouseEntered(MouseEvent e) {
            
        }
        @Override
        public void mousePressed(MouseEvent e) {
            
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            //mousePos = e.getPoint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mousePos = e.getPoint();
        }
    
      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         
         Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

         g2d.setColor(Color.BLACK);
         g2d.fillRect(0, 0, this.getSize().width, this.getSize().width);
         
         for (GameObject go: mainGame.splitter) {
            RadialGradientPaint rgp = new RadialGradientPaint(
                new Point((int)(go.x + (go.width / 2)), (int)(go.y + (go.height / 2))),
                (((go.width / 2) + (go.height / 2) / 2)),
                new float[]{0.4f, 1f},
                new Color[]{go.col, new Color(0,0,0,0)}
                );
            g2d.setPaint(rgp);
            g2d.fillOval((int)go.x, (int)go.y, go.width, go.height);
         }
         for (GameObject go: mainGame.blocks) {
            /*Rectangle2D box = new Rectangle2D.Double(go.x, go.y, go.width, go.height);
            float[] fractions = { 0.0f, 0.6f, 1.0f };
            Color[] colors = { new Color(0,0,0,0), go.col, go.col };
            ContourGradientPaint cgp = new ContourGradientPaint(box.getBounds(), fractions, colors);
            g2d.setPaint(cgp);*/
            g2d.setColor(new Color(go.col.getRed(), go.col.getGreen(), go.col.getBlue(), 100));
            g2d.fillRect((int)go.x, (int)go.y, go.width, go.height);
            g2d.setColor(new Color(go.col.getRed(), go.col.getGreen(), go.col.getBlue(), 200));
            g2d.fillRect((int)go.x + 1, (int)go.y + 1, go.width - 2, go.height - 2);
            g2d.setColor(go.col);
            g2d.fillRect((int)go.x + 2, (int)go.y  + 2, go.width - 4, go.height - 4);
         }
         for (GameObject go: mainGame.balls) {
            RadialGradientPaint rgp = new RadialGradientPaint(
                new Point((int)(go.x + (go.width / 2)), (int)(go.y + (go.height / 2))),
                (((go.width / 2) + (go.height / 2) / 2)),
                new float[]{0.4f, 1f},
                new Color[]{go.col, new Color(0,0,0,0)}
                );
            g2d.setPaint(rgp);
            g2d.fillOval((int)go.x, (int)go.y, go.width, go.height);
         }
         
         if (mainGame.pointFontSize < mainGame.maxPointFontSize) {
            g2d.setColor(new Color(255, 255, 255, (int)(255 * (1 - ((double)mainGame.pointFontSize / (double)mainGame.maxPointFontSize)))));
            //System.out.println((1 - ((double)mainGame.pointFontSize / (double)mainGame.maxPointFontSize)));
            
            int x_middle = this.getSize().width / 2;
            int y_middle = this.getSize().height / 2;
            
            Font f_points = new Font("SansSerif", Font.BOLD, mainGame.pointFontSize);
            
            g2d.setFont(f_points);
            FontMetrics m_points = g2d.getFontMetrics(f_points);
            String points = Integer.toString(mainGame.balls_count);
            g2d.drawString(points, (int)(x_middle - (m_points.stringWidth(points) / 2)), (int)(y_middle - (m_points.getHeight() / 2)) + m_points.getAscent());
         }
         
         if (mainGame.gs == Game.GameState.GameOver) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, this.getSize().width, this.getSize().width);
            
            g2d.setColor(Color.white);
            
            int x_middle = this.getSize().width / 2;
            int y_middle = this.getSize().height / 2;
            
            Font f_points = new Font("SansSerif", Font.BOLD, 80);
            
            g2d.setFont(f_points);
            FontMetrics m_points = g2d.getFontMetrics(f_points);
            String points = Integer.toString(mainGame.balls_count);
            g2d.drawString(points, (int)(x_middle - (m_points.stringWidth(points) / 2)), (int)(y_middle - (m_points.getHeight() / 2)) + m_points.getAscent());
         }
      }
   }
