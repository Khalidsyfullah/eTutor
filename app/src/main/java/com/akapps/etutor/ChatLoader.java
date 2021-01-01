package com.akapps.etutor;

public class ChatLoader implements Comparable{
    private int value;
    private String msg;

    public ChatLoader(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public int getValue() {
        return value;
    }

    public String getMsg() {
        return msg;
    }


    @Override
    public int compareTo(Object o) {
        int val = ((ChatLoader)o).getValue();
        return this.value-val;
    }
}
