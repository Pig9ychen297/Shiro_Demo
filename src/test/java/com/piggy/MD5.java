package com.piggy;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;
public class MD5 {
    @Test
    public void testMd5(){
        //密码明文仅使用算法加密
        String pwd = "123456";
        Md5Hash hash = new Md5Hash(pwd);
        System.out.println(hash);

//        加密之后又进行加盐操作
        hash = new Md5Hash(pwd,"aaa111");
        System.out.println(hash);

//        md5算法加密+盐+散列次数：就是将加密的结果在重复加密3次
        hash = new Md5Hash(pwd,"abc111",3);
        System.out.println(hash);
    }
}
