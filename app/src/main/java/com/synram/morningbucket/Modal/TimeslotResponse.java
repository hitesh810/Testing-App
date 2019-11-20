package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeslotResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("time_slot_data")
    @Expose
    private List<TimeSlotDatum> timeSlotData = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<TimeSlotDatum> getTimeSlotData() {
        return timeSlotData;
    }

    public void setTimeSlotData(List<TimeSlotDatum> timeSlotData) {
        this.timeSlotData = timeSlotData;
    }


}
