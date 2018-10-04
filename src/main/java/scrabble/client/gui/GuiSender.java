package scrabble.client.gui;

import com.alibaba.fastjson.JSON;
import scrabble.client.blockingqueue.GuiGetMsg;
import scrabble.client.blockingqueue.GuiPutMsg;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.ScrabbleProtocol;

import java.io.PrintWriter;
import java.net.Socket;

public class GuiSender {

    private static GuiSender instance = null;

    private GuiSender() {

    }

    public static synchronized GuiSender get() {
        if (instance == null) {
            instance = new GuiSender();
        }
        return instance;
    }

    public void sendToCenter(ScrabbleProtocol scrabbleProtocol) {
        try {
            String json = JSON.toJSONString(scrabbleProtocol);
            System.out.println(json);
            //System.out.println("Trans before send");
            //NonGamingProtocol protocol = (NonGamingProtocol) scrabbleProtocol;
            //System.out.println(JSON.toJSONString(protocol));
            //output.println(json);
            //output.flush();
            GuiPutMsg.getInstance().putMsgToCenter(json);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
