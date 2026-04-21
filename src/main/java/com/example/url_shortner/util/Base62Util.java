package com.example.url_shortner.util;

public class Base62Util {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARSET.length();

    public static String encode(long num) {
        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            int rem = (int) (num % BASE);
            sb.append(CHARSET.charAt(rem));
            num = num / BASE;
        }

        return sb.reverse().toString();
    }
}