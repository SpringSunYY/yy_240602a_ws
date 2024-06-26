package com.yy;

import org.junit.jupiter.api.Test;

/**
 * @Project: WS
 * @Package: com.yy
 * @Author: YY
 * @CreateTime: 2024-06-13  20:59
 * @Description: StringTest
 * @Version: 1.0
 */
public class StringTest {

    @Test
    public void splitString() {
        String s= "5513996845206\n" +
                "5538998355270\n" +
                "5512988774581\n" +
                "5516988073190";

        String[] split = s.split("\n");
        for (String string : split) {
            System.out.println("string = " + string);
        }
        System.out.println("split = " + split.toString());
    }

    @Test
    public void subtract(){
        Long a= 2L,b=7L;
        Float af= Float.parseFloat(a.toString());
        Float bf= Float.parseFloat(b.toString());
        Float res=af/bf;
        System.out.println("res = " + res);
        int result= (int) (111*res);
        System.out.println("res = " + result);
        Float ress=111*res;

        System.out.println("ress = " + ress);
    }
}
