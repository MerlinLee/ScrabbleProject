package scrabble.server.gui;

import scrabble.server.controllers.controlcenter.ControlCenter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ServerGui {
    private JFrame frame;
    private JTextField userName;
    private JTextField ip;
    private JTextField port;

    public ServerGui() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 300, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Server GUI");
        JLabel lblNewLabel_1 = new JLabel("Port:");
        lblNewLabel_1.setBounds(50, 50, 29, 16);
        frame.getContentPane().add(lblNewLabel_1);

        port = new JTextField();
        port.setBounds(82, 50, 160, 26);
        frame.getContentPane().add(port);
        port.setColumns(10);

        JButton login = new JButton("start");
        login.setBounds(100, 92, 90, 30);
        frame.getContentPane().add(login);

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
                    startServer();
                }
            }
        };
        login.addKeyListener(keyListener);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    startServer();
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });
        this.frame.setVisible(true);
    }

    private void startServer(){
        try {
            String portStr = port.getText();
            if(portStr.equals("")){
                new Thread(new ControlCenter()).start();
            }else {
                int portNum = Integer.parseInt(port.getText());
                new Thread(new ControlCenter(portNum)).start();
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Wrong Port Number!");
            System.err.println(e.getMessage());
        }
    }

    public void closeWindow() {
        frame.dispose();
    }
}
