package com.ydt.lockredis.domain;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.swing.*;

@Entity
@Table(name = "goods_store")
public class GoodsStore implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private String code;

    @Column(name = "store")
    private int store;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "GoodsStore{" +
                "code='" + code + '\'' +
                ", store=" + store +
                '}';
    }
}
