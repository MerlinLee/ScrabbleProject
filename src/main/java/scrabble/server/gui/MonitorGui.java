package scrabble.server.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MonitorGui {
    private JFrame frame;
    public MonitorGui() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Server Monitor");
        JLabel status = new JLabel("Server is running....");
        status.setBounds(50, 50, 130, 16);
        frame.getContentPane().add(status);

        JButton shutdown = new JButton("shutdown");
        shutdown.setBounds(100, 92, 100, 30);
        frame.getContentPane().add(shutdown);

        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    shutdown();
                }
            }
        };
        shutdown.addKeyListener(keyListener);
        shutdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    shutdown();
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });
        this.frame.setVisible(true);
    }

    public void shutdown(){
        System.exit(0);
    }
}
