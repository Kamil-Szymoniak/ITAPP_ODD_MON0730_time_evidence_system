import {BasicResponse, DictionaryResponse, RoleRequest, RoleResponse} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function getAllRoles() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/roles/all`,
    });
}

export function getRole(roleId: number) {
    return apiRequest<RoleResponse>({
        method: 'GET',
        path: `/api/roles/${roleId}`,
    });
}

export function addRole(values: RoleRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/roles`,
        method: 'POST',
        body: JSON.stringify(values),

    });
}

export function editRole(roleId: number, roleData: RoleRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/roles/${roleId}`,
        method: 'PUT',
        body: JSON.stringify(roleData),
    });
}

export function deleteRole(roleId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/roles/${roleId}`,
        method: 'DELETE',
    });
}