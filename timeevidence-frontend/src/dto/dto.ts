export type TimeEvidenceStatus = 'SENT' | 'ACCEPTED' | 'REJECTED'

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
    description: string | null;
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

export interface PermissionResponse {
    id: number;
    name: string;
    description: string | null;
}

export interface PermissionRequest {
    name: string;
    description: string | null;
}

export interface RoleResponse {
    id: number;
    name: string;
    description: string | null;
    permissions: PermissionResponse[];
}

export interface RoleRequest {
    name: string;
    description: string | null;
    permissions: number[];
}

export interface PersonResponse {
    id: number;
    name: string;
    surname: string;
    phone: string;
    birthday: string;
    user: DictionaryResponse | null
}

export interface PersonRequest {
    name: string;
    surname: string;
    phone: string;
    birthday: string;
}

export interface TeamResponse {
    id: number;
    name: string;
    description: string | null;
    teamMembers: DictionaryResponse[];
    teamLeader: DictionaryResponse | null;
}

export interface TeamRequest {
    name: string;
    description: string | null;
    teamMembers: number[];
    teamLeader: number | null;
}

export interface TimeEvidenceResponse {
    id: number
    date: string
    minutes: number
    comment: string | null
    person: DictionaryResponse
    project: DictionaryResponse
    status: TimeEvidenceStatus
    statusComment: string | null
}

export interface TimeEvidenceChangeStatusRequest {
    status: TimeEvidenceStatus
    statusComment: string | null
}

export interface TimeEvidenceRequest {
    date: string
    minutes: number
    comment: string | null
    project: number
}

export interface ProjectResponse {
    id: number
    name: string
    inhouseName: string | null
    description: string | null
    clientName: string
    beginningDate: string
    projectMembers: DictionaryResponse[]
    projectManager: DictionaryResponse | null
}

export interface ProjectRequest {
    name: string
    inhouseName: string | null
    description: string | null
    clientName: string
    beginningDate: string
    projectMembers: number[]
    projectManager: number | null
}

export interface PeriodResponse {
    timeFrom: string
    timeTo: string
    minutes: number
}

export interface PeriodRequest {
    timeFrom: string
    timeTo: string
}

export interface AvailabilityResponse {
    id: number
    comment: string | null
    date: string
    person: DictionaryResponse
    team: DictionaryResponse
    periods: PeriodResponse[]
}

export interface AvailabilityRequest {
    comment: string | null
    date: string
    team: number
    periods: PeriodRequest[]
}

export interface UserResponse {
    id: number;
    username: string;
    email: string;
    person: DictionaryResponse;
    roles: RoleResponse[];
}