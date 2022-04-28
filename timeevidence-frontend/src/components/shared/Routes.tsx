import * as React from 'react';
import * as routeNames from '../../routeNames';
import {Route} from 'react-router-dom';
import {usePermissions} from "../../util/usePermissions";
import {ForbiddenScreen} from "./ForbiddenScreen";
import {RouteComponentProps, withRouter} from "react-router";
import Login from "../auth/Login";
import RoleComponent from "../auth/role/RoleComponent";
import RoleDetailsComponent from "../auth/role/RoleDetailsComponent";
import PermissionComponent from "../auth/permission/PermissionComponent";
import PermissionDetailsComponent from "../auth/permission/PermissionDetailsComponent";
import UserComponent from "../auth/user/UserComponent";
import UserDetailsComponent from "../auth/user/UserDetailsComponent";
import PersonComponent from "../person/PersonComponent";
import PersonDetailsComponent from "../person/PersonDetailsComponent";
import TimeEvidenceComponent from "../time-evidence/TimeEvidenceComponent";
import ProjectComponent from "../project/ProjectComponent";
import ProjectDetailsComponent from "../project/ProjectDetailsComponent";
import TeamComponent from "../team/TeamComponent";
import TeamDetailsComponent from "../team/TeamDetailsComponent";
import TimeEvidenceManagerComponent from "../time-evidence/TimeEvidenceManagerComponent";
import TimeEvidenceDetailsComponent from "../time-evidence/TimeEvidenceDetailsComponent";
import AvailabilityComponent from "../availability/AvailabilityComponent";
import AvailabilityManagerComponent from "../availability/AvailabilityManagerComponent";
import AvailabilityDetailsComponent from "../availability/AvailabilityDetailsComponent";
import {CHANGE_PASSWORD} from "../../routeNames";
import {ChangePassword} from "../auth/ChangePasswordComponent";

const getRoute = (path: string, permission: boolean, component:  React.ComponentType<RouteComponentProps<any>> | React.ComponentType<any> | undefined) =>
    permission ? (<Route path={path} exact component={component}/>) : (
        <Route path={path} exact component={ForbiddenScreen}/>)

const Routes = () => {
    const permissions = usePermissions()
    return (
        <>
            {getRoute(routeNames.PERMISSIONS, permissions.canSeePermissions, withRouter(PermissionComponent))}
            {getRoute(`${routeNames.PERMISSIONS}/:mode/:permissionId?`, permissions.canSeePermissions, PermissionDetailsComponent)}
            {getRoute(routeNames.ROLES, permissions.canSeeRoles, RoleComponent)}
            {getRoute(`${routeNames.ROLES}/:mode/:roleId?`, permissions.canSeeRoles, RoleDetailsComponent)}
            {getRoute(routeNames.USERS, permissions.canSeeUsers, UserComponent)}
            {getRoute(`${routeNames.USERS}/:mode/:userId?`, permissions.canSeeUsers, UserDetailsComponent)}
            {getRoute(routeNames.PERSONS, permissions.canSeePersons, PersonComponent)}
            {getRoute(`${routeNames.PERSONS}/:mode/:personId?`, permissions.canSeePersons, PersonDetailsComponent)}
            {getRoute(routeNames.TEAMS, permissions.canSeeTeams, TeamComponent)}
            {getRoute(`${routeNames.TEAMS}/:mode/:teamId?/:tab?`, permissions.canSeeTeams, TeamDetailsComponent)}
            {getRoute(routeNames.PROJECTS, permissions.canSeeProjects, ProjectComponent)}
            {getRoute(`${routeNames.PROJECTS}/:mode/:projectId?/:tab?`, permissions.canSeeProjects, ProjectDetailsComponent)}
            {getRoute(routeNames.AVAILABILITY, permissions.canSeeAvailability, AvailabilityComponent)}
            {getRoute(`${routeNames.AVAILABILITY}/manager`, permissions.canSeeAvailability, AvailabilityManagerComponent)}
            {getRoute(`${routeNames.AVAILABILITY}/manager/:availabilityId`, permissions.canSeeAvailability, AvailabilityDetailsComponent)}
            {getRoute(routeNames.TIME_EVIDENCE, true, TimeEvidenceComponent)}
            {getRoute(`${routeNames.TIME_EVIDENCE}/manager`, permissions.canEditEvidence, TimeEvidenceManagerComponent)}
            {getRoute(`${routeNames.TIME_EVIDENCE}/manager/:timeEvidenceId`, permissions.canSeeEvidence, TimeEvidenceDetailsComponent)}
            {getRoute(`${routeNames.LOGIN}`, true, Login)}
            {getRoute(`${CHANGE_PASSWORD}`, true, ChangePassword)}
        </>
    )
}

export default Routes;