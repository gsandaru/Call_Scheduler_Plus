package com.gihansandaru.callscheduler.models;

public class CallLogData {
    String name;
    String number;
    String callType;
    String dateTime;
    String callDuration;

    public CallLogData(String name, String number, String callType, String dateTime, String callDuration) {
        this.name = name;
        this.number = number;
        this.callType = callType;
        this.dateTime = dateTime;
        this.callDuration = callDuration;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
