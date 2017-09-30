// classic Line 98 game
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.*;

// class line for new game
public class Mines extends JPanel 
{   
    public static void main(String[] args) 
    {
        JFrame window = new JFrame("Line");
        Mines content = new Mines();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width - window.getWidth())/2,
                (screensize.height - window.getHeight())/2 );
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setVisible(true);
    }
    
    private JButton newGameButton;
    private JButton resignButton;
    private JLabel message;
    private JLabel message2;
    
    // initiate class line
    public Mines() 
    {
        setLayout(null);
        setPreferredSize( new Dimension(730,650) );
        setBackground(new Color(0,150,0));
        
        Board board = new Board();
        
        // set new game terminal
        add(board);
        add(newGameButton);
        add(resignButton);
        add(message);
        add(message2);
        
        board.setBounds(0,0,560,560);
        newGameButton.setBounds(590,60,120,30);
        resignButton.setBounds(590,120,120,30);
        message.setBounds(100,600,350,30);
        message2.setBounds(590,180,120,30);
    }
    
    // class board where actual game plays
    private class Board extends JPanel implements ActionListener, MouseListener 
    {
        private static final int INFINITY = Integer.MAX_VALUE;
        CaroSquare square;
        Timer timer;
        boolean gameInProgress;
        int level = 0;
        int r, c = 0;
        int openSquare = 0;
        
        // initiate Board object
        Board() 
        {
            setBackground(Color.BLACK);
            addMouseListener(this);
            resignButton = new JButton("Resign");
            resignButton.addActionListener(this);
            newGameButton = new JButton("New Game");
            newGameButton.addActionListener(this);
            message = new JLabel ("",JLabel.CENTER);
            message.setFont(new Font("Serif", Font.BOLD, 14));
            message.setBackground(Color.green);
            message2 = new JLabel ("", JLabel.CENTER);
            message2.setFont(new Font("Serif", Font.BOLD, 14));
            message2.setBackground(Color.green);
            square = new CaroSquare();
            doNewGame();
        }
        
        // check for user click's on button
        public void actionPerformed(ActionEvent evt) 
        {
            Object src = evt.getSource();
            if (src == newGameButton)
                doNewGame();
            else if (src == resignButton)
                doResign();
        }
        
        // start a new game
        void doNewGame() 
        {
            // check if user's in a current game
            if (gameInProgress == true) 
            {
                message.setText("Finish the current game first");
                return;
            }
            square.setUpgame();
            message.setText("New Game");
            message2.setText("Your score: 0");
            gameInProgress = true;
            newGameButton.setEnabled(false);
            resignButton.setEnabled(true);
            repaint();
        }
        
        // end current game
        void doResign() 
        {
            // check if user's in a current game
            if (gameInProgress == false) 
            {
                message.setText("There is no game in progress");
                return;
            }
            else 
            {
                message.setText("You give up");
                newGameButton.setEnabled(true);
                resignButton.setEnabled(false);
                gameInProgress = false;
                return;
            }
        }
        
        // user selects a square
        void doClickSquare(int row, int col) 
        {
            if (square.board[row][col].hidden == false)
                message.setText("You've already clicked this square");
            else 
            {
                square.board[row][col].hidden = false;
                if (square.board[row][col].number == CaroSquare.MINE)
                {
                    message.setText("You lost");
                    newGameButton.setEnabled(true);
                    resignButton.setEnabled(false);
                    gameInProgress = false;
                }
                else if (square.board[row][col].number == CaroSquare.EMPTY)
                {
                    square.board[row][col].hidden = false;
                    doOpenSquare(row, col);
                }
            }

            if (checkWinMove())
            {
                message.setText("Congratulation! You win");
                newGameButton.setEnabled(true);
                resignButton.setEnabled(false);
                gameInProgress = false;
            }

            repaint();
            return;
        }
        
        void doOpenSquare(int row, int col)
        {
            int[] dirX = {1, 0, 0, -1};
            int[] dirY = {0, 1, -1, 0};

            for (int i = 0; i < 4; i++) 
            {
                r = row + dirX[i];
                c = col + dirY[i];
                if (r >= 0 && r < 9 && c >= 0 && c < 9) 
                {
                    if (square.board[r][c].number == CaroSquare.EMPTY && square.board[r][c].hidden == true)
                    {
                        square.board[r][c].hidden = false;
                        doOpenSquare(r, c);
                    }
                    else if (square.board[r][c].number != CaroSquare.MINE)
                        square.board[r][c].hidden = false;
                }
            }
        }

        boolean checkWinMove() 
        {
            for (int row = 0; row < 9; row++)
            {
                for (int col = 0; col < 9; col++)
                {
                    if (square.board[row][col].number != CaroSquare.MINE && square.board[row][col].hidden == true)
                    {
                        return false;
                    }
                }
            }
            repaint();
            return true;
        }
        
        public void paintComponent(Graphics g) 
        {          
            Graphics2D g2 = (Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g.drawRect(0,0,getSize().width - 1, getSize().height - 1);
            g.drawRect(1,1,getSize().width - 3, getSize().height - 3);
            
            for (int i = 0; i < 9; i++) 
            {
                g.drawLine(0 + i*60 + 2*i, 0, 0 + i*60 + 2*i, 560);
                g.drawLine(1 + i*60 + 2*i, 1, 1 + i*60 + 2*i, 560);
                g.drawLine(0, 0 + i*60 + 2*i, 560, 0 + i*60 + 2*i);
                g.drawLine(1, 1 + i*60 + 2*i, 560, 1 + i*60 + 2*i);
            }
            
            for (int row = 0; row < 9; row++) 
            {
                for (int col = 0; col < 9; col++) 
                {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(2 + col*60 + 2*col, 2 + row*60 + 2*row, 60, 60);
                    if (square.markedAt(row, col))
                    {
                        g.setColor(Color.WHITE);
                        g.fillOval(4 + col*60 + 2*col, 4 + row*60 + 2*row, 55, 55);
                    }
                    if (!square.hiddenAt(row, col))
                    {
                        switch (square.numberAt(row, col)) 
                        {
                            case CaroSquare.EMPTY:
                                g.setColor(Color.GRAY);
                                break;
                            case CaroSquare.RED:
                                g.setColor(Color.RED);
                                break;
                            case CaroSquare.BLACK:
                                g.setColor(Color.BLACK);
                                break;
                            case CaroSquare.BLUE:
                                g.setColor(Color.BLUE);
                                break;
                            case CaroSquare.CYAN:
                                g.setColor(Color.CYAN);
                                break;
                            case CaroSquare.YELLOW:
                                g.setColor(Color.YELLOW);
                                break;
                            case CaroSquare.MINE:
                                g.setColor(Color.BLACK);
                                break;
                        }
                        if (square.numberAt(row, col) == CaroSquare.MINE)
                            g.fillOval(4 + col*60 + 2*col, 4 + row*60 + 2*row, 55, 55);
                        else
                            g.fillOval(22 + col*60 + 2*col, 22 + row*60 + 2*row, 20, 20);
                    }            
                }
            }
        }
        
        public void mousePressed(MouseEvent evt) 
        {
            if (gameInProgress == false)
                message.setText("Click \"New Game\" to start a new game.");
            else if (SwingUtilities.isRightMouseButton(evt))
            {
                int col = (evt.getX() - 2)/62;
                int row = (evt.getY() - 2)/62;
                if (col >= 0 && col < 9 && row >= 0 && row < 9)
                {
                    if (square.board[row][col].marked == true)
                        square.board[row][col].marked = false;
                    else
                        square.board[row][col].marked = true;
                }
                repaint();
            }
            else if (SwingUtilities.isLeftMouseButton(evt))
            {
                int col = (evt.getX() - 2)/62;
                int row = (evt.getY() - 2)/62;
                if (col >= 0 && col < 9 && row >= 0 && row < 9)
                    doClickSquare(row, col);
            }
        }
        public void mouseReleased(MouseEvent evt) {}
        public void mouseClicked(MouseEvent evt) {}
        public void mouseEntered(MouseEvent evt) {}
        public void mouseExited(MouseEvent evt) {}   
    }
    
    private class CaroSquare 
    { 
        static final int START = -1, EMPTY = 0, RED = 1, BLACK = 2, BLUE = 3, CYAN = 4, YELLOW = 5, MINE = 11;
        private EachSquare[][] board;
        
        CaroSquare() 
        {
            board = new EachSquare[9][9];
            for (int row = 0; row < 9; row++) 
            {
                for (int col = 0; col < 9; col++) 
                {
                    board[row][col] = new EachSquare();
                }
            }
        }
        
        int numberAt(int row, int col) 
        {
            return board[row][col].number;
        }
        
        boolean hiddenAt(int row, int col)
        {
            return board[row][col].hidden;
        }
        
        boolean markedAt(int row, int col)
        {
            return board[row][col].marked;
        }
        
        void setUpgame() 
        {
            int r, c;
            int mineCount = 0;
            int emptyCount = 0;
            int numberCount = 0;
            for (int row = 0; row < 9; row++) 
            {
                for (int col = 0; col < 9; col++) 
                {
                    board[row][col].number = START;
                    board[row][col].hidden = true;
                    board[row][col].marked = false;
                }
            }
            while (mineCount < 10)
            {
                int randRow = (int)(Math.random()*8);
                int randCol = (int)(Math.random()*8);

                if (board[randRow][randCol].number != MINE)
                {
                    board[randRow][randCol].number = MINE;
                    mineCount++;
                }
            }

            int[] dirX = {1, 1, 1, 0, 0, -1, -1, -1};
            int[] dirY = {0, 1, -1, 1, -1, 0, 1, -1};

            for (int row = 0; row < 9; row++) 
            {
                for (int col = 0; col < 9; col++) 
                {
                    if (board[row][col].number != MINE)
                    {
                        numberCount = 0;
                        for (int i = 0; i < 8; i++) 
                        {
                            r = row + dirX[i];
                            c = col + dirY[i];
                            if (r >= 0 && r < 9 && c >= 0 && c < 9 && board[r][c].number == MINE) 
                            {
                                numberCount++;
                            }
                        }
                        if (numberCount > 5)
                        {
                            numberCount = 5;
                        }
                        board[row][col].number = numberCount;
                    }
                }
            } 
        }
    }

    private class EachSquare 
    {
        static final int START = -1, EMPTY = 0, RED = 1, BLACK = 2, BLUE = 3, CYAN = 4, YELLOW = 5, MINE = 11;
        int number;
        boolean hidden;
        boolean marked;
        
        EachSquare() 
        {
            number = START;
            hidden = true;
            marked = false;
        }
    }  
}