package com.ef.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "access_log_statement")
public class AccessLogStatement {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private Date datetime;
    private String ip;
    private String request;
    private String status;
    private String agent;

    public Integer getId() {
        return id;
    }

    public AccessLogStatement setId(Integer id) {
        this.id = id;
        return this;
    }

    public Date getDatetime() {
        return datetime;
    }

    public AccessLogStatement setDatetime(Date datetime) {
        this.datetime = datetime;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public AccessLogStatement setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getRequest() {
        return request;
    }

    public AccessLogStatement setRequest(String request) {
        this.request = request;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public AccessLogStatement setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getAgent() {
        return agent;
    }

    public AccessLogStatement setAgent(String agent) {
        this.agent = agent;
        return this;
    }

    public static AccessLogStatement getInstance(Date datetime, String ip, String request, String status, String agent) {
        return new AccessLogStatement().setDatetime(datetime).setIp(ip).setRequest(request).setStatus(status).setAgent(agent);
    }

    public static String getTableName() {
        final Table table = AccessLogStatement.class.getAnnotation(Table.class);
        return table.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessLogStatement that = (AccessLogStatement) o;

        if (!id.equals(that.id)) return false;
        if (!datetime.equals(that.datetime)) return false;
        if (!ip.equals(that.ip)) return false;
        if (!request.equals(that.request)) return false;
        if (!status.equals(that.status)) return false;
        return agent.equals(that.agent);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + datetime.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + request.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + agent.hashCode();
        return result;
    }
}
