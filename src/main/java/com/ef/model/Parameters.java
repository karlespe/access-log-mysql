package com.ef.model;

import java.util.Date;

public class Parameters {

    private String accessLogPath;
    private Date startDate;
    private Duration duration;
    private int threshold;

    public Parameters(String accessLogPath, Date startDate, Duration duration, int threshold) {
        this.accessLogPath = accessLogPath;
        this.startDate = startDate;
        this.duration = duration;
        this.threshold = threshold;
    }

    public String getAccessLogPath() {
        return accessLogPath;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameters that = (Parameters) o;

        if (threshold != that.threshold) return false;
        if (!accessLogPath.equals(that.accessLogPath)) return false;
        if (!startDate.equals(that.startDate)) return false;
        return duration == that.duration;

    }

    @Override
    public int hashCode() {
        int result = accessLogPath.hashCode();
        result = 31 * result + startDate.hashCode();
        result = 31 * result + duration.hashCode();
        result = 31 * result + threshold;
        return result;
    }
}

