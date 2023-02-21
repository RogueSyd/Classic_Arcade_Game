/*
Pac-Man Application: Phase 2 Source
CMSC 495 
Instructor: Shanna Kuchenbecker 
John Borra, Sarah Drury, Oanh Woodworth, Yeng Veng, and Thayne Emery
*/
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Application extends JPanel implements ActionListener 
{
    private Dimension d;
    private final Font smallFont = new Font("Helvetica", Font.BOLD,14);

    private Image ii;
    private final Color dotColor = new Color(192, 192, 0);
    private Color mazeColor;

    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int PAC_ANIM_DELAY = 2;
    private final int PACMAN_ANIM_COUNT = 4;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 6;

    private int pacAnimCount = PAC_ANIM_DELAY;
    private int pacAnimDir = 1;
    private int pacmanAnimPos = 0;
    private int N_GHOSTS = 6;
    private int pacsLeft, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image ghost,heart;
    private Image pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
    private Image pacman3up, pacman3down, pacman3left, pacman3right;
    private Image pacman4up, pacman4down, pacman4left, pacman4right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy, view_dx, view_dy;

    private final short levelData[] = 
    {
            19, 26, 26, 26, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 0, 17, 16, 16, 24, 16, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 18, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 16, 24, 20,
            25, 16, 16, 16, 24, 24, 28, 0, 25, 24, 24, 16, 20, 0, 21,
            1, 17, 16, 20, 0, 0, 0, 0, 0, 0, 0, 17, 20, 0, 21,
            1, 17, 16, 16, 18, 18, 22, 0, 19, 18, 18, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 20, 0, 17, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 18, 16, 16, 16, 16, 20, 0, 21,
            1, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 21,
            1, 25, 24, 24, 24, 24, 24, 24, 24, 24, 16, 16, 16, 18, 20,
            9, 8, 8, 8, 8, 8, 8, 8, 8, 8, 25, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    private JButton startButton;
    private JButton exitButton;
    
    public Application()                                                //Phase1
    {
        loadImages();
        initVariables();    
        initBoard();
        
        //create Start and Exit Buttons
        setLayout(null);
        startButton = new JButton("Start");
        startButton.setBounds(100, 150, 100, 30);
        startButton.addActionListener(this);
        add(startButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(100, 200, 100, 30);
        exitButton.addActionListener(this);
        add(exitButton);
    }

    private void initBoard()                                            //Phase1
    {      
        setFocusable(true);
        setBackground(Color.black);
    }

    private void initVariables()                                        //Phase1
    {      
        screenData = new short[N_BLOCKS * N_BLOCKS];
        mazeColor = new Color(5, 100, 5);
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    public void addNotify() 
    {
        super.addNotify();
        initGame();
    }

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
        }
    }

    private void showIntroScreen(Graphics2D g2d)                        //Phase1
    {      
        // Draw Pac-Man title
        g.setColor(Color.YELLOW);
        Font titleFont = new Font("Helvetica", Font.BOLD, 48);
        g.setFont(titleFont);
        FontMetrics titleMetrics = g.getFontMetrics(titleFont);
        String title = "Pac-Man";
        g.drawString(title, (getWidth() - titleMetrics.stringWidth(title)) / 2, 
        		getHeight() / 3);
        
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
    
    }

    private void drawScore(Graphics2D g)                                //Phase1 
    {      
        int i;
        String s;

        g.setFont(smallFont);
        g.setColor(new Color(26, 12, 255));
        s = "SCORE:  " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (i = 0; i < pacsLeft; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

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

    private void death()                                                //Phase2
    {
        pacsLeft--;

        if (pacsLeft == 0) 
        {
            inGame = false;
        }
        continueLevel();
    }

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

            ghost_x[n] = ghost_x[n] + (ghost_dx[n] * ghostSpeed[n]);
            ghost_y[n] = ghost_y[n] + (ghost_dy[n] * ghostSpeed[n]);
            drawGhost(g2d, ghost_x[n] + 1, ghost_y[n] + 1);

            if (pacman_x > (ghost_x[n] - 12) && pacman_x < (ghost_x[n] + 12)
                    && pacman_y > (ghost_y[n] - 12) && pacman_y < (ghost_y[n] + 12)
                    && inGame) 
            {
                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y)                //Phase2
    {
        g2d.drawImage(ghost, x, y, this);
    }

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
                score++;
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
        
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

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

    private void drawMaze(Graphics2D g2d) 
    {

    }

    private void initGame() 
    {
        pacsLeft = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void initLevel() 
    {

    }

    private void continueLevel() 
    {

    }

    private void loadImages() 
    {
        ghost = new ImageIcon("src/ghost.gif").getImage();
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

    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) 
    {

    }

    class TAdapter extends KeyAdapter 
    {

        @Override
        public void keyPressed(KeyEvent e) 
        {

        }

        @Override
        public void keyReleased(KeyEvent e) 
        {
            
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
        	requestFocusInWindow(); // set focus 
            // Start the game
        	inGame = true;
            initGame();

        } else if (e.getSource() == exitButton) {
            // Exit the game
            inGame = false;
        }
        repaint();
    }
}
