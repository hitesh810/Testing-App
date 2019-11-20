package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShiftResponse {
    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("shift_data")
    @Expose
    private List<ShiftDatum> shiftData = null;

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ShiftDatum> getShiftData() {
        return shiftData;
    }

    public void setShiftData(List<ShiftDatum> shiftData) {
        this.shiftData = shiftData;
    }
}
