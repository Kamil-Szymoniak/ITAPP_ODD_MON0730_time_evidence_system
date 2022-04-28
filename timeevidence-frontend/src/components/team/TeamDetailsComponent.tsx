import {useHistory, useParams} from "react-router-dom";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {PersonResponse, TeamRequest} from "../../dto/dto";
import {TEAMS} from "../../routeNames";
import {getExceptionMessage} from "../../util/getMessage";
import {useApiRequest} from "../../util/apiRequest";
import {usePermissions} from "../../util/usePermissions";
import {Box} from "@mui/material";
import {a11yProps, StyledTab, StyledTabs, TabPanel} from "../shared/StyledTabs";
import DataTable from "../shared/table/AppTable";
import React from "react";
import TeamFormik from "./TeamFormik";
import {addTeam, editTeam, getTeam} from "../../endpoints/team";


type Params = {
    teamId?: string
    mode: string
    tab?: string
}

const getTab = (tab?: string) => {
    if (tab === 'details') return 0
    if (tab === 'members') return 1
    return 0
}




const TeamDetailsComponent = () => {
    const {teamId, mode, tab} = useParams<Params>()
    const history = useHistory()
    const {canSeeTeams, canSeePersons} = usePermissions()
    const value = getTab(tab)

    const {data: teamData, reload: reloadTeamData} = useApiRequest(
        async () => {
            if (teamId != null && (mode === 'edit' || mode === 'details') && canSeeTeams) {
                return getTeam(parseInt(teamId))
            }
            return null
        }, [teamId],
    );

    const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        let tabName = 'details'
        if (newValue === 0) {
            tabName = 'details';
        }
        if (newValue === 1) {
            tabName = 'members';
        }
        history.push(`${TEAMS}/${mode}/${teamId}/${tabName}`)
    };


    const onSubmit = async (request: TeamRequest) => {
        if (mode === 'edit' && teamId) {
            try {
                const response = await editTeam(parseInt(teamId!), request)
                toast.success(response.message)
                reloadTeamData()
                history.push(TEAMS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        } else {
            try {
                const response = await addTeam(request)
                toast.success(response.message)
                reloadTeamData()
                history.push(TEAMS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        }
    }

    return (
        <Box sx={{width: '100%'}}>
            <Box>
                <StyledTabs value={value} onChange={handleChange}>
                    <StyledTab label="Team details" {...a11yProps(0)} />
                    <StyledTab disabled={mode !== 'details'} label="Team members" {...a11yProps(1)} />
                </StyledTabs>
            </Box>
            <TabPanel value={value} index={0}>
                <TeamFormik onSubmit={onSubmit} teamData={teamData} mode={mode}/>
            </TabPanel>
            {(mode === 'details') && canSeePersons && (
                <TabPanel value={value} index={1}>
                    <h1>Persons</h1>
                    <DataTable<PersonResponse>
                        url={`/api/persons/teams/${teamId}`}
                        columns={[
                            {
                                name: 'Name',
                                field: 'name',
                                sort: true,
                                filter: true,
                            },
                            {
                                name: 'Surname',
                                field: 'surname',
                                sort: true,
                                filter: true,
                            },
                            {
                                name: 'phone',
                                field: 'phone',
                                sort: true,
                                filter: true,
                            },
                            {
                                name: 'user',
                                field: 'user.name',
                                sort: true,
                                filter: true,
                            }
                        ]}
                    />
                </TabPanel>)}
        </Box>

    )
}

export default TeamDetailsComponent
