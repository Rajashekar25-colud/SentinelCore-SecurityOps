package com.sentinelcore.secureops.dto;
import jakarta.validation.constraints.NotBlank;
public class IncidentAssignmentRequest {
    @NotBlank private String assignedTeam; private String assignedTo;
    public String getAssignedTeam(){return assignedTeam;} public String getAssignedTo(){return assignedTo;}
    public void setAssignedTeam(String v){assignedTeam=v;} public void setAssignedTo(String v){assignedTo=v;}
}
