package scrabble.client.gui;

import scrabble.Models.Player;
import scrabble.Models.Users;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel;

public class GameLobbyWindow implements Runnable {

    private JFrame frame;
    private JTable userList;
    private JTable playerList;

    private JButton btnInvite;
    private JButton btnStart;

    private NonEditableModel userTableModel = new NonEditableModel();
    private NonEditableModel playerTableModel = new NonEditableModel();

    public static class LobbyWindowHolder {
        private static final GameLobbyWindow INSTANCE = new GameLobbyWindow();
    }

    private GameLobbyWindow() {

    }

    public static final GameLobbyWindow get() {
        return LobbyWindowHolder.INSTANCE;
    }

    void setModel() {
        userTableModel.addColumn("Id");
        userTableModel.addColumn("Name");
        userTableModel.addColumn("Status");
        playerTableModel.addColumn("Id");
        playerTableModel.addColumn("Name");
        playerTableModel.addColumn("Status");
    }

    public void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
    }

    /**
     * @wbp.parser.entryPoint
     */
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
        frame.setBounds(100, 100, 460, 420);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        userList = new JTable(userTableModel);
        userList.setBounds(20, 40, 200, 300);
        frame.getContentPane().add(userList);

        JScrollPane spUserList = new JScrollPane(userList);
        spUserList.setBounds(20, 40, 200, 300);
        spUserList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(spUserList);

        btnInvite = new JButton("Invite");
        btnInvite.setBounds(75, 350, 90, 29);
        frame.getContentPane().add(btnInvite);

        btnInvite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int[] selection = userList.getSelectedRows();
                if (selection.length < 2) {
                    showDialog("You must select more than one players.");
                }
                else {
                    String[] players = new String[selection.length];
                    //System.out.printf("%d%n", selection[0]);
                    int num = 0;
                    boolean valid = false;
                    for (int i : selection) {
                        System.out.printf("row index: %d%n", i);
                        if (userList.getValueAt(i, 0).toString().equals(GuiController.get().getId())) {
                            valid = true;
                        }
                        players[num++] = userList.getValueAt(i, 1).toString(); // include the inviter
                    }
                    if (!valid) {
                        showDialog("You must select yourself.");
                    }
                    else {
                        GuiController.get().invitePlayers(players);
                    }
                }
            }
        });

        JLabel lblNewLabel, lblNewLabel1;
        lblNewLabel = new JLabel("Online users:");
        lblNewLabel.setBounds(20, 20, 103, 16);
        frame.getContentPane().add(lblNewLabel);

        playerList = new JTable(playerTableModel);
        playerList.setBounds(240, 40, 200, 300);
        frame.getContentPane().add(playerList);

        JScrollPane spPlayerList = new JScrollPane(playerList);
        spPlayerList.setBounds(240, 40, 200, 150);
        spPlayerList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(spPlayerList);

        btnStart = new JButton("Start Game");
        btnStart.setBounds(280, 200, 120, 29);
        frame.getContentPane().add(btnStart);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (playerList.getRowCount() < 1) {
                    showDialog("You must have more than one player");
                }
                else {
                    String[] players = new String[playerList.getRowCount()];
                    for (int i = 0; i < playerList.getRowCount(); i++) {
                        players[i] = playerList.getValueAt(i, 0).toString();
                    }
                    GuiController.get().startGame(players);
                }
            }
        });

        lblNewLabel1 = new JLabel("Current Players:");
        lblNewLabel1.setBounds(240, 20, 103, 16);
        frame.getContentPane().add(lblNewLabel1);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                super.windowClosing(e);
                GuiController.get().logoutGame();;
                frame.dispose();
                System.exit(0);
            }
        });
    }

    public class NonEditableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    int getIndexInUserList(String id) {
        for (int i = 0; i < userList.getRowCount(); i++) {
            if (userList.getValueAt(i, 0).toString().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    /*
    void updateUserList(int id, String username, String status) {
        String strId = Integer.toString(id);
        int index = getIndexInUserList(strId);
        if (index == -1) {
            addToUserList(strId, username, status);
        }
        else {
            userList.setValueAt(username, index, 1);
            userList.setValueAt(status, index, 2);
        }
    }
    */

    void clearUserList() {
        int rowCount = userTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            userTableModel.removeRow(i);
        }
    }

    void updateUserList(Users[] userList) {
        clearUserList();
        for (Users user : userList){
            addToUserList(user.getUserID(), user.getUserName(), user.getStatus());
        }

    }

    void addToUserList(int id, String name, String status) {
        String strId = Integer.toString(id);
        userTableModel.addRow(new Object[]{strId, name, status});
    }

    void clearPlayerList() {
        int rowCount = playerTableModel.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            playerTableModel.removeRow(i);
        }
    }

    void updatePlayerList(Users[] playerList) {
        clearPlayerList();
        for (Users player : playerList){
            addToUserList(player.getUserID(), player.getUserName(), player.getStatus());
        }

    }

    void addToPlayerList(int id, String name, String status) {
        String strId = Integer.toString(id);
        playerTableModel.addRow(new Object[]{strId, name, status});
    }

    void showInviteMessage(int inviterId, String inviterName) {
        int confirmed = JOptionPane.showConfirmDialog(null, inviterName+" ask you to join a game, yes or no?",
                "Invite", JOptionPane.YES_NO_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            GuiController.get().sendInviteResponse(true, inviterId);
        }
        else {
            GuiController.get().sendInviteResponse(false, inviterId);
        }
    }

    void refuseInvite(int id) {
        String strId = Integer.toString(id);
        int index = getIndexInUserList(strId);
        showDialog(userList.getValueAt(index, 1) + "has refused you.");
    }
}
