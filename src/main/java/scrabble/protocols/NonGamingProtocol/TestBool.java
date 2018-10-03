package scrabble.protocols.NonGamingProtocol;

import com.alibaba.fastjson.JSON;

public class TestBool {
    public static void main(String[] args){
        NonGamingProtocol nonGamingProtocol = new NonGamingProtocol("inviteResponse",null);
        TestJsonBool jsonBool = new TestJsonBool();
        jsonBool.setFlag(true);
        jsonBool.setId(1);
        jsonBool.setInviteAccepted(true);
        nonGamingProtocol.setInviteAccepted(true);
        String msg = JSON.toJSONString(nonGamingProtocol);
        System.out.println(msg);
//        TestJsonBool temp = JSON.parseObject(msg,TestJsonBool.class);
        NonGamingProtocol temp = JSON.parseObject(msg,NonGamingProtocol.class);
    }
}
