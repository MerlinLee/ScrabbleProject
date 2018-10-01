package scrabble.client.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class TestMyMouse extends JFrame
{
    //JTextArea text = new JTextArea() ;
    public TestMyMouse()
    {
        super.setTitle("鼠标精灵") ;
        super.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseDragged(MouseEvent e)
            {
                System.out.println("x坐标:"+e.getX()+"  y坐标:"+e.getY()) ;
            }
        }) ;
        super.addWindowListener(new WindowAdapter()
        {
            public void WindowClosing(WindowEvent e)
            {
                System.exit(1) ;
            }
        }) ;

        super.setSize(400,300) ;
        super.setVisible(true) ;
    }


};

class Tester
{
    public static void main(String args[])
    {
        TestMyMouse mhk = new TestMyMouse() ;
    }
};
