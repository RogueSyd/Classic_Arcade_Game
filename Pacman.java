/*
Pac-Man Application: 
CMSC 495 
Instructor: Shanna Kuchenbecker 
John Borra, Sarah Drury, Oanh Woodworth, Yeng Veng, and Thayne Emery
 */

import javax.swing.JFrame;

public class Pacman extends JFrame 
{
	/**
	 * Default constructor for Pacman object.
	 * Called from start when the application is launched.
	 */
    public Pacman() 
    {
        initGUI();
    }
    
    /**
     * Loaded upon start of application.
     * creates Application object in a JFrame window.
     */
    private void initGUI() 
    {
    	//Sets application to run in JFrame Window.
        add(new Application());
        setTitle("Group 2: Pac-Man Application");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(380, 420);
        setLocationRelativeTo(null);
    }
}
