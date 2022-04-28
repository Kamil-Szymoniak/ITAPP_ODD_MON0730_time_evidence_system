import {BasicResponse, LoginRequest} from "../dto/dto";
import {basicRequest} from "../util/apiRequest";

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