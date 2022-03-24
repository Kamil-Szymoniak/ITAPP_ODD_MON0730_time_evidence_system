export interface BasicResponse {
    success: boolean;
    message: string | null;
    dto: Object | null;
}

export interface PageResponse<T> {
    items: T[];
    totalElements: number;
}

export interface DictionaryResponse {
    id: number;
    name: string;
    description: string | undefined;
}

export interface UserRequest {
    username: string;
    email: string;
    password: string;
    roles: number[];
    person: number | null;
}

export interface LoginRequest {
    login: string;
    password: string;
}

export interface ChangePasswordRequest {
    oldPassword: string;
    newPassword: string;
}

export interface RoleResponse {
    id: number;
    name: string;
    permissions: DictionaryResponse[];
}

export interface UserResponse {
    id: number;
    username: String;
    email: String;
    person: DictionaryResponse;
    roles: RoleResponse[];
}