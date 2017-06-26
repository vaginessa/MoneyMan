package com.moneyman.models;

/**
 * Created by chatRG.
 */

public class ModelItem {

    private int id;
    private String amount;
    private String desc;
    private String transaction;
    private String date;

    public ModelItem() {
    }

    public ModelItem(int id, String amount, String desc, String transaction, String date) {
        this.id = id;
        this.amount = amount;
        this.desc = desc;
        this.transaction = transaction;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
