import {RoleResponse} from "../../../dto/dto";
import {useState} from "react";
import {useHistory} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {ROLES} from "../../../routeNames";
import {deleteRole} from "../../../endpoints/role";
import {toast} from "react-toastify";
import {getExceptionMessage} from "../../../util/getMessage";
import DataTable from "../../shared/table/AppTable";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import ConfirmationDialog from "../../shared/ConfirmationDialog";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';


const RoleComponent = () => {
    const [currentRole, setCurrentRole] = useState<RoleResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditRoles } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditRoles && history.push(`${ROLES}/create`)
    const onEdit = (role: RoleResponse) => canEditRoles && history.push(`${ROLES}/edit/${role.id}`)
    const onDetails = (role: RoleResponse) => history.push(`${ROLES}/details/${role.id}`)
    const onDelete = (role: RoleResponse) => {
        if (canEditRoles) {
            setCurrentRole(role)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentRole && canEditRoles) {
                const response = await deleteRole(currentRole.id)
                onReload()
                toast.success(response.message)
                setCurrentRole(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <>
            <h1>Roles</h1>
            <DataTable<RoleResponse>
                url="/api/roles"
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
                        name: 'Permissions',
                        field: 'permissions.name',
                        sort: true,
                        filter: true,
                    }
                ]}
                actions={canEditRoles ? [
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
                toolbar={canEditRoles ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add role
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete role?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this role?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </>

    )
}

export default RoleComponent