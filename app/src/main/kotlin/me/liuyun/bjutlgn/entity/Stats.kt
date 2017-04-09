package me.liuyun.bjutlgn.entity

class Stats(var flow: Int, var time: Int, var fee: Int, var isOnline: Boolean) {

    override fun toString(): String {
        return "Stats{" +
                "flow=" + flow +
                ", time=" + time +
                ", fee=" + fee +
                ", online=" + isOnline +
                '}'
    }
}
