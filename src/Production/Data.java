/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Production;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Wei Wang
 */


public class Data {

    public SimpleStringProperty testCode;
    public SimpleStringProperty name;
    public SimpleStringProperty curJun;
    public SimpleStringProperty curUnit;
    public SimpleStringProperty preJun;
    public SimpleStringProperty preUnit;

    public Data(String testCode, String name, String curJun, String curUnit, String preJun, String preUnit) {
        this.testCode = new SimpleStringProperty(testCode);
        this.name = new SimpleStringProperty(name);
        this.curJun = new SimpleStringProperty(curJun);
        this.curUnit = new SimpleStringProperty(curUnit);
        this.preJun = new SimpleStringProperty(preJun);
        this.preUnit = new SimpleStringProperty(preUnit);
    }

    public String getTestCode() {
        return testCode.get();
    }

    public void setTestCode(String fName) {
        testCode.set(fName);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String fName) {
        name.set(fName);
    }

    public String getCurJun() {
        return curJun.get();
    }

    public void setCurJun(String fName) {
        curJun.set(fName);
    }

    public String getCurUnit() {
        return curUnit.get();
    }

    public void setCurUnit(String fName) {
        curUnit.set(fName);
    }

    public String getPreJun() {
        return preJun.get();
    }

    public void setPreJun(String fName) {
        preJun.set(fName);
    }

    public String getPreUnit() {
        return preUnit.get();
    }

    public void setPreUnit(String fName) {
        preUnit.set(fName);
    }
}