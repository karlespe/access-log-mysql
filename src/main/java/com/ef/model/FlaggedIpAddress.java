package com.ef.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class FlaggedIpAddress {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String ip;
    private String note;

    public Integer getId() {
        return id;
    }

    public FlaggedIpAddress setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public FlaggedIpAddress setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getNote() {
        return note;
    }

    public FlaggedIpAddress setNote(String note) {
        this.note = note;
        return this;
    }

    public static FlaggedIpAddress getInstance(String ip, String note) {
        return new FlaggedIpAddress().setIp(ip).setNote(note);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlaggedIpAddress that = (FlaggedIpAddress) o;

        if (!id.equals(that.id)) return false;
        if (!ip.equals(that.ip)) return false;
        return note.equals(that.note);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + note.hashCode();
        return result;
    }

}
