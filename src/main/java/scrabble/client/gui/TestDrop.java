package scrabble.client.gui;

/**
 *   @author   zakisoft.com
 */

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class TestDrop {
    private TestDrop() {
        JFrame frame = new DropTargetFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        new TestDrop();
    }
}

class DropTargetFrame extends JFrame {

    private static final long serialVersionUID = 6197629178772816152L;

    public DropTargetFrame() {
        setTitle("Swing拖拽的例子 Zakisoft.com");
        setSize(600, 400);
        setLocationRelativeTo(getOwner());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        Container contentPane = getContentPane();
        JTextArea textArea = new JTextArea(
                "请拖拽文件到该区域.\n ");

        new DropTarget(textArea, new TextDropTargetListener(textArea));
        contentPane.add(new JScrollPane(textArea), "Center");
    }
}

class TextDropTargetListener implements DropTargetListener {
    public TextDropTargetListener(JTextArea ta) {
        textArea = ta;
    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
        int a = event.getDropAction();
        if ((a & DnDConstants.ACTION_COPY) != 0)
            textArea.append("ACTION_COPY\n ");
        if ((a & DnDConstants.ACTION_MOVE) != 0)
            textArea.append("ACTION_MOVE\n ");
        if ((a & DnDConstants.ACTION_LINK) != 0)
            textArea.append("ACTION_LINK\n ");

        if (!isDragAcceptable(event)) {
            event.rejectDrag();
            return;
        }
    }

    public void dragExit(DropTargetEvent event) {
    }

    public void dragOver(DropTargetDragEvent event) {
    }

    public void dropActionChanged(DropTargetDragEvent event) {
        if (!isDragAcceptable(event)) {
            event.rejectDrag();
            return;
        }
    }

    public void drop(DropTargetDropEvent event) {
        if (!isDropAcceptable(event)) {
            event.rejectDrop();
            return;
        }

        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable transferable = event.getTransferable();

        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        for (int i = 0; i < flavors.length; i++) {
            DataFlavor d = flavors[i];
            textArea.append("MIME   type= " + d.getMimeType() + "\n ");

            try {
                if (d.equals(DataFlavor.javaFileListFlavor)) {
                    List<?> fileList = (List<?>) transferable
                            .getTransferData(d);
                    Iterator<?> iterator = fileList.iterator();
                    while (iterator.hasNext()) {
                        File f = (File) iterator.next();
                        textArea.append(f + "\n ");
                    }
                } else if (d.equals(DataFlavor.stringFlavor)) {
                    String s = (String) transferable.getTransferData(d);
                    textArea.append(s + "\n ");
                } else if (d.isMimeTypeEqual("text/plain ")) {
                    String charset = d.getParameter("charset ");
                    InputStream in = (InputStream) transferable
                            .getTransferData(d);

                    boolean more = true;
                    int ch;
                    if (charset.equals("ascii ")) {
                        do {
                            ch = in.read();
                            if (ch != 0 && ch != -1)
                                textArea.append(" " + (char) ch);
                            else
                                more = false;
                        } while (more);
                    } else if (charset.equals("unicode ")) {
                        boolean littleEndian = true;
                        do {
                            ch = in.read();
                            int ch2 = in.read();
                            if (ch != -1 && littleEndian)
                                ch = (ch & 0xFF) | ((ch2 & 0xFF) << 8);
                            if (ch == 0xFFFE)
                                littleEndian = false;
                            else if (ch != 0 && ch != -1)
                                textArea.append(" " + (char) ch);
                            else
                                more = false;
                        } while (more);
                    }

                    textArea.append("\n ");
                }
            } catch (Exception e) {
                textArea.append("Error:   " + e + "\n ");
            }
        }
        event.dropComplete(true);
    }

    public boolean isDragAcceptable(DropTargetDragEvent event) {
        return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }

    public boolean isDropAcceptable(DropTargetDropEvent event) {
        return (event.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }

    private JTextArea textArea;
}