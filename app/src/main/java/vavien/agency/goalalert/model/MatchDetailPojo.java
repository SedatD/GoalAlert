package vavien.agency.goalalert.model;

/**
 * Created by Sedat
 * on 3.04.2018.
 */

public class MatchDetailPojo {
    private String statsName;
    private int localValue, visitorValue;
    private float barValue;

    public MatchDetailPojo(String statsName, int localValue, int visitorValue, float barValue) {
        this.statsName = statsName;
        this.localValue = localValue;
        this.visitorValue = visitorValue;
        this.barValue = barValue;
    }

    public String getStatsName() {
        return statsName;
    }

    public int getLocalValue() {
        return localValue;
    }

    public int getVisitorValue() {
        return visitorValue;
    }

    public float getBarValue() {
        return barValue;
    }

}
