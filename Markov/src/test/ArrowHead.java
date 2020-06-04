/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
  
public class ArrowHead
{
    public ArrowHead()
    {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new ArrowPanel());
        f.setSize(400,400);
        f.setLocation(200,200);
        f.setVisible(true);
    }
  
    public static void Draw(Graphics g,Point head,int weidth,int height,int direction,double alpha,double beta){
        double alpha_ortho = -1/alpha;
        double delta1 = Math.sqrt((double)(Math.pow(weidth, 2)/(1+Math.pow(alpha, 2))));
        delta1 *= direction;
        Point back_of_arrow = new Point((int)(head.x - delta1), (int)((head.x - delta1)*alpha + beta));

        
         double delta = Math.sqrt((double)(Math.pow(height, 2)/(1+Math.pow(alpha_ortho, 2)))),
                   beta_ortho = (double)back_of_arrow.y - alpha_ortho*back_of_arrow.x ;
        
        int[] ints =  new int[]{head.x  ,back_of_arrow.x + (int)delta                              ,    back_of_arrow.x - (int)delta   },
              ints1 = new int[]{head.y  ,(int)((back_of_arrow.x + delta)*alpha_ortho+beta_ortho)   , (int)((back_of_arrow.x - delta)*alpha_ortho+beta_ortho)}  ;
        g.fillPolygon(ints, ints1, 3);

        
    
    
    }
}