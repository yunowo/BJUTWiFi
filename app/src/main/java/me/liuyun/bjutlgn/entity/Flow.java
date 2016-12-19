package me.liuyun.bjutlgn.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "flow")
public class Flow {
    @DatabaseField(generatedId = true) int id;
    @DatabaseField long timestamp;
    @DatabaseField int flow;

    public Flow() {
    }

    public Flow(int id, long timestamp, int flow) {
        this.id = id;
        this.timestamp = timestamp;
        this.flow = flow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }
}
