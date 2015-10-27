package nazar.cybulskij.blinkr.events;

/**
 * Created by nazar on 28.10.15.
 */
public class ReloadEvent {

    int index;


    public ReloadEvent(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



}
