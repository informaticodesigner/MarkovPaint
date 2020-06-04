/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;


import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
class ArrowPanel extends JPanel
{
    double phi;
    int barb;
  
    public ArrowPanel()
    {
        phi = Math.toRadians(40);
        barb = 20;
    }
  
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        Point sw = new Point(w/8, h*7/8);
        Point ne = new Point(w*7/8, h/8);
        g2.draw(new Line2D.Double(sw, ne));
        drawArrowHead(g2, sw, ne, Color.red);
        drawArrowHead(g2, ne, sw, Color.blue);
    }
  
    private void drawArrowHead(Graphics2D g2, Point tip, Point tail, Color color)
    {
        g2.setPaint(color);
        double dy = tip.y - tail.y;
        double dx = tip.x - tail.x;
        double theta = Math.atan2(dy, dx);
        //System.out.println("theta = " + Math.toDegrees(theta));
        double x, y, rho = theta + phi;
         x = tip.x - barb * Math.cos(rho);
            y = tip.y - barb * Math.sin(rho);
            g2.draw(new Line2D.Double(tip.x, tip.y, x, y));
    }
}