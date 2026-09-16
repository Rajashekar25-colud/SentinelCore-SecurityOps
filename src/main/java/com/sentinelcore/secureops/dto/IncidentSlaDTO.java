package com.sentinelcore.secureops.dto;
import java.time.LocalDateTime;
public class IncidentSlaDTO {
    private String status; private Integer slaHours; private LocalDateTime dueAt; private long remainingMinutes; private boolean breached;
    public IncidentSlaDTO() {}
    public IncidentSlaDTO(String status,Integer slaHours,LocalDateTime dueAt,long remainingMinutes,boolean breached){this.status=status;this.slaHours=slaHours;this.dueAt=dueAt;this.remainingMinutes=remainingMinutes;this.breached=breached;}
    public String getStatus(){return status;} public Integer getSlaHours(){return slaHours;} public LocalDateTime getDueAt(){return dueAt;} public long getRemainingMinutes(){return remainingMinutes;} public boolean isBreached(){return breached;}
    public void setStatus(String v){status=v;} public void setSlaHours(Integer v){slaHours=v;} public void setDueAt(LocalDateTime v){dueAt=v;} public void setRemainingMinutes(long v){remainingMinutes=v;} public void setBreached(boolean v){breached=v;}
}
