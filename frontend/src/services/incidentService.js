import axiosInstance from '../api/axios.js';
const incidentService={
 getAll:()=>axiosInstance.get('/api/incidents'), getById:(id)=>axiosInstance.get(`/api/incidents/${id}`),
 create:(data)=>axiosInstance.post('/api/incidents',data), update:(id,data)=>axiosInstance.put(`/api/incidents/${id}`,data),
 delete:(id)=>axiosInstance.delete(`/api/incidents/${id}`),
 changeStatus:(id,status,resolutionNotes)=>axiosInstance.post(`/api/incidents/${id}/status`,{status,resolutionNotes}),
 assign:(id,assignedTeam,assignedTo)=>axiosInstance.post(`/api/incidents/${id}/assign`,{assignedTeam,assignedTo}),
 resolve:(id,resolutionNotes)=>axiosInstance.post(`/api/incidents/${id}/resolve`,{status:'Resolved',resolutionNotes}),
 getSla:(id)=>axiosInstance.get(`/api/incidents/${id}/sla`), getHistory:(id)=>axiosInstance.get(`/api/incidents/${id}/history`)
}; export default incidentService;
