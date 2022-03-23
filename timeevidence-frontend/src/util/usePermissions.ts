import {AuthContextProps, useAuthContext} from "../components/auth/AuthContext";

export const getUserPermissions = (auth: AuthContextProps) => {
    console.log(auth);
    if (auth == null || auth.user == null) return []
        if (auth.user.username && auth.user.roles.length > 0) {
            const permissions = auth.user.roles.flatMap(r => r.permissions)
            if (permissions.length > 0) {
                return permissions.map(x => x.name)
            }
        }
        return []

}

export const includesMultipleOr = (target: string | string[] | undefined, pattern: string[] | undefined): boolean => {
    if (pattern === undefined || target === undefined) {
        return false
    }
    return pattern.filter(word => target.includes(word)).length > 0
}

export const usePermissions = () => {
    const auth = useAuthContext()
    const permissions = getUserPermissions(auth)

    const canSeePermissions = includesMultipleOr(permissions, ['CAN_SEE_PERMISSIONS', 'CAN_EDIT_PERMISSIONS'])
    const canSeeRoles = includesMultipleOr(permissions, ['CAN_SEE_ROLES', 'CAN_EDIT_ROLES'])
    const canSeeUsers = includesMultipleOr(permissions, ['CAN_SEE_USERS', 'CAN_EDIT_USERS'])
    const canSeePersons = includesMultipleOr(permissions, ['CAN_SEE_PERSONS', 'CAN_EDIT_PERSONS'])
    const canSeeTeams = includesMultipleOr(permissions, ['CAN_SEE_TEAMS', 'CAN_EDIT_TEAMS'])
    const canSeeProjects = includesMultipleOr(permissions, ['CAN_SEE_PROJECTS', 'CAN_EDIT_PROJECTS'])
    const canSeeAvailability = includesMultipleOr(permissions, ['CAN_SEE_AVAILABILITY', 'CAN_EDIT_AVAILABILITY'])
    const canSeeEvidence = includesMultipleOr(permissions, ['CAN_SEE_EVIDENCE', 'CAN_EDIT_EVIDENCE'])

    const canEditPermissions = permissions ? permissions.includes('CAN_EDIT_PERMISSIONS') : false
    const canEditRoles = permissions ? permissions.includes('CAN_EDIT_ROLES') : false
    const canEditUsers = permissions ? permissions.includes('CAN_EDIT_USERS') : false
    const canEditPersons = permissions ? permissions.includes('CAN_EDIT_PERSONS') : false
    const canEditTeams = permissions ? permissions.includes('CAN_EDIT_TEAMS') : false
    const canEditProjects = permissions ? permissions.includes('CAN_EDIT_PROJECTS') : false
    const canEditAvailability = permissions ? permissions.includes('CAN_EDIT_AVAILABILITY') : false
    const canEditEvidence = permissions ? permissions.includes('CAN_EDIT_EVIDENCE') : false

    const hasAdmPermission = includesMultipleOr(permissions, [
        'CAN_SEE_PERMISSIONS',
        'CAN_EDIT_PERMISSIONS',
        'CAN_SEE_ROLES',
        'CAN_EDIT_ROLES',
        'CAN_SEE_USERS',
        'CAN_EDIT_USERS'
    ])

    return {
        canSeePermissions,
        canSeeRoles,
        canSeeUsers,
        canSeePersons,
        canSeeTeams,
        canSeeProjects,
        canSeeAvailability,
        canSeeEvidence,
        canEditPermissions,
        canEditRoles,
        canEditUsers,
        canEditPersons,
        canEditTeams,
        canEditProjects,
        canEditAvailability,
        canEditEvidence,
        hasAdmPermission
    }
}