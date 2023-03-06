/*
Pac-Man Application:
CMSC 495 
Instructor: Shanna Kuchenbecker 
John Borra, Sarah Drury, Oanh Woodworth, Yeng Veng, and Thayne Emery
 */

import java.awt.EventQueue;
/**
 * Main class.
 */
public class Main 
{
     public static void main(String[] args) 
     {
        EventQueue.invokeLater(() -> 
        {
        	// Creates a Pac-Man object and sets it to visible.
            var ex = new Pacman();
            ex.setVisible(true);
        });
    }
}
