import {UserResponse} from "../../../dto/dto";
import {useState} from "react";
import {useHistory} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {USERS} from "../../../routeNames";
import {toast} from "react-toastify";
import {getExceptionMessage} from "../../../util/getMessage";
import DataTable from "../../shared/table/AppTable";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import ConfirmationDialog from "../../shared/ConfirmationDialog";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import {deleteUser} from "../../../endpoints/user";


const UserComponent = () => {
    const [currentUser, setCurrentUser] = useState<UserResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditUsers } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditUsers && history.push(`${USERS}/create`)
    const onEdit = (user: UserResponse) => canEditUsers && history.push(`${USERS}/edit/${user.id}`)
    const onDetails = (user: UserResponse) => history.push(`${USERS}/details/${user.id}`)
    const onDelete = (user: UserResponse) => {
        if (canEditUsers) {
            setCurrentUser(user)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentUser && canEditUsers) {
                const response = await deleteUser(currentUser.id)
                onReload()
                toast.success(response.message)
                setCurrentUser(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <>
            <h1>Users</h1>
            <DataTable<UserResponse>
                url="/api/users"
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
                        name: 'Username',
                        field: 'username',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Email',
                        field: 'email',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Person',
                        field: 'person.name',
                        sort: true,
                        filter: true,
                    },
                    {
                        name: 'Roles',
                        field: 'roles.name',
                        sort: true,
                        filter: true,
                    }
                ]}
                actions={canEditUsers ? [
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
                toolbar={canEditUsers ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add user
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete user?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this user?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </>

    )
}

export default UserComponent