import {useState} from "react";
import {useHistory} from "react-router-dom";
import {toast} from "react-toastify";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import {usePermissions} from "../../util/usePermissions";
import {TeamResponse} from "../../dto/dto";
import {TEAMS} from "../../routeNames";
import {getExceptionMessage} from "../../util/getMessage";
import DataTable from "../shared/table/AppTable";
import ConfirmationDialog from "../shared/ConfirmationDialog";
import {deleteTeam} from "../../endpoints/team";


const TeamComponent = () => {
    const [currentTeam, setCurrentTeam] = useState<TeamResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditTeams } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditTeams && history.push(`${TEAMS}/create`)
    const onEdit = (team: TeamResponse) => canEditTeams && history.push(`${TEAMS}/edit/${team.id}`)
    const onDetails = (team: TeamResponse) => history.push(`${TEAMS}/details/${team.id}`)
    const onDelete = (team: TeamResponse) => {
        if (canEditTeams) {
            setCurrentTeam(team)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentTeam && canEditTeams) {
                const response = await deleteTeam(currentTeam.id)
                onReload()
                toast.success(response.message)
                setCurrentTeam(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <>
            <h1>Teams</h1>
            <DataTable<TeamResponse>
                url="/api/teams"
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
                        name: 'Description',
                        field: 'description',
                        filter: true,
                    },
                    {
                        name: 'Team leader',
                        field: 'teamLeader.name',
                        sort: true,
                        filter: true,
                    }
                ]}
                actions={canEditTeams ? [
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
                toolbar={canEditTeams ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add team
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete team?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this team?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </>

    )
}

export default TeamComponent
