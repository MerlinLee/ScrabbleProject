package scrabble.client.gui;

import scrabble.Models.Users;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameWindow implements Runnable {

    private ClientController clientManager;

    private JFrame frame;
    private GameGridPanel gridPanel = GameGridPanel.get();
    private GameAlphabet alphabetPanel = GameAlphabet.get();
    private PlayerPanel playerPanel = PlayerPanel.get();
    private JButton passBtn, voteBtn;

    public static class GameWindowHolder {
        private static final GameWindow INSTANCE = new GameWindow();
    }

    private GameWindow() {

    }

    public static final GameWindow get() {
        return GameWindowHolder.INSTANCE;
    }

    void setClient(ClientController client) {
        clientManager = client;
    }

    @Override
    public void run() {
        initialize();
        this.frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Scrabble Game");
        frame.setSize(860, 740);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        gridPanel.setBounds(20, 20, 600, 600);
        frame.getContentPane().add(gridPanel);

        alphabetPanel.setBounds(125, 640, 390, 60);
        frame.getContentPane().add(alphabetPanel);

        playerPanel.setBounds(640, 20, 200, 300);
        frame.getContentPane().add(playerPanel);

        passBtn = new JButton("PASS");
        passBtn.setBounds(640, 640, 100, 60);
        voteBtn = new JButton("VOTE");
        voteBtn.setBounds(740, 640, 100, 60);
        frame.add(passBtn);
        frame.add(voteBtn);

        gridPanel.setAllowDrag(true);

        passBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] lastMove = new int[2];
                lastMove = gridPanel.getLastMove();
                clientManager.sendPass(lastMove, gridPanel.getCharacter(lastMove[0], lastMove[1]));
                gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                gridPanel.delLastMoveValue();
                gridPanel.setAllowDrag(false);
            }
        });

        voteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] lastMove = new int[2];
                int[] selectArea = new int[4];
                lastMove = gridPanel.getLastMove();
                gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                gridPanel.getSelectArea();
                gridPanel.delLastMoveValue();
                gridPanel.setAllowDrag(false);
            }
        });

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                clientManager.quitGame();
                frame.dispose();
            }
        });
    }

    void startOneTurn() {
        gridPanel.setAllowDrag(true);
    }

    /*
    void placingChar(int[] lastMove, char c) {
        clientManager.placingChar(lastMove, c);
    }
    */

    void sendSelect(int[] lastMove, int sx, int sy, int ex, int ey) {
        char c = gridPanel.getCharacter(lastMove[0], lastMove[1]);
        clientManager.sendVote(lastMove, c, sx, sy, ex, ey);
    }
}
