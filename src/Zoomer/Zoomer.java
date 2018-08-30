/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Zoomer;

import java.util.Map;

/**
 *
 * @author Wei Wang
 */
class ZommerUnit{
    public final String pillarID, julienBarcode , testCode , preJulienBarcode , name;
    public final float curUnit , preUnit ,cf;
    public ZommerUnit(String pillarID ,String julienBarcode ,String testCode ,float curUnit ,
                    float preUnit ,float cf,String preJulienBarcode ,String name){
        this.pillarID = pillarID;
        this.julienBarcode = julienBarcode;
        this.testCode = testCode;
        this.curUnit = curUnit;
        this.preUnit = preUnit;
        this.cf = cf;
        this.preJulienBarcode = preJulienBarcode;
        this.name = name;
    }
}
public abstract class Zoomer {
    protected Map<String ,Map<String ,ZommerUnit>> ouputUnit;
    protected String[] testCode;
    protected String pillarID;
    public Zoomer(String pillarID){
        this.pillarID = pillarID;
    }
    public abstract void readRawData();
    
    public abstract void trasfertoUnit();
    
    public abstract void getDupUnit();
    
    public abstract Map<String ,Map<String ,ZommerUnit>> combineUnit();

}
