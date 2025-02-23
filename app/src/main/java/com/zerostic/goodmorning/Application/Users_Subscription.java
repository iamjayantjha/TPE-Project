package com.zerostic.goodmorning.Application;

public class Users_Subscription {
    private String subscription;
    private String payDate;
    private String nextPayDate;
    private String currency;

    public Users_Subscription() {
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getNextPayDate() {
        return nextPayDate;
    }

    public void setNextPayDate(String nextPayDate) {
        this.nextPayDate = nextPayDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
