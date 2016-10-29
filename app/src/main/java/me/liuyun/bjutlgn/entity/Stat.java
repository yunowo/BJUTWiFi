package me.liuyun.bjutlgn.entity;

public class Stat {
    private int flow;
    private int time;
    private int fee;

    public Stat(int flow, int time, int fee) {
        this.flow = flow;
        this.time = time;
        this.fee = fee;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "flow=" + flow +
                ", time=" + time +
                ", fee=" + fee +
                '}';
    }

    public int getFlow() {
        return flow;
    }

    public int getTime() {
        return time;
    }

    public int getFee() {
        return fee;
    }


    public void setFlow(int flow) {
        this.flow = flow;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

}
