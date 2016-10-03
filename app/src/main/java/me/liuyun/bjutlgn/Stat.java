package me.liuyun.bjutlgn;

public class Stat {
    private float flow;
    private int time;
    private float fee;

    public Stat(float flow, int time, float fee) {
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

    public float getFlow() {
        return flow;
    }

    public int getTime() {
        return time;
    }

    public float getFee() {
        return fee;
    }


    public void setFlow(float flow) {
        this.flow = flow;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

}
