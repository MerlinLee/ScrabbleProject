package scrabble.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
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
    private int curScore;

    private boolean allowDrag = false;
    private boolean allowSelectHead = false;

    public static class GameGridPanelHolder {
        private static final GameGridPanel INSTANCE = new GameGridPanel();
    }

    private GameGridPanel() {
        this.setSize(600, 600);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));

        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = new JButton(" ");
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

    int getCurScore() {
        return curScore;
    }

    public char getCharacter(int i, int j) {
        if (isInGrid(i, j))
            return grid[i][j].getText().charAt(0);
        return '?';
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

    public void headBlink(int i, int j, int x, int y, int iend, int yend) {

        Timer timer1 = new Timer(500, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter % 2 == 0) {
                    grid[i][j].setBackground(new Color(173, 216, 230));
                } else {
                    grid[i][j].setBackground(new Color(224, 255, 255));
                }
            }
        });


        Timer timer2 = new Timer(500, new ActionListener() {
            private int counter = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter % 2 == 0) {
                    grid[x][y].setBackground(new Color(143, 188, 143));
                } else {
                    grid[x][y].setBackground(new Color(240, 255, 240));
                }
            }
        });

        timer1.start();
        timer2.start();

        grid[i][j].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (allowSelectHead) {
                    System.out.printf("%d %d", i, j);
                    GameWindow.get().sendSelect(lastMove, i, j, iend, j);
                    curScore = iend - i + 1;
                }
                timer1.stop();
                allowSelectHead = false;
                resetToDefault();
            }
        });
        grid[x][y].addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (allowSelectHead) {
                    System.out.printf("%d %d", x, y);
                    GameWindow.get().sendSelect(lastMove, x, y, x, yend);
                    curScore = yend - y + 1;
                }
                timer2.stop();
                allowSelectHead = false;
                resetToDefault();
            }
        });
    }

    public void getSelectArea() {
        allowSelectHead = true;
        int i = lastMove[0], j = lastMove[1];
        if (!isInGrid(i, j))		return;
        while (i >= 0 && grid[i][j].getText() != " ") {
            i--;
        }
        vHead = i+1;
        i = lastMove[0];
        while (i < GRID_SIZE && grid[i][j].getText() != " ") {
            i++;
        }
        vTail = i-1;
        vJ = j;
        showValidArea(vHead, vTail, j, j, new Color(173, 216, 230));

        i = lastMove[0];
        j = lastMove[1];
        while (j >= 0 && grid[i][j].getText() != " ") {
            j--;
        }
        hHead = j+1;
        j = lastMove[1];
        while (j < GRID_SIZE && grid[i][j].getText() != " ") {
            j++;
        }
        hTail = j-1;
        hI = i;
        showValidArea(i, i, hHead, hTail, new Color(143, 188, 143));

        JButton btn = grid[lastMove[0]][lastMove[1]];
        btn.setBackground(Color.PINK);
        btn.setOpaque(true);
        btn.setBorderPainted(false);

        headBlink(vHead, vJ, hI, hHead, vTail, hTail);
    }

    private void delOutline(int i, int j) {
        JButton btn = grid[i][j];
        btn.setBorder(UIManager.getBorder("Button.border"));
    }

    public void drawUneditable(int i, int j) {
        JButton btn = grid[i][j];
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
            if (i >= 0 && i < GRID_SIZE && j >=0 && j < GRID_SIZE && grid[i][j].getText() != " ") {
                return false;
            }
            return true;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            boolean flag = support.isDataFlavorSupported(DataFlavor.stringFlavor) && allowDrag;
            Component comp = support.getComponent();
            if (((JButton) comp).getText() != " ") {
                flag = false;
            }
            if (isEmpty(rowIndex-1, colIndex) && isEmpty(rowIndex, colIndex-1) && isEmpty(rowIndex+1, colIndex) && isEmpty(rowIndex, colIndex+1) && num!=0) {
                flag = false;
            }
            return flag;
        }

        @Override
        public boolean importData(JComponent comp, Transferable t) {
            try {
                Object str = t.getTransferData(DataFlavor.stringFlavor);
                if (str instanceof String) {
                    if (comp instanceof JButton) {
                        if (((JButton) comp).getText() == " ") {
                            ((JButton) comp).setText(str.toString());
                            ++num;
                            lastMove[0] = rowIndex;
                            lastMove[1] = colIndex;
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
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j].setText(Character.toString(board[i][j]));
            }
        }
    }
}
