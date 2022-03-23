import {BasicResponse, LoginRequest, UserRequest, UserResponse} from "../dto/dto";
import {basicRequest} from "../util/apiRequest";

export function register(request: UserRequest) {
    return basicRequest<BasicResponse>({
        method: 'POST',
        url: '/api/auth/register',
        body: JSON.stringify(request),
    });
}

export function loginUser(values: LoginRequest) {
    return basicRequest<BasicResponse>({
        url: `/api/auth/login`,
        method: 'POST',
        body: JSON.stringify(values),
    })
}

export const logout = async (): Promise<any> => {
    return basicRequest<any>({
        url: "/api/auth/logout",
        method: 'POST'
    })
};

export const getLoggedUserData = async (): Promise<UserResponse> => {
    return basicRequest<UserResponse>({
        url: "/api/users/me",
        method: 'GET'
    })
};