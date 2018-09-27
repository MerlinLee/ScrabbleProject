package scrabble.server.controllers.gameEngine;

import com.alibaba.fastjson.JSON;
import scrabble.protocols.GamingProtocol.GamingOperationProtocol;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;
import scrabble.protocols.ScrabbleProtocol;

public class GameProcess {
    private char[][] board = new char[20][20];
    private static int userID;
    private static String msg;


    private volatile static GameProcess gameProcess;


    public GameProcess(){}

    //Singleton GameProcess
    public static GameProcess getInstance(){
        if (gameProcess == null ){
            synchronized (GameProcess.class){
                if (gameProcess == null){
                    gameProcess = new GameProcess();
                }
            }
        }
        return gameProcess;
    }


    public void addData(int userID, String msg){
        this.userID = userID;
    }

    private void switchOperation(String msg){
        ScrabbleProtocol temp = JSON.parseObject(msg,ScrabbleProtocol.class);
        String type  = temp.getTAG();
        switch (type){
            case "NonGamingProtocol":
                nonGamingOperation(JSON.parseObject(msg,NonGamingProtocol.class));
            case "GamingOperationProtocol":
                gamingOperation(JSON.parseObject(msg,GamingOperationProtocol.class));
            default:
                break;
        }
    }

    private void nonGamingOperation(NonGamingProtocol nonGamingProtocol){
        //command: start,login, logout, quit, invite(inviteOperation,inviteEnvelope,invitationResponse,inviteACK)

    }
    private void gamingOperation(GamingOperationProtocol gamingOperationProtocol){

    }
}
