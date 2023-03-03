/*
Pac-Man Application: Phase 3 Source
CMSC 495 
Instructor: Shanna Kuchenbecker 
John Borra, Sarah Drury, Oanh Woodworth, Yeng Veng, and Thayne Emery
--------------------------------------------------------------------------------
Reference:

Gaspar Coding. (2020, September 4). Pacman in Java, Programming Tutorial 1/2 [Video]. 
YouTube. https://www.youtube.com/watch?v=ATz7bIqOjiA

*/
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;

public class Application extends JPanel implements ActionListener 
{
    private Dimension dimension;
    private final Font smallFont = new Font("Helvetica", Font.BOLD,14);
    private Color mazeColor;
    private Image image;
    private final Color dotColor = new Color(192, 192, 0);

    private boolean inGame = false;
    private boolean dying = false;
    
    //identify constant variables
    // how big a block is in the game
    private final int BLOCK_SIZE = 24;
    //how many blocks horizontally and vertically. so 15 block by 15
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    
    private int PACMAN_SPEED = 4; 
    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_GHOSTS = 6;
    private int pacsLeft, score;
    
    //position of ghosts
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private final Image[] ghost = new Image[N_GHOSTS];
    private Image heart, cherry;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;
    
    //Pac-Man's position
    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    // Define an array to store the heart locations
    private static final int[] CHERRY_LOCATIONS = {32, 42, 182, 192};
    private boolean[] cherryCollected = new boolean[CHERRY_LOCATIONS.length];
    
