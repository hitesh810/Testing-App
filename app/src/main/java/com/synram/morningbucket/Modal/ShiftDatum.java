package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShiftDatum {

    @SerializedName("shift_name")
    @Expose
    private String shiftName;

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public ShiftDatum(String shiftName) {
        this.shiftName = shiftName;
    }
}