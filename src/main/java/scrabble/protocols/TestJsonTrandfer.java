package scrabble.protocols;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;
import scrabble.protocols.NonGamingProtocol.NonGamingProtocol;

public class TestJsonTrandfer {
    public static void main(String[] args){
        Users userA = new Users();
        userA.setId(1);
        userA.setNumWin(2);
        userA.setStatus("Available");
        userA.setUsername("Peppa Long");
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol();
        nonGamingProtocol.setCommand("login");
        String[] userList = new String[1];
        userList[0] = userA.getUsername();
        nonGamingProtocol.setUserList(userList);
        nonGamingProtocol.setTAG("NonGamingProtocol");
        String json = JSON.toJSONString(nonGamingProtocol);
        System.out.println(json);
        ScrabbleProtocol scrabbleProtocol = JSON.parseObject(json,ScrabbleProtocol.class);
        System.out.println(scrabbleProtocol.getTAG());
        if(scrabbleProtocol.getTAG().equals("NonGamingProtocol")){
            NonGamingProtocol test = JSON.parseObject(json,NonGamingProtocol.class);
            System.out.println(test.getUserList()[0]);
        }

    }
}
