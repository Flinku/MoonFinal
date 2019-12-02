import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;



public class MainWindow implements KeyListener{
    //Window stuff
    JFrame mainFrame;
    PaintPanel panel;

    //Arrays of objects to be drawn
    ArrayList<SpriteObj> lowerDrawList = new ArrayList<SpriteObj>();
    ArrayList<SpriteObj> upperDrawList = new ArrayList<SpriteObj>();
    String[] text = {"vx: ", "vy: ","vz: " , "Total speed: ", "dv", "altitude: ", "Speed factor: ", ""};

    //Variables for seeing what keys are being pressed
    boolean aPressed = false;
    boolean dPressed = false;
    boolean spacePressed = false;
    boolean escPressed = false;
    boolean downPressed = false;
    boolean downUsed = false;
    boolean upPressed = false;
    boolean upUsed = false;
    boolean onePressed = false;
    boolean oneUsed = false;
    boolean wPressed = false;
    boolean sPressed = false;

    boolean[] keyArray = new boolean[]{aPressed, dPressed, spacePressed, escPressed};

    int boxVal = 0;

    MainWindow(){
        //Sets up the window
        mainFrame = new JFrame();
        panel = new PaintPanel();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 800);
        mainFrame.add(panel);
        mainFrame.setVisible(true);
        //Lets it listen for key presses
        mainFrame.addKeyListener(this);

    }
    //Adds an image to the upper list, for things that need to go above the forground
    void addUpper(SpriteObj i){
        upperDrawList.add(i);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    void addLower(SpriteObj i){
        lowerDrawList.add(i);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    void sortDrawList(ArrayList<SpriteObj> arr){
        if (arr.get(0).level > arr.get(1).level) {
            SpriteObj temp = arr.get(0);
            arr.set(0, arr.get(1));
            arr.set(1, temp);
        }
    }

    //Clears out everything in the two draw list arrays
    void clearDraws(){
        lowerDrawList.clear();
        upperDrawList.clear();
    }
    //Quick way to call repaint() and revalidate()
    void refresh(){
        mainFrame.repaint();
        mainFrame.revalidate();
    }
    //I don't use this but it's necessary for the KeyEvent to work
    @Override
    public void keyTyped(KeyEvent e) {
    }
    //Tells when the key is depressed
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == 65){
            aPressed = true;
        }
        if (e.getKeyCode() == 68){
            dPressed = true;
        }
        if (e.getKeyCode() == 87){
            wPressed = true;
        }
        if (e.getKeyCode() == 83){
            sPressed = true;
        }
        if (e.getKeyCode() == 32){
            spacePressed = true;
        }
        if (e.getKeyCode() == 40) {
            downPressed = true;
        }
        if (e.getKeyCode() == 38) {
            upPressed = true;
        }
        if (e.getKeyCode() == 49) {
            onePressed = true;
        }
    }
    //Tells when the key is released
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 65){
            aPressed = false;
        }
        if (e.getKeyCode() == 68){
            dPressed = false;
        }
        if (e.getKeyCode() == 87){
            wPressed = false;
        }
        if (e.getKeyCode() == 83){
            sPressed = false;
        }
        if (e.getKeyCode() == 32){
            spacePressed = false;
        }
        if (e.getKeyCode() == 40) {
            downPressed = false;
            downUsed = false;
        }
        if (e.getKeyCode() == 38) {
            upPressed = false;
            upUsed = false;
        }
        if (e.getKeyCode() == 49){
            onePressed = false;
            oneUsed = false;
        }

    }

    //Draws on the frame using the two sprite list arrays
    public class PaintPanel extends JPanel{
        @Override
        public void paintComponent(Graphics g) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            for (SpriteObj i: lowerDrawList) {
                g.drawImage(i.sprite, i.x, i.y, panel);
            }

            g.setColor(Color.WHITE);

            g.drawRect(875, 50, 25, 25);
            g.drawRect(900, 50, 25, 25);
            g.drawRect(925, 50, 25, 25);
            g.drawRect(950, 50, 25, 25);
            g.drawRect(900, 25, 25, 25);
            g.drawRect(900, 75, 25, 25);

            if (boxVal == Program.FRONT){
                g.fillRect(900, 50, 25, 25);
            }
            else if (boxVal == Program.RIGHT){
                g.fillRect(925, 50, 25, 25);
            }
            else if (boxVal == Program.LEFT){
                g.fillRect(875, 50, 25, 25);
            }
            else if (boxVal == Program.BACK){
                g.fillRect(950, 50, 25, 25);
            }
            else if (boxVal == Program.TOP){
                g.fillRect(900, 25, 25, 25);
            }
            else if (boxVal == Program.BOTTOM){
                g.fillRect(900, 75, 25, 25);
            }


            Font font = new Font("Monospaced", Font.PLAIN, 12);
            g.setFont(font);
            int loc = 10;
            for (String i: text){
                g.drawString(i, 10, loc);
                loc += 15;
            }

            for (SpriteObj i: upperDrawList) {
                g.drawImage(i.sprite, i.x, i.y, panel);
            }

        }
    }

}
