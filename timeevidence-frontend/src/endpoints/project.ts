import {
    BasicResponse,
    DictionaryResponse,
    ProjectRequest,
    ProjectResponse,
} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addProject(values: ProjectRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/projects`,
        method: 'POST',
        body: JSON.stringify(values),

    });
}

export function editProject(projectId: number, projectData: ProjectRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/projects/${projectId}`,
        method: 'PUT',
        body: JSON.stringify(projectData),
    });
}

export function deleteProject(projectId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/projects/${projectId}`,
        method: 'DELETE',
    });
}

export function getProject(projectId: number) {
    return apiRequest<ProjectResponse>({
        method: 'GET',
        path: `/api/projects/${projectId}`,
    });
}

export function getAllProjects() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/projects/all`,
    });
}

export function getAllUserProjects() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/projects/me`,
    });
}