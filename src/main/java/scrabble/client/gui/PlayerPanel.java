package scrabble.client.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

    void updateScore(String id, int score) {
        for (int i = 0; i < playerList.getRowCount(); i++) {
            if (playerList.getValueAt(i, 0).toString().equals(id)) {
                int lastScore = Integer.parseInt(playerList.getValueAt(i, 2).toString());
                playerList.setValueAt(Integer.toString(lastScore+score), i, 2);
            }
        }
    }

    public class NonEditableModel extends DefaultTableModel {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
