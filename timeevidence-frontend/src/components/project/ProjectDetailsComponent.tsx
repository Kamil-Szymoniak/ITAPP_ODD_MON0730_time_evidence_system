import {useHistory, useParams} from "react-router-dom";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {PersonResponse, ProjectRequest} from "../../dto/dto";
import {PROJECTS} from "../../routeNames";
import {getExceptionMessage} from "../../util/getMessage";
import {useApiRequest} from "../../util/apiRequest";
import {usePermissions} from "../../util/usePermissions";
import {Box} from "@mui/material";
import {a11yProps, StyledTab, StyledTabs, TabPanel} from "../shared/StyledTabs";
import DataTable from "../shared/table/AppTable";
import React from "react";
import {addProject, editProject, getProject} from "../../endpoints/project";
import ProjectFormik from "./ProjectFormik";


type Params = {
    projectId?: string
    mode: string
    tab?: string
}

const getTab = (tab?: string) => {
    if (tab === 'details') return 0
    if (tab === 'members') return 1
    return 0
}




const ProjectDetailsComponent = () => {
    const {projectId, mode, tab} = useParams<Params>()
    const history = useHistory()
    const {canSeeProjects, canSeePersons} = usePermissions()
    const value = getTab(tab)

    const {data: projectData, reload: reloadProjectData} = useApiRequest(
        async () => {
            if (projectId != null && (mode === 'edit' || mode === 'details') && canSeeProjects) {
                return getProject(parseInt(projectId))
            }
            return null
        }, [projectId],
    );

    const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        let tabName = 'details'
        if (newValue === 0) {
            tabName = 'details';
        }
        if (newValue === 1) {
            tabName = 'members';
        }
        history.push(`${PROJECTS}/${mode}/${projectId}/${tabName}`)
    };


    const onSubmit = async (request: ProjectRequest) => {
        if (mode === 'edit' && projectId) {
            try {
                const response = await editProject(parseInt(projectId!), request)
                toast.success(response.message)
                reloadProjectData()
                history.push(PROJECTS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        } else {
            try {
                const response = await addProject(request)
                toast.success(response.message)
                reloadProjectData()
                history.push(PROJECTS)
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
                    <StyledTab label="Project details" {...a11yProps(0)} />
                    <StyledTab disabled={mode !== 'details'} label="Project members" {...a11yProps(1)} />
                </StyledTabs>
            </Box>
            <TabPanel value={value} index={0}>
                <ProjectFormik onSubmit={onSubmit} projectData={projectData} mode={mode}/>
            </TabPanel>
            {(mode === 'details') && canSeePersons && (
                <TabPanel value={value} index={1}>
                    <h1>Persons</h1>
                    <DataTable<PersonResponse>
                        url={`/api/persons/project/${projectId}`}
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

export default ProjectDetailsComponent
