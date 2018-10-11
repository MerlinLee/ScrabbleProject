package scrabble.client.gui;

import scrabble.client.clientControl.ClientControlCenter;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.JButton;

public class LoginWindow implements Runnable {
    public ClientControlCenter getCenter() {
        return center;
    }

    public void setCenter(ClientControlCenter center) {
        this.center = center;
    }

    private ClientControlCenter center;
    private JFrame frame;
    private JTextField userName;
    private JTextField ip;
    private JTextField port;

    public LoginWindow() {
    }

//    public static class LoginWindowHolder {
//        private static final LoginWindow INSTANCE = new LoginWindow();
//    }
    private static LoginWindow loginWindow;



    public static final LoginWindow get() {
        if(loginWindow==null){
            return loginWindow=new LoginWindow();
        }else {
            return loginWindow;
        }
    }


    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public void run() {
        initialize();
        this.frame.setVisible(true);
    }

    public void showDialog(String res) {
        JOptionPane.showMessageDialog(null, res);
        closeWindow();
    }

    public void closeWindow() {
        frame.dispose();
    }

    public void reInitial(){
        initialize();
        this.frame.setVisible(true);
    }
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(47, 23, 66, 16);
        frame.getContentPane().add(lblUsername);

        JLabel lblNewLabel = new JLabel("IP address:");
        lblNewLabel.setBounds(45, 62, 68, 16);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Port:");
        lblNewLabel_1.setBounds(84, 100, 29, 16);
        frame.getContentPane().add(lblNewLabel_1);

        userName = new JTextField();
        userName.setBounds(125, 18, 160, 26);
        frame.getContentPane().add(userName);
        userName.setColumns(10);

        ip = new JTextField();
        ip.setBounds(125, 57, 160, 26);
        frame.getContentPane().add(ip);
        ip.setColumns(10);

        port = new JTextField();
        port.setBounds(125, 95, 160, 26);
        frame.getContentPane().add(port);
        port.setColumns(10);

        JButton login = new JButton("Login");
        login.setBounds(130, 135, 90, 30);
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
                if (e.getKeyChar() == KeyEvent.VK_ENTER){
                    loginAction();
                }
            }
        };
        login.addKeyListener(keyListener);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    loginAction();
                }catch (Exception e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "IP or Port Number is wrong!");
                }
            }
        });
    }

    void loginAction(){
        String address = ip.getText();
        String portStr = port.getText();
        String userNameStr = userName.getText();
        center.openNet(address, Integer.parseInt(portStr), userNameStr);
        showDialog(userNameStr);
        //clientManager.openSocket(address, portStr, userNameStr);
        GuiController.get().setUserName(userNameStr);
        GuiController.get().loginGame();
    }
}
