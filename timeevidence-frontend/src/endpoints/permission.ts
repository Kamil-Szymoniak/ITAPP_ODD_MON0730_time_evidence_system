import {BasicResponse, DictionaryResponse, PermissionRequest, PermissionResponse} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function addPermission(values: PermissionRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/permissions`,
        method: 'POST',
        body: JSON.stringify(values),

    });
}

export function editPermission(permissionId: number, permissionData: PermissionRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/permissions/${permissionId}`,
        method: 'PUT',
        body: JSON.stringify(permissionData),
    });
}

export function deletePermission(permissionId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/permissions/${permissionId}`,
        method: 'DELETE',
    });
}

export function getPermission(permissionId: number) {
    return apiRequest<PermissionResponse>({
        method: 'GET',
        path: `/api/permissions/${permissionId}`,
    });
}

export function getAllPermissions() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/permissions/all`,
    });
}