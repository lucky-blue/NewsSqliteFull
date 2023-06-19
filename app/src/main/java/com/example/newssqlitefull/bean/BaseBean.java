package com.example.newssqlitefull.bean;

import java.io.Serializable;

public class BaseBean <T> implements Serializable {
        private int status;
        private String msg;
        private T result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
