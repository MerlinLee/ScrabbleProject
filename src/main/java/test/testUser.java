package test;

import com.alibaba.fastjson.JSON;
import scrabble.Models.Users;

public class testUser {

    public static void main(String[] args) {

        Users temp = new Users(1,"peppaMerlin");
        System.out.println(JSON.toJSONString(temp));

    }
}
