/*
Pac-Man Application: 
CMSC 495 
Instructor: Shanna Kuchenbecker 
John Borra, Sarah Drury, Oanh Woodworth, Yeng Veng, and Thayne Emery
 */

import javax.swing.JFrame;

public class Pacman extends JFrame 
{
    public Pacman() 
    {
        initGUI();
    }

    private void initGUI() 
    {
        add(new Application());
        setTitle("Group 2 Pacman Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(380, 420);
        setLocationRelativeTo(null);
    }
}
