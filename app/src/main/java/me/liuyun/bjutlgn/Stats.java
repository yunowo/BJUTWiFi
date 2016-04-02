package me.liuyun.bjutlgn;

public class Stats {
    private int flow;
    private int time;
    private int fee;

    public Stats() {
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

    @Override
    public String toString() {
        return "Stats{" +
                "flow=" + flow +
                ", time=" + time +
                ", fee=" + fee +
                '}';
    }
}