    //Map data for the game.
    private final short levelData[] = 
    {
        19, 26, 26, 26, 18, 26, 26, 26, 26, 26, 18, 26, 26, 26, 22,
        21, 0, 0, 0, 21, 0, 0, 0, 0, 0, 21, 0, 0, 0, 21,
        21, 0, 19, 26, 20, 0, 19, 26, 22, 0, 17, 26, 22, 0, 21,
        17, 26, 28, 0, 25, 18, 28, 0, 25, 18, 28, 0, 25, 26, 20,
        21, 0, 0, 0, 0, 21, 0, 0, 0, 21, 0, 0, 0, 0, 21,
        21, 0, 19, 26, 26, 16, 26, 26, 26, 16, 26, 26, 22, 0, 21,
        25, 26, 20, 0, 0, 21, 3, 0, 6, 21, 0, 0, 17, 26, 28,
        10, 10, 16, 26, 26, 20, 9, 8, 12, 17, 26, 26, 16, 10, 10,
        19, 26, 28, 0, 0, 17, 26, 26, 26, 20, 0, 0, 25, 26, 22,
        21, 0, 0, 0, 0, 21, 0, 0, 0, 21, 0, 0, 0, 0, 21,
        17, 26, 18, 26, 26, 24, 22, 0, 19, 24, 26, 26, 18, 26, 20,
        21, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 21,
        21, 0, 25, 22, 0, 0, 17, 26, 20, 0, 0, 19, 28, 0, 21,
        21, 0, 0, 17, 26, 26, 28, 0, 25, 26, 26, 20, 0, 0, 21,
        25, 26, 26, 28, 8, 8, 8, 8, 8, 8, 8, 25, 26, 26, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;
    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    private JButton startButton,exitButton;
    private int numCherriesCollected;
    
    // Variables for audio clips
    private Clip clipMain;
    private Clip clipAudio;
    
    /**
     * Constructor
     */
    public Application()                                                //Phase1
    {
    	createClip();
        loadImages();
        initVariables();    
        initBoard();
        addKeyListener(new TAdapter());                    //add keys controller
        
        //create Start Buttons
        setLayout(null);
        startButton = new JButton("Start");
        startButton.setBounds(100, 150, 100, 30);
        startButton.addActionListener(this);
        add(startButton);
        
        //create Exit Buttons
        exitButton = new JButton("Exit");
        exitButton.setBounds(100, 200, 100, 30);
        exitButton.addActionListener(this);
        add(exitButton);
    }
    
    /**
     * Setting the focus for the window.
     */
    private void initBoard()                                            //Phase1
    {      
        setFocusable(true);
        setBackground(Color.black);//set background color to black
    }
    
    /**
     * Initialize all variables.
     */
    private void initVariables()                                        //Phase1
    {
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        dimension = new Dimension(400, 400);

        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];  
        //set up timer every 40ms
        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() 
    {
        super.addNotify();
        initGame();
    }
    
    /**
     * Pac-Man animation.
     */
    private void doAnim()                                               //Phase1
    {             
        pacAnimCount--;
        
        if (pacAnimCount <= 0) 
        {
            pacAnimCount = PAC_ANIM_DELAY;
            pacmanAnimPos = pacmanAnimPos + pacAnimDir;

            if (pacmanAnimPos == (PACMAN_ANIM_COUNT - 1) || pacmanAnimPos == 0) 
            {
                pacAnimDir = -pacAnimDir;
            }
        }
    }
    
    /**
     * Runs the game.
     * @param g2d Pac-Man graphics
     */
    private void playGame(Graphics2D g2d)                               //Phase1
    {   
        if (dying) 
        {
            death();
        } 
        else 
        {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
            
            // Starts game audio
            playSound(); 
        }
    }
    
    /**
     * Shows the intro screen.
     * @param g Pac-Man graphics
     */
    private void introScreen(Graphics2D g)                              //Phase1
    {    
    	//fixed buttons so they will work once game over happens.
    	startButton.setVisible(true);
        exitButton.setVisible(true);
        // Draw Pac-Man title
        g.setColor(Color.YELLOW);
        Font titleFont = new Font("Helvetica", Font.BOLD, 48);    //set the font
        g.setFont(titleFont);
        FontMetrics titleMetrics = g.getFontMetrics(titleFont);
        String title = "Pac-Man"; 
        g.drawString(title, (getWidth() - titleMetrics.stringWidth(title)) / 2, 
            getHeight() / 3);                                   //draw the title
                
        // Draw Start and Exit buttons
        Font buttonFont = new Font("Helvetica", Font.PLAIN, 24);
        g.setFont(buttonFont);
        FontMetrics buttonMetrics = g.getFontMetrics(buttonFont);

        startButton.setBounds((getWidth() - 200) / 2, getHeight() / 2, 100, 30);
        exitButton.setBounds((getWidth() + 10) / 2, getHeight() / 2, 100, 30);

        g.setColor(Color.YELLOW);
        g.fillRect(startButton.getBounds().x, startButton.getBounds().y, 100, 30);
        g.fillRect(exitButton.getBounds().x, exitButton.getBounds().y, 100, 30);

        g.setColor(Color.BLACK);
        g.drawString("Start", startButton.getBounds().x + (startButton.getWidth() - buttonMetrics.stringWidth("Start")) 
            / 2, startButton.getBounds().y + (startButton.getHeight() + buttonMetrics.getAscent()) / 2);
        g.drawString("Exit", exitButton.getBounds().x + (exitButton.getWidth() - buttonMetrics.stringWidth("Exit")) 
            / 2, exitButton.getBounds().y + (exitButton.getHeight() + buttonMetrics.getAscent()) / 2);
        
        // Starts audio for main screen
        playSound();
    }
    
    /**
     * Draws the score.
     * @param g Pac-Man graphics
     */
    private void drawScore(Graphics2D g)                                //Phase1 
    {      
        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(26, 12, 255));
        s = "SCORE:  " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 86, SCREEN_SIZE + 16);

        for (i = 0; i < pacsLeft; i++) 
        {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
        // check if the character is on a cherry
        int cherryIndex = isOnCherry();
        if (cherryIndex != -1 && !cherryCollected[cherryIndex]) 
        {
            // add 100 points
            score += 100;
            cherryCollected[cherryIndex] = true;  // set the cherry as collected
            //removes the cherry from screen
            screenData[CHERRY_LOCATIONS[cherryIndex]] &= ~16;
        }
    }
    
    /**
     * Checks to see if the maze has been completed.
     * Reference: Gaspar Coding. (2020, September 4).
     */
    private void checkMaze()                                            //Phase2
    {
        short i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) 
        {
            if ((screenData[i] & 48) != 0) 
            {
                finished = false;
            }
            i++;
        }

