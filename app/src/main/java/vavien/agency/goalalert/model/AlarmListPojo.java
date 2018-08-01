package vavien.agency.goalalert.model;

/**
 * Created by SD
 * on 3.07.2018.
 */

public class AlarmListPojo {
    private int id;
    private String text;

    public AlarmListPojo(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}
