import {
    BasicResponse, ChangePasswordRequest,
    DictionaryResponse,
    UserRequest,
    UserResponse
} from "../dto/dto";
import {apiRequest, basicRequest} from "../util/apiRequest";

export function getAllUsers() {
    return apiRequest<DictionaryResponse[]>({
        method: 'GET',
        path: `/api/users/all`,
    });
}

export function getUser(userId: number) {
    return apiRequest<UserResponse>({
        method: 'GET',
        path: `/api/users/${userId}`,
    });
}

export function editUser(userId: number, userData: UserRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/users/${userId}`,
        method: 'PUT',
        body: JSON.stringify(userData),
    });
}

export function deleteUser(userId: number) {
    return basicRequest<BasicResponse>({
        url: `/api/users/${userId}`,
        method: 'DELETE',
    });
}

export function register(request: UserRequest) {
    return basicRequest<BasicResponse>({
        method: 'POST',
        url: '/api/users/register',
        body: JSON.stringify(request),
    });
}

export const getLoggedUserData = async (): Promise<UserResponse> => {
    return basicRequest<UserResponse>({
        url: "/api/users/me",
        method: 'GET'
    })
};

export function editMyPassword(request: ChangePasswordRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/users/me/password`,
        method: 'PUT',
        body: JSON.stringify(request),
    });
}