        if (finished) 
        {
            score += 50;
            if (N_GHOSTS < MAX_GHOSTS) 
            {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) 
            {
                currentSpeed++;
            }
            initLevel();
        }
    }
    
    /**
     * checks to see if Pac-Man has lives left, if not then the game as over.
     */
    private void death()                                                //Phase2
    {
        pacsLeft--;
        //if Pac-Man has no more lives the game is over.
        if (pacsLeft == 0) 
        {
            inGame = false;
        }
        continueLevel();
    }
    
    /**
     * Moves the ghosts. if a ghost touchs Pac-Man then set Pac-Man to dying.
     * @param g2d Pac-Man graphics
     * Reference: Gaspar Coding. (2020, September 4).
     */
    private void moveGhosts(Graphics2D g2d)                             //Phase2
    {
        short n;
        int pos;
        int count;

        for (n = 0; n < N_GHOSTS; n++) 
        {
            if (ghost_x[n] % BLOCK_SIZE == 0 && ghost_y[n] % BLOCK_SIZE == 0) 
            {
                pos = ghost_x[n] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[n] / BLOCK_SIZE);
                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[n] != 1) 
                {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[n] != 1) 
                {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[n] != -1) 
                {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[n] != -1) 
                {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) 
                {
                    if ((screenData[pos] & 15) == 15) 
                    {
                        ghost_dx[n] = 0;
                        ghost_dy[n] = 0;
                    } 
                    else 
                    {
                        ghost_dx[n] = -ghost_dx[n];
                        ghost_dy[n] = -ghost_dy[n];
                    }
                } 
                else 
                {
                    count = (int) (Math.random() * count);

                    if (count > 3) 
                    {
                        count = 3;
                    }
                    
                    ghost_dx[n] = dx[count];
                    ghost_dy[n] = dy[count];
                }
            }
            
            // Ghost left-to-right teleport at tunnel
            if (ghost_x[n] / BLOCK_SIZE <= 0 && ghost_y[n] / BLOCK_SIZE == 7 && ghost_dx[n] == -1)
            {
                ghost_x[n] = BLOCK_SIZE * 14 + ghost_x[n] + (ghost_dx[n] * ghostSpeed[n]);
            }
            // Ghost right-to-left teleport at tunnel
            else if (ghost_x[n] / BLOCK_SIZE >= 14 && ghost_y[n] / BLOCK_SIZE == 7 && ghost_dx[n] == 1)
            {
                ghost_x[n] = -BLOCK_SIZE * 14 + ghost_x[n] + (ghost_dx[n] * ghostSpeed[n]);
            }

            ghost_x[n] = ghost_x[n] + (ghost_dx[n] * ghostSpeed[n]);
            ghost_y[n] = ghost_y[n] + (ghost_dy[n] * ghostSpeed[n]);
            drawGhost(g2d, n, ghost_x[n] + 1, ghost_y[n] + 1);

            if (pacman_x > (ghost_x[n] - 12) && pacman_x < (ghost_x[n] + 12)
                    && pacman_y > (ghost_y[n] - 12) && pacman_y < (ghost_y[n] + 12)
                    && inGame) 
            {
                dying = true;
            }
        }
    }
    
    // method to check if the character is on a cherry location
    private int isOnCherry() 
    {
        int pacRow = pacman_y / BLOCK_SIZE;
        int pacCol = pacman_x / BLOCK_SIZE;
        for (int i = 0; i < CHERRY_LOCATIONS.length; i++) 
        {
            int location = CHERRY_LOCATIONS[i];
            if (location != -1 && !cherryCollected[i]) 
            {
                int row = location / N_BLOCKS;
                int col = location % N_BLOCKS;
                if (pacRow == row && pacCol == col) 
                {
                    return i;
                }
            }
        }
        return -1;
    }
    
    //method draws cherry images
    private void drawCherries(Graphics2D g2d)                           //phase3
    {                         
        Image cherryImage = cherry;
        // loop to draw 4 cherries
        for (int i = 0; i < CHERRY_LOCATIONS.length; i++) 
        {
            int location = CHERRY_LOCATIONS[i];
            if (location != -1 && !cherryCollected[i]) 
            {
                int row = location / N_BLOCKS;
                int col = location % N_BLOCKS;
                int x = col * BLOCK_SIZE + BLOCK_SIZE / 2;
                int y = row * BLOCK_SIZE + BLOCK_SIZE / 2;
                
                //if the character collides with the heart
                if (pacman_x == x && pacman_y == y) 
                {
                    if ((screenData[location] & 32) == 0) 
                    {
                        cherryCollected[i] = true; // Set the cherry as collected
                        //executes it if the cherry hasn't been collected yet
                        screenData[location] &= ~16;
                        numCherriesCollected++;
                    }
                } 
                else 
                {
                    // Draw the cherry image only if it hasn't been collected yet
                    if ((screenData[location] & 16) != 0) 
                    {
                        g2d.drawImage(cherryImage, col * BLOCK_SIZE, row * BLOCK_SIZE, this);
                    }
                }
            }
        }
    }
    
    /**
     * Draws the ghost at the x and y position.
     * @param g2d Pac-Man graphics
     * @param n identifier of ghost being drawn
     * @param x position of ghost on x axis
     * @param y position of ghost on y axis
     */
    private void drawGhost(Graphics2D g2d, int n, int x, int y)         //Phase2
    {
        if (n >= 6)
        {
            n = n - 6;
        }
        g2d.drawImage(ghost[n], x, y, this);
    }
    
    /**
     * Moves Pac-Man according to the user input.
     * Reference: Gaspar Coding. (2020, September 4).
     */
    private void movePacman()                                           //Phase2
    {
        int pos;
        short ch;

        if (req_dx == -pacmand_x && req_dy == -pacmand_y) 
        {
            pacmand_x = req_dx;
            pacmand_y = req_dy;
            view_dx = pacmand_x;
            view_dy = pacmand_y;
        }

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) 
        {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) 
            {
                screenData[pos] = (short) (ch & 15);
                score+=10;
            }

            if (req_dx != 0 || req_dy != 0) 
            {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) 
                {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                    view_dx = pacmand_x;
                    view_dy = pacmand_y;
                }
            }
            // standstill check
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) 
            {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        
        // Pacman left-to-right teleport at tunnel
        if (pacman_x == BLOCK_SIZE * 0 && pacman_y == BLOCK_SIZE * 7 && view_dx == -1)
        {
            pacman_x = BLOCK_SIZE * 14 + pacman_x + PACMAN_SPEED * pacmand_x;
        }
        // Pacman right-to-left teleport at tunnel
        else if (pacman_x == BLOCK_SIZE * 14 && pacman_y == BLOCK_SIZE * 7 && view_dx == 1)
        {
            pacman_x = -BLOCK_SIZE * 14 + pacman_x + PACMAN_SPEED * pacmand_x;
        }
        // Normal Pacman lateral movement
        else
        {
            pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        }

        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }
    
    /**
     * Draws Pac-Man direction
     * @param g2d Pac-Man graphics
     */
    private void drawPacman(Graphics2D g2d)                             //Phase2
    {
        if (view_dx == -1) 
        {
            drawPacmanLeft(g2d);
        } 
        else if (view_dx == 1) 
        {
            drawPacmanRight(g2d);
        } 
        else if (view_dy == -1) 
        {
            drawPacmanUp(g2d);
        } 
        else 
        {
            drawPacmanDown(g2d);
        }
    }
    
    /**
     * Draws Pac-man's animation while going up.
     * @param g2d Pac-Man graphics.
     */
    private void drawPacmanUp(Graphics2D g2d)                           //Phase2
    {
        switch (pacmanAnimPos) 
        {
            case 1:
                g2d.drawImage(pacman2up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3up, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4up, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }
    
    /**
     * Draws Pac-man's animation while going down.
     * @param g2d Pac-Man graphics.
     */
    private void drawPacmanDown(Graphics2D g2d)                         //Phase2
    {
        switch (pacmanAnimPos) 
        {
            case 1:
                g2d.drawImage(pacman2down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3down, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4down, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }
    
    /**
     * Draws Pac-man's animation while going left.
     * @param g2d Pac-Man graphics.
     */
    private void drawPacmanLeft(Graphics2D g2d)                         //Phase2
    {
        switch (pacmanAnimPos) 
        {
            case 1:
                g2d.drawImage(pacman2left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3left, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4left, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }
    
    /**
     * Draws Pac-man's animation while going right.
     * @param g2d Pac-Man graphics.
     */
    private void drawPacmanRight(Graphics2D g2d)                        //Phase2
    {
        switch (pacmanAnimPos) 
        {
            case 1:
                g2d.drawImage(pacman2right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 2:
                g2d.drawImage(pacman3right, pacman_x + 1, pacman_y + 1, this);
                break;
            case 3:
                g2d.drawImage(pacman4right, pacman_x + 1, pacman_y + 1, this);
                break;
            default:
                g2d.drawImage(pacman1, pacman_x + 1, pacman_y + 1, this);
                break;
        }
    }
    
    /**
     * Draws the maze using the level data.
     * @param g2d Pac-Man graphics.
     * Reference: Gaspar Coding. (2020, September 4).
     */
    private void drawMaze(Graphics2D g2d) 
    {
        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) 
        {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) 
            {
                g2d.setColor(new Color(120, 160, 255)); // sets color to light blue
            	g2d.setStroke(new BasicStroke(5));//uses stroke of 5 pixels wide

                if ((screenData[i] & 1) != 0) 
                {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) 
                {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) 
                {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) 
                {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) 
                {
                    g2d.setColor(dotColor);
                    g2d.fillRect(x + 11, y + 11, 2, 2);
                }
                i++;
            }
        }
    }
       
    /**
     * Initializes  the level data in to screen data.
     */
    private void initLevel() 
    {
        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) 
        {
            screenData[i] = levelData[i];
        }
        // Reset the cherryCollected array
        cherryCollected = new boolean[CHERRY_LOCATIONS.length]; 
        continueLevel();
    }
    
    /**
     * Continues the level. called every frame.
     * Reference: Gaspar Coding. (2020, September 4).
     */
    private void continueLevel()                                       //Phase 3
    {
        short i;
        int dx = 1;
        int random;

        for (i = 0; i < N_GHOSTS; i++) 
        {
            ghost_y[i] = 7 * BLOCK_SIZE;
            ghost_x[i] = 7 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) 
            {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 12 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        view_dx = -1;
        view_dy = 0;
        dying = false;
    }
    
    /**
     * Loads the Images.
     */
    private void loadImages() 
    {
        for (int n = 0; n < N_GHOSTS; n++)
        {
            ghost[n] = new ImageIcon("src/ghost"+ n + ".gif").getImage();
        }
        cherry = new ImageIcon("src/cherries.png").getImage();
        pacman1 = new ImageIcon("src/pacman.png").getImage();
        heart = new ImageIcon("src/heart.png").getImage();
        pacman2up = new ImageIcon("src/up1.png").getImage();
        pacman3up = new ImageIcon("src/up2.png").getImage();
        pacman4up = new ImageIcon("src/up3.png").getImage();
        pacman2down = new ImageIcon("src/down1.png").getImage();
        pacman3down = new ImageIcon("src/down2.png").getImage();
        pacman4down = new ImageIcon("src/down3.png").getImage();
        pacman2left = new ImageIcon("src/left1.png").getImage();
        pacman3left = new ImageIcon("src/left2.png").getImage();
        pacman4left = new ImageIcon("src/left3.png").getImage();
        pacman2right = new ImageIcon("src/right1.png").getImage();
        pacman3right = new ImageIcon("src/right2.png").getImage();
        pacman4right = new ImageIcon("src/right3.png").getImage();
    }
    
    /**
     * overrides the paintComponent method to make it call the doDrawing method.
     * @param g2d Pac-Man graphics.
     */
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    /**
     * Does all the drawing for the game.
     * @param g Pac-Man graphics.
     */
    private void doDrawing(Graphics g)                                 //Phase 3
    {                                
    	Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, dimension.width, dimension.height);
        drawMaze(g2d);
        drawScore(g2d);
        doAnim();

        if (inGame) 
        {
            playGame(g2d);
            drawCherries(g2d);
        } 
        else 
        {
            introScreen(g2d);
        }

        g2d.drawImage(image, 5, 5, this);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }
    
    /**
     * This class is used to get user input.
     */
    class TAdapter extends KeyAdapter 
    {
        /*
        * Responds to a key release event by resetting the requested direction of movement.
        * @param e the key event
        */
     	@Override
    	public void keyPressed(KeyEvent e) 
        {
            int key = e.getKeyCode();
    	    if (inGame) 
            {
                if (key == KeyEvent.VK_ENTER && timer.isRunning())
                {
                    PACMAN_SPEED = 0;
                    N_GHOSTS = 0;
                    timer.stop();
                }
                else
                {
                    PACMAN_SPEED = 4;
                    N_GHOSTS = 6;
                    timer.start();
                }    
    	        switch (key) 
                {
    	            case KeyEvent.VK_LEFT:
    	            case KeyEvent.VK_A:
    	                req_dx = -1;
    	                req_dy = 0;
    	                break;
    	            case KeyEvent.VK_RIGHT:
    	            case KeyEvent.VK_D:
    	                req_dx = 1;
    	                req_dy = 0;
    	                break;
    	            case KeyEvent.VK_UP:
    	            case KeyEvent.VK_W:
    	                req_dx = 0;
    	                req_dy = -1;
    	                break;
    	            case KeyEvent.VK_DOWN:
    	            case KeyEvent.VK_S:
    	                req_dx = 0;
    	                req_dy = 1;
    	                break;
    	            case KeyEvent.VK_ESCAPE:
    	                if (timer.isRunning()) 
                        {
    	                    inGame = false;
    	                }
    	                break;
                }
            }          
        }
    	
    	/**
    	 * gets user input upon button release.
    	 */
        @Override
        public void keyReleased(KeyEvent e) 
        {
            int key = e.getKeyCode();

            if (key == Event.LEFT || key == Event.RIGHT
                    || key == Event.UP || key == Event.DOWN) 
            {
                req_dx = 0;
                req_dy = 0;
            }
        }
    }
    
    /**
     * Gets user input for when they click on either the start or exit button.
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        if (e.getSource() == startButton) 
        {
            requestFocusInWindow(); // set focus        
            inGame = true;          // Start the game
            initGame();
            startButton.setVisible(false);
            exitButton.setVisible(false);

        } else if (e.getSource() == exitButton) 
        {
            inGame = false;         // Exit the game
            System.exit(0);         // Terminate the program
        }
        repaint();
    }
      
    /**
     * Creates clip from audio files
     */
    public void createClip()
    {
    	try
    	{
    		// Created main clip
    		clipMain = AudioSystem.getClip();
        	clipMain.open(AudioSystem.getAudioInputStream(new File("src/main.wav")));
        	
        	// Creates game clip
        	clipAudio = AudioSystem.getClip();
        	clipAudio.open(AudioSystem.getAudioInputStream(new File("src/audio.wav")));	
    	}
    	catch (Exception exc)
    	{
            exc.printStackTrace(System.out);
        }
    }
    
    /**
     * Plays audio clips 
     */
    public void playSound()
    {
    	try
    	{  		
            if (inGame == false)
            {
                // Stops other audio
                clipAudio.stop();
    			
                // Starts new audio
                clipMain.start();
                clipMain.loop(Clip.LOOP_CONTINUOUSLY); // Loops sound continuously
            }
    		if(inGame == true)
    		{
                    // Stops other audio
                    clipMain.stop();
    		
                    // Starts new audio
                    clipAudio.start();
                    clipAudio.loop(Clip.LOOP_CONTINUOUSLY); // Loops sound continuously
    		}  	    	
    	}
    	catch (Exception exc)
    	{
            exc.printStackTrace(System.out);
   	}
    }
    
    /**
    * Starts the game.
    */
    private void initGame() 
    {
        pacsLeft = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 5;
        currentSpeed = 3;
    }
}
