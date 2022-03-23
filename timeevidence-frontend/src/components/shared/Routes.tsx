import * as React from 'react';
import * as routeNames from '../../routeNames';
import {BrowserRouter, Route} from 'react-router-dom';
import {usePermissions} from "../../util/usePermissions";
import {ForbiddenScreen} from "./ForbiddenScreen";
import {RouteComponentProps} from "react-router";
import Login from "../auth/Login";

const getRoute = (path: string, permission: boolean, component:  React.ComponentType<RouteComponentProps<any>> | React.ComponentType<any> | undefined) =>
    permission ? (<Route path={path} exact component={component}/>) : (
        <Route path={path} exact component={ForbiddenScreen}/>)

const Routes = () => {
    const permissions = usePermissions()
    return (
        <BrowserRouter>
            {getRoute(routeNames.PERMISSIONS, permissions.canSeePermissions, ForbiddenScreen)}
            {getRoute(`${routeNames.PERMISSIONS}/:id`, permissions.canSeePermissions, ForbiddenScreen)}
            {getRoute(routeNames.ROLES, permissions.canSeeRoles, ForbiddenScreen)}
            {getRoute(`${routeNames.ROLES}/:id`, permissions.canSeeRoles, ForbiddenScreen)}
            {getRoute(routeNames.USERS, permissions.canSeeUsers, ForbiddenScreen)}
            {getRoute(`${routeNames.USERS}/:id`, permissions.canSeeUsers, ForbiddenScreen)}
            {getRoute(routeNames.PERSONS, permissions.canSeePersons, ForbiddenScreen)}
            {getRoute(`${routeNames.PERSONS}/:id`, permissions.canSeePersons, ForbiddenScreen)}
            {getRoute(routeNames.TEAMS, permissions.canSeeTeams, ForbiddenScreen)}
            {getRoute(`${routeNames.TEAMS}/:id/:tab?`, permissions.canSeeTeams, ForbiddenScreen)}
            {getRoute(routeNames.PROJECTS, permissions.canSeeProjects, ForbiddenScreen)}
            {getRoute(`${routeNames.PROJECTS}/:id/:tab?`, permissions.canSeeProjects, ForbiddenScreen)}
            {getRoute(routeNames.AVAILABILITY, permissions.canSeeAvailability, ForbiddenScreen)}
            {getRoute(`${routeNames.AVAILABILITY}/:id`, permissions.canSeeAvailability, ForbiddenScreen)}
            {getRoute(`${routeNames.AVAILABILITY}/manager`, permissions.canSeeAvailability, ForbiddenScreen)}
            {getRoute(`${routeNames.AVAILABILITY}/manager/:id`, permissions.canSeeAvailability, ForbiddenScreen)}
            {getRoute(routeNames.TIME_EVIDENCE, permissions.canSeeEvidence, ForbiddenScreen)}
            {getRoute(`${routeNames.TIME_EVIDENCE}/:id`, permissions.canSeeEvidence, ForbiddenScreen)}
            {getRoute(`${routeNames.TIME_EVIDENCE}/manager`, permissions.canSeeEvidence, ForbiddenScreen)}
            {getRoute(`${routeNames.TIME_EVIDENCE}/manager/:id`, permissions.canSeeEvidence, ForbiddenScreen)}
            {getRoute(`${routeNames.LOGIN}`, true, Login)}
        </BrowserRouter>
    )
}

export default Routes;