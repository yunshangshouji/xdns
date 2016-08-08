package zhuboss.dnsproxy.po;

import java.util.Date;
import zhuboss.framework.mybatis.mapper.AbstractPO;

public class HostsPO extends AbstractPO {
    private String ip;

    private String hosts;

    private Date mainDate;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public Date getMainDate() {
        return mainDate;
    }

    public void setMainDate(Date mainDate) {
        this.mainDate = mainDate;
    }
}