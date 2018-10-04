package scrabble.client.gui;

import scrabble.Models.Player;
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
import java.util.ArrayList;

public class GameWindow implements Runnable {

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
        gridPanel.delLastMoveValue();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GuiController.get().sendQuitMsg();

            }
        });
        frame.setTitle("Scrabble Game");
        frame.setSize(860, 740);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

//      gridPanel.setAllowDrag(true);

        passBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] lastMove = gridPanel.getLastMove();
                if (lastMove[0] != -1 && lastMove[1] != -1) {
                    // Placing but pass
                    System.err.println("sendPass: " + gridPanel.getCharacter(lastMove[0], lastMove[1]));
                    GuiController.get().sendPass(lastMove, gridPanel.getCharacter(lastMove[0], lastMove[1]));
                    gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                    gridPanel.delLastMoveValue();
                }
                else {
                    // No Placing
                    GuiController.get().sendPass(lastMove, '0');
                }
                gridPanel.setAllowDrag(false);
                System.err.println("set to false 3");
            }
        });

        voteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int[] lastMove = gridPanel.getLastMove();
                gridPanel.drawUneditable(lastMove[0], lastMove[1]);
                gridPanel.getSelectArea();
//                gridPanel.delLastMoveValue();
                gridPanel.setAllowDrag(false);
                System.err.println("set to false 2");
            }
        });

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                //GuiController.get().quitGame();
                frame.dispose();
            }
        });
    }

    void startOneTurn() {
        System.err.println("set to true 1");
        gridPanel.setAllowDrag(true);
    }

    /*
    void placingChar(int[] lastMove, char c) {
        clientManager.placingChar(lastMove, c);
    }
    */

    void sendSelect(int[] lastMove, int sx, int sy, int ex, int ey) {
        char c = gridPanel.getCharacter(lastMove[0], lastMove[1]);
        GuiController.get().sendVote(lastMove, c, sx, sy, ex, ey);
    }

    synchronized void updatePlayerList(Player[] playerList) {
        playerPanel.updatePlayerList(playerList);
    }

    void updateBoard(char[][] board) {
        gridPanel.updateBoard(board);
    }

    void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
    }

    void showVoteRequest(int inviterId, int[] startPosition, int[] endPosition) {
        String inviterName = PlayerPanel.get().getPlayerName(inviterId);
        String word = GameGridPanel.get().getWord(startPosition, endPosition);
        int confirmed = JOptionPane.showConfirmDialog(null, inviterName+"'s Vote:%n" + "Do you agree " + word + " is a word?"
                ,"Vote", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            GuiController.get().sendVoteResponse(true);
        }
        else {
            GuiController.get().sendVoteResponse(false);
        }
    }

    void showWinners(Player[] players) {
        String message = new String();
        message = "Winner:%n";
        for (Player player: players) {
            message = message + player.getUser().getUserName() + "  ";
        }
        showDialog(message);
        frame.dispose();
    }


}
