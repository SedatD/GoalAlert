package vavien.agency.goalalert.pojoClasses;

/**
 * Created by Sedat
 * on 3.04.2018.
 */

public class MatchDetailPojo {
    private String statsName;
    private int localValue, visitorValue;

    public MatchDetailPojo(String statsName, int localValue, int visitorValue) {
        this.statsName = statsName;
        this.localValue = localValue;
        this.visitorValue = visitorValue;
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

}
