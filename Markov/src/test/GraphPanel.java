/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author TRETEC
 */
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author John B. Matthews; distribution per GPL.
 */
public class GraphPanel extends JComponent {
 
    private static final int WIDE = 640;
    private static final int HIGH = 480;
    private static final int RADIUS = 35;
    private static final Random rnd = new Random();
    private ControlPanel control = new ControlPanel();
    private int radius = RADIUS;
    private Kind kind = Kind.Circular;
    private List<Node> nodes = new ArrayList<Node>();
    private List<Node> selected = new ArrayList<Node>();
    private List<Edge> edges = new ArrayList<Edge>();
    private double[][] Matrice;
    private Point mousePt = new Point(WIDE / 2, HIGH / 2);
    private Rectangle mouseRect = new Rectangle();
    private boolean selecting = false;
    private boolean CTRL_PRESSED = false;
    private boolean CMTCwasSelected = false;

    public static void main(String[] args) throws Exception {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame f = new JFrame("GraphPanel");
              
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                GraphPanel gp = new GraphPanel();
               
                f.add(gp.control, BorderLayout.NORTH);
                 
                f.add(new JScrollPane(gp), BorderLayout.CENTER);
                f.getRootPane().setDefaultButton(gp.control.defaultButton);
                
                f.pack();
           
 

                f.setLocationByPlatform(true);
                 gp.requestFocus();
                f.setVisible(true);
            }
        });
    }

    public GraphPanel() {
        this.setOpaque(true);
        this.addMouseListener(new MouseHandler());
        this.addMouseMotionListener(new MouseMotionHandler());
        this.addKeyListener(new MyKeyListner());
   
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDE, HIGH);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(0x00f0f0f0));
        g.setFont(new Font("TimesRoman", Font.BOLD, 14));
        g.fillRect(0, 0, getWidth(), getHeight());
        for (Edge e : edges) {
            e.draw(g);
        }
        for (Node n : nodes) {
            n.draw(g);
        }
        if (selecting) {
            g.setColor(Color.darkGray);
            g.drawRect(mouseRect.x, mouseRect.y,
                mouseRect.width, mouseRect.height);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            selecting = false;
            mouseRect.setBounds(0, 0, 0, 0);
            if (e.isPopupTrigger()) {
                showPopup(e);
            }
            e.getComponent().repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            mousePt = e.getPoint();
            if (e.isControlDown()) {
                Node.selectToggle(nodes, mousePt,selected);
            } else if (e.isPopupTrigger()) {
                Node.selectOne(nodes, mousePt,selected);
                showPopup(e);
            } else if (Node.selectOne(nodes, mousePt,selected)) {
                selecting = false;
            } else {
                Node.selectNone(nodes,selected);
                selecting = true;
            }
            e.getComponent().repaint();
        }

        private void showPopup(MouseEvent e) {
            control.popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    
    public  class MyKeyListner extends KeyAdapter{
       

       @Override
 
public void keyPressed(KeyEvent event) {
   if(event.getKeyCode() == KeyEvent.VK_CONTROL)
        CTRL_PRESSED = true;
 
}
 
@Override
 
public void keyReleased(KeyEvent event) {
 if(event.getKeyCode() == KeyEvent.VK_CONTROL)
    CTRL_PRESSED = false;
 
}
 
@Override
 
public void keyTyped(KeyEvent event) {
 
    
}
 

 
  };
    private class MouseMotionHandler extends MouseMotionAdapter {

        Point delta = new Point();

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selecting) {
                mouseRect.setBounds(
                    Math.min(mousePt.x, e.getX()),
                    Math.min(mousePt.y, e.getY()),
                    Math.abs(mousePt.x - e.getX()),
                    Math.abs(mousePt.y - e.getY()));
                Node.selectRect(nodes, mouseRect,selected);
            } else {
                delta.setLocation(
                    e.getX() - mousePt.x,
                    e.getY() - mousePt.y);
                Node.updatePosition(nodes, delta);
                mousePt = e.getPoint();
            }
            e.getComponent().repaint();
        }
    }

    public JToolBar getControlPanel() {
        return control;
    }

    private class ControlPanel extends JToolBar {

        private Action newNode = new NewNodeAction("New");
        private Action boucler = new Boucler("boucler");
        private Action clearAll = new ClearAction("Clear");
        private Action kind = new KindComboAction("Kind");
        private Action color = new ColorAction("Color");
        private Action CMTCaction = new CMTCAction("CMTC");
        private Action CMTDaction = new CMTDAction("CMTD");
        private Action Rename = new Rename("Rename");
        private Action remove_boucle = new Remove_boucle("Remove_boucle");
        private Action connect = new ConnectAction("Connect");
        private Action delete = new DeleteAction("Delete");
        private Action genererMatrice = new GenererMatrice("GenererMatrice");
        private JButton defaultButton = new JButton(newNode);
        private JButton ColorButton = new JButton(color);
        private JButton Matrice = new JButton(genererMatrice);
        private JComboBox kindCombo = new JComboBox();
        ButtonGroup gr=new ButtonGroup();
        JRadioButton CMTD = new JRadioButton("CMTD");
        JRadioButton CMTC = new JRadioButton("CMTC");
        private JPopupMenu popup = new JPopupMenu();

        ControlPanel() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT));
            this.setBackground(Color.lightGray);
            this.add(CMTD);
            this.add(CMTC);
            this.add(defaultButton);
            this.add(new JButton(clearAll));
            this.add(Matrice);
            this.add(kindCombo);
            ColorButton.setBackground(Color.BLUE);
            ColorButton.setForeground(Color.BLUE);
            this.add(ColorButton);
            
            JSpinner js = new JSpinner();
            js.setModel(new SpinnerNumberModel(RADIUS, 5, 100, 5));
            js.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    JSpinner s = (JSpinner) e.getSource();
                    radius = (Integer) s.getValue();
                    Node.updateRadius(nodes, radius);
                    GraphPanel.this.repaint();
                }
            });
            this.add(new JLabel("Size:"));
            this.add(js);
            CMTD.setSelected(true);
            CMTD.setAction(CMTDaction);
            CMTC.setAction(CMTCaction);
            gr.add(CMTD);
            gr.add(CMTC);
            //this.add(new JButton(random));

            popup.add(new JMenuItem(newNode));
            popup.add(new JMenuItem(Rename));
            popup.add(new JMenuItem(color));
            popup.add(new JMenuItem(remove_boucle));
            popup.add(new JMenuItem(connect));
            popup.add(new JMenuItem(delete));
            popup.add(new JMenuItem(genererMatrice));
            JMenu subMenu = new JMenu("Kind");
            for (Kind k : Kind.values()) {
                kindCombo.addItem(k);
                subMenu.add(new JMenuItem(new KindItemAction(k)));
            }
            popup.add(subMenu);
            popup.add(new JMenuItem(boucler));
            kindCombo.addActionListener(kind);
        }

        class KindItemAction extends AbstractAction {

            private Kind k;

            public KindItemAction(Kind k) {
                super(k.toString());
                this.k = k;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                kindCombo.setSelectedItem(k);
            }
        }
    }

    private class ClearAction extends AbstractAction {

        public ClearAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            nodes.clear();
            edges.clear();
            repaint();
        }
    }

    private class Remove_boucle extends AbstractAction {

        public Remove_boucle(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            for(Node n:selected)
                n.set_loop(false);
            repaint();
        }
    }
    
    private class GenererMatrice extends AbstractAction {

         
         
        private double getSome(double [] array){
          double somme =0;
          for(int i=0;i<array.length;i++)
              somme+=array[i];
         return somme;
        }
        
        public GenererMatrice(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            
   
            
                Matrice = new double[nodes.size()][nodes.size()];    
                for(int i=0;i< nodes.size();i++)
                    for(int j=0;j<nodes.size();j++)
                        Matrice[i][j]=0;
                
                for(Edge edge:edges){
                    int indice_premier_node = nodes.indexOf(edge.getN1()),
                            indice_deusième_node = nodes.indexOf(edge.getN2());
                    Matrice[indice_premier_node][indice_deusième_node]=Double.parseDouble(edge.getPoid());
                }
                String titel = "MATRICE_CMTC";
                if(control.CMTC.isSelected())
                    for(int i=0;i< nodes.size();i++)
                        Matrice[i][i] = -getSome(Matrice[i]);
                else{
                    titel = "MATRICE_CMTD";
                    for(int i=0;i<nodes.size();i++)
                        if(nodes.get(i).Has_loop())
                            Matrice[i][i]=Double.parseDouble(nodes.get(i).getPoid_loop());}
                
            try {  
                JFrame frame = new AffichageMatrice(titel,Matrice,nodes);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(GraphPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
     
    private class ColorAction extends AbstractAction {

        public ColorAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Color color = control.ColorButton.getBackground();
            color = JColorChooser.showDialog(
                GraphPanel.this, "Choose a color", color);
            if (color != null) {
                Node.updateColor(nodes, color);
              //  control.hueIcon.setColor(color);
                control.ColorButton.setBackground(color);
                control.ColorButton.setForeground(color);

                control.repaint();
                repaint();
            }
        }
    }
    
    private class Rename extends AbstractAction {

        public Rename(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            if (selected.size() == 1) {
                Node n1 = selected.get(0);
                String s1 = JOptionPane.showInputDialog("give the new name");
                if( s1!= null && s1.length()>0){
                    n1.setLabel(s1);
                }
            }
            repaint();
        }
    }
    
    private class ConnectAction extends AbstractAction {

        public ConnectAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            //Node.getSelected(nodes, selected);
            if (selected.size() > 1) {
                for (int i = 0; i < selected.size()-1 ; ++i) {
                    Node n1 = selected.get(i);
                    Node n2 = selected.get(i + 1);
                    String s1 = JOptionPane.showInputDialog("give the weight between "+n1.label+" -> "+n2.label);
                    try {
                        Float.parseFloat(s1);
                    } catch (NumberFormatException exception) {
                        s1 = "0.0";
                    }
    
                    boolean find_it = false;
                    for(Edge ed :edges)
                        if(ed.n1.label == n1.label && ed.n2.label == n2.label){
                            ed.poid=s1;
                            find_it = true;
                        } 
                    if(!find_it){
                        Edge edge = new Edge(n1, n2);
                        edge.setPoid(s1);
                        edges.add(edge);}
                }
            }
            repaint();
        }
    }

    private class DeleteAction extends AbstractAction {

        public DeleteAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            ListIterator<Node> iter = nodes.listIterator();
            while (iter.hasNext()) {
                Node n = iter.next();
                if (n.isSelected()) {
                    deleteEdges(n);
                    iter.remove();
                }
            }
            repaint();
        }

        private void deleteEdges(Node n) {
            ListIterator<Edge> iter = edges.listIterator();
            while (iter.hasNext()) {
                Edge e = iter.next();
                if (e.n1 == n || e.n2 == n) {
                    iter.remove();
                }
            }
        }
    }

    private class KindComboAction extends AbstractAction {

        public KindComboAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox combo = (JComboBox) e.getSource();
            kind = (Kind) combo.getSelectedItem();
            Node.updateKind(nodes, kind);
            repaint();
        }
    }

    private class NewNodeAction extends AbstractAction {

        public NewNodeAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
            Node.selectNone(nodes,selected);
            Point p = mousePt.getLocation();
            Color color = control.ColorButton.getBackground();
            Node n = new Node(p, radius, color, kind,"Node"+nodes.size());
            n.setSelected(true);
            selected.add(n);
            nodes.add(n);
            repaint();
        }
    }
   
    private class Boucler extends AbstractAction {

        public Boucler(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent e) {
           if (selected.size() == 1 && control.CMTD.isSelected()) {
               selected.get(0).set_loop(true);
               String s1 = JOptionPane.showInputDialog("give the weight of "+selected.get(0).label);
               try {
                   Float.parseFloat(s1);
               } catch (NumberFormatException exception) {
                   s1 = "0.0";}
                selected.get(0).setPoid_loop(s1);
            }
            repaint();
        }
    }

    private class CMTCAction extends AbstractAction {

        public CMTCAction(String string) {
            super(string);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
           if(!CMTCwasSelected && !nodes.isEmpty()){
           
            int dialogResult = JOptionPane.showConfirmDialog (null, "Voulez-vous vraiment de effacer tout!","Warning",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                 CMTCwasSelected = true;
                control.popup.remove(control.popup.getComponentCount()-1);
                nodes.clear();
                edges.clear();
                repaint();}
            else
                  control.CMTD.setSelected(true); 
           }
        }
    }
    
    private class CMTDAction extends AbstractAction {

        public CMTDAction(String string) {
            super(string);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            
            if(CMTCwasSelected && !nodes.isEmpty()){

            int dialogResult = JOptionPane.showConfirmDialog (null, "Voulez-vous vraiment de effacer tout!","Warning",JOptionPane.YES_NO_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION){
                CMTCwasSelected = false;
                control.popup.add(control.boucler);
                nodes.clear();
                edges.clear();
                repaint();}
             else
                control.CMTD.setSelected(true); 
           }
          
        }
    }

    /**
     * The kinds of node in a graph.
     */
    private enum Kind {

        Circular, Rounded, Square;
    }

    /**
     * An Edge is a pair of Nodes.
     */
    
    private static class Boucle{
        private Node n1;
        private String poid;
   
        public Boucle( Node n1) {
            this.n1 = n1;
            this.poid = "0.654";
        }
        
        
        public void setPoid(String poid) {
            this.poid = poid;
        }
        
        public void draw(Graphics g) {
            Point p1 = n1.getLocation();
            
            
            g.setColor(Color.darkGray);
            Graphics2D g2 = (Graphics2D) g;
            g2.setFont(new Font("TimesRoman", Font.BOLD, 14)); 
            Stroke temps = g2.getStroke();
            g2.setStroke(new BasicStroke(5));
            
            g2.drawArc(p1.x, p1.y-n1.r, 2*n1.r,n1.r,-90,270);

            
            Point firt = new Point(p1.x+n1.r,p1.y-n1.r);

            g2.setColor(n1.color);
            FontMetrics fm = g.getFontMetrics();
            double textWidth = fm.getStringBounds(this.poid, g2).getWidth();
            g2.fillRoundRect(firt.x, firt.y-14, (int)textWidth, 16,4,4);
            g2.setColor(Color.WHITE);
            g2.drawString(this.poid, firt.x, firt.y);
   
            g2.setStroke(temps);
        }}
    
    private  class Edge {

        private Node n1;
        private Node n2;
        private String poid;

        public String getPoid() {
            return poid;
        }

        public Edge(Node n1, Node n2) {
            this.n1 = n1;
            this.n2 = n2;
            this.poid = "0.654";
        }

        public Node getN1() {
            return n1;
        }

        public Node getN2() {
            return n2;
        }
        
        

        public void setPoid(String poid) {
            this.poid = poid;
        }

        public void draw(Graphics g) {
            Point p1 = n1.getLocation();
            Point p2 = n2.getLocation();
            g.setColor(Color.darkGray);
            Graphics2D g2 = (Graphics2D) g;
            
            Stroke temps = g2.getStroke();
            g2.setStroke(new BasicStroke(5));
            boolean is_here = false;
            for(int i = edges.indexOf(this);i>=0;i--)
                if(edges.get(i).n1.label == n2.label && edges.get(i).n2.label == n1.label)
                    is_here = true;
            
            if(!is_here)
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);

            int X_midlle = p2.x,
                Y_middle = p2.y,
                distance = 10;
            
            double alpha_ortho = (double)(p1.x-p2.x)/(double)(p2.y-p1.y),
                   alpha = - 1 /alpha_ortho,
                   beta = Y_middle - alpha*X_midlle;
            
            double delta1 = Math.sqrt((double)(Math.pow(n2.r, 2)/(1+Math.pow(alpha, 2))));
            int direction = 1;
            if (p1.x > p2.x)
                direction *=-1;
            
            delta1*=direction;
            Point firt = new Point((int)(X_midlle - 1.5*delta1), (int)((X_midlle - 1.5*delta1)*alpha + beta));
            if (alpha_ortho == 0)
                firt.y = distance;
            if (firt.x == 0)
                firt.x = X_midlle;
            
            double delta = Math.sqrt((double)(Math.pow(distance, 2)/(1+Math.pow(alpha_ortho, 2)))),
                   beta_ortho = (double)firt.y - alpha_ortho*firt.x ;
            
            int[] ints = new int[]{X_midlle  ,firt.x + (int)delta    ,    firt.x - (int)delta   },
                  ints1 = new int[]{Y_middle ,(int)((firt.x + delta)*alpha_ortho+beta_ortho)   , (int)((firt.x - delta)*alpha_ortho+beta_ortho)}  ;
            g2.fillPolygon(ints, ints1, 3);
            
            FontMetrics fm = g.getFontMetrics();
            double textWidth = fm.getStringBounds(this.poid, g2).getWidth();
            if (p1.x > p2.x){
                g2.setColor(n2.color);
                g2.fillRoundRect(firt.x, firt.y-14, (int)textWidth, 16,4,4);
                g2.setColor(Color.WHITE);
                g2.drawString(this.poid, firt.x, firt.y);}
            else{
                g2.setColor(n2.color);
                g2.fillRoundRect(firt.x - direction * 8 *this.poid.length(), firt.y-14,(int)textWidth, 16,4,4);
                g2.setColor(Color.WHITE);
                g2.drawString(this.poid, firt.x - direction * 8 *this.poid.length(), firt.y);}
            
            g2.setStroke(temps);
        }
    }

    /**
     * A Node represents a node in a graph.
     */
    public static class Node {

        private Point p;

        public String getLabel() {
            return label;
        }
        private int r;
        private Color color;
        private Kind kind;
        private String label;
        private boolean is_selected = false;
        private boolean has_loop = false;
        private String poid_loop= "0.5";
        private Rectangle b = new Rectangle();

        public String getPoid_loop() {
            return poid_loop;
        }
        
        
        public void set_loop(boolean has_loop) {
            this.has_loop = has_loop;
        }

        public boolean Has_loop() {
            return has_loop;
        }

        public void setPoid_loop(String poid_loop) {
            this.poid_loop = poid_loop;
        }
        
        
        

        /**
         * Construct a new node.
         */
        public Node(Point p, int r, Color color, Kind kind, String label) {
            this.p = p;
            this.r = r;
            this.color = color;
            this.kind = kind;
            this.label = label;
            setBoundary(b);
        }
        
        
        

        public void setLabel(String label) {
            this.label = label;
        }
        
        /**
         * Calculate this node's rectangular boundary.
         */
        private void setBoundary(Rectangle b) {
            b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
        }

        public Point getBoundary(){
            return b.getLocation();
        }

        /**
         * Draw this node.
         */
        public void draw(Graphics g) {

            
            
            if(Has_loop()){
                Boucle boucle = new Boucle(this);
                boucle.setPoid(poid_loop);
                boucle.draw(g);
            }
            
            
            g.setColor(this.color);
            if (this.kind == Kind.Circular) {
                g.fillOval(b.x, b.y, b.width, b.height);
            } else if (this.kind == Kind.Rounded) {
                g.fillRoundRect(b.x, b.y, b.width, b.height, r, r);
            } else if (this.kind == Kind.Square) {
                g.fillRect(b.x, b.y, b.width, b.height);
            }
            if (is_selected) {
                g.setColor(Color.darkGray);
                g.drawRect(b.x, b.y, b.width, b.height);
            }
         
            // draw text
            FontMetrics fm = g.getFontMetrics();
            double textWidth = fm.getStringBounds(this.label, g).getWidth();
            g.setColor(Color.WHITE);
            g.drawString(this.label, (int) (p.x - textWidth/2),
                                   (int) (p.y + fm.getMaxAscent() / 2));
            g.setColor(this.color);
        }

        /**
         * Return this node's location.
         */
        public Point getLocation() {
            return p;
        }

        /**
         * Return true if this node contains p.
         */
        public boolean contains(Point p) {
            return b.contains(p);
        }

        /**
         * Return true if this node is selected.
         */
        public boolean isSelected() {
            return is_selected;
        }

        /**
         * Mark this node as selected.
         */
        public void setSelected(boolean selected) {
            this.is_selected = selected;
        }

        /**
         * Collected all the selected nodes in list.
         */
        public static void getSelected(List<Node> list, List<Node> selected) {
            selected.clear();
            for (Node n : list) {
                if (n.isSelected()) {
                    selected.add(n);
                }
            }
        }

        /**
         * Select no nodes.
         */
        public static void selectNone(List<Node> list,List<Node> selected) {
            for (Node n : list) {
                n.setSelected(false);
            }
            selected.clear();
        }

        /**
         * Select a single node; return true if not already selected.
         */
        public static boolean selectOne(List<Node> list, Point p,List<Node> selected) {
            for (Node n : list) {
                if (n.contains(p)) {
                    if (!n.isSelected()) {
                        Node.selectNone(list,selected);
                        n.setSelected(true);
                        selected.add(n);
                    }
                    return true;
                }
            }
            return false;
        }

        /**
         * Select each node in r.
         */
        public static void selectRect(List<Node> list, Rectangle r,List<Node> selected) {
            for (Node n : list) {
                n.setSelected(r.contains(n.p));
                if(r.contains(n.p)){
                    if(!selected.contains(n)){
                      selected.add(n);}}
                else
                    selected.remove(n);
            }
        }

        /**
         * Toggle selected state of each node containing p.
         */
        public static void selectToggle(List<Node> list, Point p,List<Node> selected) {
            for (Node n : list) {
                if (n.contains(p)) {
                    if(n.isSelected())
                        selected.remove(n);
                    else
                        selected.add(n);
                    n.setSelected(!n.isSelected());
                }
            }
        }

        /**
         * Update each node's position by d (delta).
         */
        public static void updatePosition(List<Node> list, Point d) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.p.x += d.x;
                    n.p.y += d.y;
                    n.setBoundary(n.b);
                }
            }
        }

        /**
         * Update each node's radius r.
         */
        public static void updateRadius(List<Node> list, int r) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.r = r;
                    n.setBoundary(n.b);
                }
            }
        }

        /**
         * Update each node's color.
         */
        public static void updateColor(List<Node> list, Color color) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.color = color;
                }
            }
        }

        /**
         * Update each node's kind.
         */
        public static void updateKind(List<Node> list, Kind kind) {
            for (Node n : list) {
                if (n.isSelected()) {
                    n.kind = kind;
                }
            }
        }
    }

    private static class ColorIcon implements Icon {

        private static final int WIDE = 20;
        private static final int HIGH = 20;
        private Color color;

        public ColorIcon(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, WIDE, HIGH);
        }

        public int getIconWidth() {
            return WIDE;
        }

        public int getIconHeight() {
            return HIGH;
        }
    }
}