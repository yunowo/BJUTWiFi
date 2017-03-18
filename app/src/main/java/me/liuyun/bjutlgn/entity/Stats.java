package me.liuyun.bjutlgn.entity;

public class Stats {
    private int flow;
    private int time;
    private int fee;
    private boolean online;

    public Stats(int flow, int time, int fee, boolean online) {
        this.flow = flow;
        this.time = time;
        this.fee = fee;
        this.online = online;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "flow=" + flow +
                ", time=" + time +
                ", fee=" + fee +
                ", online=" + online +
                '}';
    }
}
