import {PermissionResponse} from "../../../dto/dto";
import {useState} from "react";
import {useHistory} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {PERMISSIONS} from "../../../routeNames";
import {toast} from "react-toastify";
import {getExceptionMessage} from "../../../util/getMessage";
import DataTable from "../../shared/table/AppTable";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import ConfirmationDialog from "../../shared/ConfirmationDialog";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import {deletePermission} from "../../../endpoints/permission";
import EditIcon from '@mui/icons-material/Edit';
import {Box} from "@mui/material";


const PermissionComponent = () => {
    const [currentPermission, setCurrentPermission] = useState<PermissionResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditPermissions } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditPermissions && history.push(`${PERMISSIONS}/create`)
    const onEdit = (permission: PermissionResponse) => canEditPermissions && history.push(`${PERMISSIONS}/edit/${permission.id}`)
    const onDetails = (permission: PermissionResponse) => history.push(`${PERMISSIONS}/details/${permission.id}`)
    const onDelete = (permission: PermissionResponse) => {
        if (canEditPermissions) {
            setCurrentPermission(permission)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentPermission && canEditPermissions) {
                const response = await deletePermission(currentPermission.id)
                onReload()
                toast.success(response.message)
                setCurrentPermission(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <Box>
            <h1>Permissions</h1>
            <DataTable<PermissionResponse>
                url="/api/permissions"
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
                    }
                ]}
                actions={canEditPermissions ? [
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
                toolbar={canEditPermissions ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add permission
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete permission?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this permission?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </Box>

    )
}

export default PermissionComponent
