package com.ajay.nlp_voice_todo_list.common;

import java.util.Arrays;
import java.util.HashSet;

public class StringConstants {

    public static final String PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvcljcDWwTyHhwjRzVRlo\n" +
            "Se4tL+DcN3Tjz/cwjRS9Y3353PBBqQtwWfQeDd0niuXqYVoQ4ZiSMD/wo5yDVwRB\n" +
            "lfW+XNI5ePXeYlHSH8r7uELaZoGMJNxXiwVvyY6lvYxkPo9go79AoXXQXebCzPV/\n" +
            "S6CbeF2DE590h4+XSQqHleHAP25UhpwuJdwaDFcWs5o+BQTT6sDClJVWx/EbgoR+\n" +
            "nYVXVBShR1xZzZA9mDWztl9iRa0VENKSP5l0tx6oe98MRp8bmH+SVQPHBhX4kjcn\n" +
            "qn1DjBpVnNLaHo6lgDAsHrm0+ThfDFYCQ8UIvGKFoRPo8+fHz96XEzbq5wAZ+vVc\n" +
            "MQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    public static String openAiToken1 = "";
    public static String openAiToken2 = "";
    public static String openAiToken3 = "";

    public static final String openAiToken = openAiToken1 + openAiToken2 + openAiToken3;

    public static final HashSet<String> stopWordsList = new HashSet<>(Arrays.asList("for", "an"));

    public static final HashSet<String> protectedTermsList = new HashSet<>();

}
