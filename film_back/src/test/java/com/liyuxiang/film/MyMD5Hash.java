package com.liyuxiang.film;

import org.apache.shiro.crypto.hash.Md5Hash;
public class MyMD5Hash {
    public static void main(String[] args) {
        String salt = "liyuxiang799";
        String password = "password123";
        Md5Hash md5Hash = new Md5Hash(password,salt,2);
        System.out.println(md5Hash.toHex());
    }
}
