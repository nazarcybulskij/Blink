package nazar.cybulskij.blinkr.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by nazar on 26.09.15.
 */
public class Message extends RealmObject {

    @PrimaryKey
    private long id;

    private String  name;
    private String time;
    private String text;
    private int rate;

    public Message(String name) {
        this.name = name;
    }

    public Message() {
        super();
        this.name = "  ";
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }



}
