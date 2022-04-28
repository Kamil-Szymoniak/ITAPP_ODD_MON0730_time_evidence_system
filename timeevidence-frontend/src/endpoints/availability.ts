import {
    AvailabilityRequest,
    AvailabilityResponse,
    BasicResponse,
} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addAvailability(values: AvailabilityRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/availability`,
        method: 'POST',
        body: JSON.stringify(values),
    });
}

export function editAvailability(availabilityId: number, timeEvidenceData: AvailabilityRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/availability/${availabilityId}`,
        method: 'PUT',
        body: JSON.stringify(timeEvidenceData),
    });
}

export function getAvailability(availabilityId: number) {
    return apiRequest<AvailabilityResponse>({
        method: 'GET',
        path: `/api/availability/${availabilityId}`,
    });
}

export function getAvailabilityInAMonth(monthIndex: number) {
    return apiRequest<any>({
        method: 'GET',
        path: `/api/availability/month/${monthIndex}`,
    });
}

export function getUserAvailabilities(date: string) {
    return apiRequest<AvailabilityResponse[]>({
        method: 'GET',
        path: `/api/availability/user/${date}`,
    });
}

export function deleteAvailability(availabilityId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/availability/${availabilityId}`,
        method: 'DELETE',
    });
}