package scrabble.client.gui;

import scrabble.Models.Player;
import scrabble.client.Gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class PlayerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable playerList;

    private NonEditableModel playerTableModel = new NonEditableModel();

    public static class PlayerPanelHolder {
        private static final PlayerPanel INSTANCE = new PlayerPanel();
    }

    private PlayerPanel() {
        this.setSize(190, 300);
        setLayout(null);

        playerTableModel.addColumn("Id");
        playerTableModel.addColumn("Name");
        playerTableModel.addColumn("Score");

        playerList = new JTable(playerTableModel);
        playerList.setBounds(0, 0, 200, 300);
        add(playerList);

        JScrollPane spPlayerList = new JScrollPane(playerList);
        spPlayerList.setBounds(0, 0, 200, 300);
        spPlayerList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(spPlayerList);
    }

    public static final PlayerPanel get() {
        return PlayerPanelHolder.INSTANCE;
    }

    private int getIndexInPlayerList(int id) {
        String strId = Integer.toString(id);
        for (int i = 0; i < playerList.getRowCount(); i++) {
            if (playerList.getValueAt(i, 0).toString().equals(strId)) {
                return i;
            }
        }
        return -1;
    }

    String getPlayerName(int id) {
        int index = getIndexInPlayerList(id);
        return playerList.getValueAt(index, 1).toString();
    }

    private void addToPlayerList(Player player) {
        String strId = Integer.toString(player.getUser().getUserID());
        String name = player.getUser().getUserName();
        String score = Integer.toString(player.getPoints());
        playerTableModel.addRow(new Object[]{strId, name, score});
        //logic error
//        if (player.getPoints() != 0) {
//            GameWindow.get().showDialog("The vote is successful!");
//        }
    }

    public class NonEditableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    synchronized void updatePlayerList(Player[] players) {
        synchronized (playerList){
            for (Player player : players) {
                System.out.printf("name: %s seq: %d",player.getUser().getUserName(), player.getInGameSequence());
                int id = player.getUser().getUserID();
                int index = getIndexInPlayerList(id);
                if (index != -1) {
                    int lastScore = Integer.parseInt(playerList.getValueAt(index, 2).toString());
                    if (player.getPoints() != lastScore) {
//                        GameWindow.get().showDialog("The vote is successful!");
                        GuiController.gameWindow.showDialog("The vote is successful!");
                    }
                    playerList.setValueAt(Integer.toString(player.getPoints()), index, 2);
                }
                else {
                    addToPlayerList(player);
                }
            }
        }
    }
}
