import {useState} from "react";
import {useHistory} from "react-router-dom";
import {toast} from "react-toastify";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import {usePermissions} from "../../util/usePermissions";
import {ProjectResponse} from "../../dto/dto";
import {PROJECTS} from "../../routeNames";
import {getExceptionMessage} from "../../util/getMessage";
import DataTable from "../shared/table/AppTable";
import ConfirmationDialog from "../shared/ConfirmationDialog";
import {deleteProject} from "../../endpoints/project";


const ProjectComponent = () => {
    const [currentProject, setCurrentProject] = useState<ProjectResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditProjects } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditProjects && history.push(`${PROJECTS}/create`)
    const onEdit = (project: ProjectResponse) => canEditProjects && history.push(`${PROJECTS}/edit/${project.id}`)
    const onDetails = (project: ProjectResponse) => history.push(`${PROJECTS}/details/${project.id}`)
    const onDelete = (project: ProjectResponse) => {
        if (canEditProjects) {
            setCurrentProject(project)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentProject && canEditProjects) {
                const response = await deleteProject(currentProject.id)
                onReload()
                toast.success(response.message)
                setCurrentProject(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <>
            <h1>Projects</h1>
            <DataTable<ProjectResponse>
                url="/api/projects"
                onClick={onDetails}
                reload={reload}
                columns={[
                    {
                        name: 'Id',
                        field: 'id',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Name',
                        field: 'name',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Inhouse name',
                        field: 'inhouseName',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Client name',
                        field: 'clientName',
                        filter: true,
                    },
                    {
                        name: 'Description',
                        field: 'description',
                        filter: true,
                    },
                    {
                        name: 'Project manager',
                        field: 'projectManager.name',
                        sort: true,
                        filter: true,
                    }
                ]}
                actions={canEditProjects ? [
                    {
                        onClick: onDelete,
                        content: 'Delete',
                        icon: <DeleteOutlineIcon style={{color: '#ff0000'}}/>,
                        danger: true,
                    },
                    {
                        onClick: onEdit,
                        content: 'Edit',
                        icon: <EditIcon style={{color: '#0000ff'}}/>,
                        danger: false,
                    },
                ] : undefined}
                toolbar={canEditProjects ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add project
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete project?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this project?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </>

    )
}

export default ProjectComponent
