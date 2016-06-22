package com.caorenhao.util;

public class IniParserException extends Exception {

    private static final long serialVersionUID = -3169926933915559316L;

    public IniParserException(String messgae) {
        super(messgae);
    }

    public IniParserException(String messgae, Throwable t) {
        super(messgae, t);
    }
}