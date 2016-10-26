/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zmluvy.crawler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Queue;

/**
 *
 * @author janmu
 */
@JsonAutoDetect
public class ZmluvyJson {

    private Zmluva[] zmluvy;

    public ZmluvyJson() {

    }

    public void setZmluvy(Zmluva[] zmluvy) {
        this.zmluvy = zmluvy;
    }

    public ZmluvyJson(Queue<Zmluva> zmluvy) {
        this.zmluvy = zmluvy.toArray(new Zmluva[zmluvy.size()]);
    }

    public Zmluva[] getZmluvy() {
        return zmluvy;
    }

}
