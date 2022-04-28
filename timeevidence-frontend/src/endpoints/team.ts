import {
    BasicResponse,
    DictionaryResponse,
    TeamRequest,
    TeamResponse
} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addTeam(values: TeamRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/teams`,
        method: 'POST',
        body: JSON.stringify(values),

    });
}

export function editTeam(teamId: number, teamData: TeamRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/teams/${teamId}`,
        method: 'PUT',
        body: JSON.stringify(teamData),
    });
}

export function deleteTeam(teamId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/teams/${teamId}`,
        method: 'DELETE',
    });
}

export function getTeam(teamId: number) {
    return apiRequest<TeamResponse>({
        method: 'GET',
        path: `/api/teams/${teamId}`,
    });
}

export function getAllTeams() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/teams/all`,
    });
}

export function getAllUserTeams() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/teams/me`,
    });
}