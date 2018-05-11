package vavien.agency.goalalert.MatchDetail;

/**
 * Created by SD
 * on 10.05.2018.
 */

public class KadroPojo {
    private String pos, number, name;

    public KadroPojo(String pos, String number, String name) {
        this.pos = pos;
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

}
