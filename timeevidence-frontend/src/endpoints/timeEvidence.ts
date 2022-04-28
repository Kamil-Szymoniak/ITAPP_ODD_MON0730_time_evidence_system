import {
    BasicResponse, PageResponse, TimeEvidenceChangeStatusRequest,
    TimeEvidenceRequest,
    TimeEvidenceResponse
} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addTimeEvidence(values: TimeEvidenceRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/time-evidence`,
        method: 'POST',
        body: JSON.stringify(values),
    });
}

export function editTimeEvidence(timeEvidenceId: number, timeEvidenceData: TimeEvidenceRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/time-evidence/${timeEvidenceId}`,
        method: 'PUT',
        body: JSON.stringify(timeEvidenceData),
    });
}

export function editTimeEvidenceStatus(timeEvidenceId: number, statusData: TimeEvidenceChangeStatusRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/time-evidence/status/${timeEvidenceId}`,
        method: 'PUT',
        body: JSON.stringify(statusData),
    });
}

export function getTimeEvidence(timeEvidenceId: number) {
    return apiRequest<TimeEvidenceResponse>({
        method: 'GET',
        path: `/api/time-evidence/${timeEvidenceId}`,
    });
}

export function getUserTimeEvidences(query: string) {
    return apiRequest<PageResponse<TimeEvidenceResponse>>({
        method: 'GET',
        path: `/api/time-evidence/user?${query}`,
    });
}

export function getTimeEvidenceInAMonth(monthIndex: number) {
    return apiRequest<any>({
        method: 'GET',
        path: `/api/time-evidence/month/${monthIndex}`,
    });
}

export function deleteTimeEvidence(timeEvidenceId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/time-evidence/${timeEvidenceId}`,
        method: 'DELETE',
    });
}