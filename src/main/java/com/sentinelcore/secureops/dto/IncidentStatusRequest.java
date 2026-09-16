package com.sentinelcore.secureops.dto;
import jakarta.validation.constraints.NotBlank;
public class IncidentStatusRequest {
    @NotBlank private String status; private String resolutionNotes;
    public String getStatus(){return status;} public String getResolutionNotes(){return resolutionNotes;}
    public void setStatus(String v){status=v;} public void setResolutionNotes(String v){resolutionNotes=v;}
}
