package scrabble.client.gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import javax.swing.JComponent;

public class GameGridPanel extends JPanel{

    private static final long serialVersionUID = 1L;

    private static final int GRID_SIZE = 20;

    private JButton[][] grid = new JButton[GRID_SIZE][GRID_SIZE];
    private int[] lastMove = new int[2];
    private int vHead, vTail, vJ, hHead, hTail, hI;
    private int num = 0;

    private boolean allowDrag = false;
    private boolean allowSelectHead = false;
    private int[] lastMove_bak = new int[2];

    private char[][] bbb = new char[GRID_SIZE][GRID_SIZE];

    public static class GameGridPanelHolder {
        private static final GameGridPanel INSTANCE = new GameGridPanel();
    }

    private GameGridPanel() {
        this.setSize(600, 600);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new JButton(" ");
                grid[i][j].setFont(new Font("Arial", Font.PLAIN, 2));
                grid[i][j].setTransferHandler(new ReceiveTransferHandler(i, j));
                //grid[i][j].addMouseListener(new RightClickListener(i, j));
                add(grid[i][j]);
            }
    }

    public static final GameGridPanel get() {
        return GameGridPanelHolder.INSTANCE;
    }

    private boolean isInGrid(int i, int j) {
        if (i >= 0 && i < GRID_SIZE && j >= 0 && j < GRID_SIZE)
            return true;
        return false;
    }

    public int[] getLastMove() {
        return lastMove;
    }

    public char getCharacter(int i, int j) {
        char brick = ' ';
        if (isInGrid(i, j))
            brick =  grid[i][j].getText().charAt(0);
        return brick;

    }

    public void setAllowDrag(boolean flag) {
        allowDrag = flag;
    }

    public void delLastMoveValue() {
        lastMove[0] = -1;
        lastMove[1] = -1;
    }

    public void showValidArea(int startRow, int endRow, int startCol, int endCol, Color color) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                grid[i][j].setBackground(color);
                grid[i][j].setOpaque(true);
                grid[i][j].setBorderPainted(false);
            }
        }
    }

    public void resetToDefault() {
        get().delLastMoveValue();
        for (int i = vHead; i <= vTail; i++) {
            grid[i][vJ].setBackground(UIManager.getColor("Button.background"));
            grid[i][vJ].setOpaque(false);
            grid[i][vJ].setBorderPainted(true);
            drawUneditable(i, vJ);
        }
        for (int j = hHead; j <= hTail; j++) {
            grid[hI][j].setBackground(UIManager.getColor("Button.background"));
            grid[hI][j].setOpaque(false);
            grid[hI][j].setBorderPainted(true);
            drawUneditable(hI, j);
        }
    }

    // grid[i][j] ~ grid[iend][j]
    // grid[x][y] ~ grid[x][yend]
    public void headBlink(int i, int j, int x, int y, int iend, int yend) {
        Timer timer1, timer2;
        lastMove_bak=lastMove;
        timer1 = new Timer(500, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter % 2 == 0) {
                    grid[i][j].setBackground(new Color(173, 216, 230));
                } else {
                    grid[i][j].setBackground(new Color(224, 255, 255));
                }
                System.out.printf("Blink1: %d %d", i, j);
                System.out.println();
            }
        });
        timer1.start();

        timer2 = new Timer(500, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter % 2 == 0) {
                    grid[x][y].setBackground(new Color(143, 188, 143));
                }
                else {
                    grid[x][y].setBackground(new Color(240, 255, 240));
                }
                System.out.printf("Blink2: %d %d", x, y);
                System.out.println();
            }
        });
        timer2.start();

        if (i == iend) {
            grid[i][j].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (allowSelectHead) {
                        GameWindow.get().sendSelect(lastMove_bak, i, j, iend, j); //行起点，列，行终点，列
                        timer1.stop();
                        timer2.stop();
                        allowSelectHead = false;
                        resetToDefault();
                    }
                }
            });
            for (int col = y; col <= yend; col++) {
                if (i == x && col == j)	continue;
                grid[x][col].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        if (allowSelectHead) {
                            GameWindow.get().sendSelect(lastMove_bak, x, y, x, yend); //行起点，列，行终点，列
                            timer1.stop();
                            timer2.stop();
                            allowSelectHead = false;
                            resetToDefault();
                        }
                    }
                });
            }
            return;
        }
        if (y == yend) {
            grid[x][y].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (allowSelectHead) {
                        GameWindow.get().sendSelect(lastMove_bak, x, y, x, yend); //行起点，列，行终点，列
                        timer1.stop();
                        timer2.stop();
                        allowSelectHead = false;
                        resetToDefault();
                    }
                }
            });
            for (int row = i; row <= iend; row++) {
                if (row == x && j == y)	continue;
                grid[row][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        if (allowSelectHead) {
                            GameWindow.get().sendSelect(lastMove_bak, i, j, iend, j); //行起点，列，行终点，列
                            timer1.stop();
                            timer2.stop();
                            allowSelectHead = false;
                            resetToDefault();
                        }
                    }
                });
            }
            return;
        }

        for (int row = i; row <= iend; row++) {
            grid[row][j].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (allowSelectHead) {
                        GameWindow.get().sendSelect(lastMove_bak, i, j, iend, j); //行起点，列，行终点，列
                        timer1.stop();
                        timer2.stop();
                        allowSelectHead = false;
                        resetToDefault();
                    }
                }
            });
        }

        for (int col = y; col <= yend; col++) {
            grid[x][col].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (allowSelectHead) {
                        GameWindow.get().sendSelect(lastMove_bak, x, y, x, yend); //行，列起点，行，列终点
                        timer1.stop();
                        timer2.stop();
                        allowSelectHead = false;
                        resetToDefault();
                    }
                }
            });
        }
    }

    public void getSelectArea() {
        int i = lastMove[0], j = lastMove[1];
        if (!isInGrid(i, j))		return;
        while (i >= 0 && ! grid[i][j].getText().equals(" ")) {
            i--;
        }
        vHead = i+1;
        i = lastMove[0];
        while (i < GRID_SIZE && !grid[i][j].getText().equals(" ")) {
            i++;
        }
        vTail = i-1;
        vJ = j;
        showValidArea(vHead, vTail, j, j, new Color(173, 216, 230));

        i = lastMove[0];
        j = lastMove[1];
        while (j >= 0 && !grid[i][j].getText().equals(" ")) {
            j--;
        }
        hHead = j+1;
        j = lastMove[1];
        while (j < GRID_SIZE && !grid[i][j].getText().equals(" ")) {
            j++;
        }
        hTail = j-1;
        hI = i;
        showValidArea(i, i, hHead, hTail, new Color(143, 188, 143));

        JButton btn = grid[lastMove[0]][lastMove[1]];
        btn.setBackground(Color.PINK);
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        // Allow to select vote area
        allowSelectHead = true;
        headBlink(vHead, vJ, hI, hHead, vTail, hTail);
    }

    /*
    private void delOutline(int i, int j) {
        JButton btn = grid[i][j];
        btn.setBorder(UIManager.getBorder("Button.border"));
    }
    */

    public void drawUneditable(int i, int j) {
        JButton btn = grid[i][j];
//        System.err.println(btn.getText()+"MMMMMerlin");
        Border roundedBorder = new LineBorder(Color.GRAY, 2, true);
        btn.setBorder(roundedBorder);
        btn.setForeground(Color.DARK_GRAY);
    }

    private void drawCurOutline(int i, int j) {
        JButton btn = grid[i][j];
        Border roundedBorder = new LineBorder(Color.PINK, 2, true);
        btn.setBorder(roundedBorder);
    }

    /*
    public class RightClickListener extends MouseAdapter {
        private int rowIndex, colIndex;

        public RightClickListener(int i, int j) {
            rowIndex = i;
            colIndex = j;
        }

        public void mouseClicked(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (rowIndex == lastMove[0] && colIndex == lastMove[1]) {
                    JButton btn = (JButton) e.getSource();
                    btn.setText(" ");
                    delOutline(rowIndex, colIndex);
                    //gameWindow.placingChar(lastMove, ' ');
                    --num;
                }
            }
        }
    }
    */

    public class ReceiveTransferHandler extends TransferHandler {

        private static final long serialVersionUID = 1L;
        private int rowIndex, colIndex;

        public ReceiveTransferHandler(int i, int j) {
            rowIndex = i;
            colIndex = j;
        }

        public boolean isEmpty(int i, int j) {
            if (i >= 0 && i < GRID_SIZE && j >=0 && j < GRID_SIZE && !grid[i][j].getText().equals(" ")) {
                return false;
            }
            return true;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            boolean flag = support.isDataFlavorSupported(DataFlavor.stringFlavor) && allowDrag;
            System.err.print("Flag to allowDrag: ");
            if (flag) {
                System.err.println("True");
            }
            else
                System.err.println("False");
            Component comp = support.getComponent();
            if (!((JButton) comp).getText().equals(" ")) {
                flag = false;
                System.err.println("Flag to false 1");
                System.err.println("in grid" + (int)((JButton) comp).getText().charAt(0));
                System.err.println("length" + ((JButton) comp).getText().length());
                System.err.println("ppp" + (int)" ".charAt(0));
            }
            if (isEmpty(rowIndex-1, colIndex) && isEmpty(rowIndex, colIndex-1) && isEmpty(rowIndex+1, colIndex) && isEmpty(rowIndex, colIndex+1) && num!=0) {
                flag = false;
                System.err.println("Flag to false 2");
            }
            if (flag == true)
                System.err.println("can!");
            else
                System.err.println("cannot!");
            return flag;
        }

        @Override
        public boolean importData(JComponent comp, Transferable t) {
            try {
                Object str = t.getTransferData(DataFlavor.stringFlavor);
                if (str instanceof String) {
                    if (comp instanceof JButton) {
                        if (((JButton) comp).getText().equals(" ")) {
                            ((JButton) comp).setText(str.toString());
                            ++num;
                            lastMove[0] = rowIndex;
                            lastMove[1] = colIndex;
                            allowDrag = false;
                            System.err.println("set to false");
                            //GameWindow.get().placingChar(lastMove, str.toString().charAt(0));
                            drawCurOutline(rowIndex, colIndex);
                            return true;
                        }
                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return false;
        }
    }

    String getWord(int[] startPosition, int[] endPosition) {
        String s = new String();
        for (int i = startPosition[0]; i <= endPosition[0]; i++)
            for (int j = startPosition[1]; j <= endPosition[1]; j++)
                s = s + getCharacter(i,j);
        return s;
    }

    void updateBoard(char[][] board) {
        bbb = board;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j].setText(Character.toString(board[i][j]));
            }
        }
    }
}
