import {useState} from "react";
import {useHistory} from "react-router-dom";
import {toast} from "react-toastify";
import Button from "@mui/material/Button";
import {Add} from "@material-ui/icons";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditIcon from '@mui/icons-material/Edit';
import {usePermissions} from "../../util/usePermissions";
import {PersonResponse} from "../../dto/dto";
import {PERSONS} from "../../routeNames";
import {deletePerson} from "../../endpoints/person";
import {getExceptionMessage} from "../../util/getMessage";
import DataTable from "../shared/table/AppTable";
import ConfirmationDialog from "../shared/ConfirmationDialog";


const PersonComponent = () => {
    const [currentPerson, setCurrentPerson] = useState<PersonResponse | null>(null)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const { canEditPersons } = usePermissions()

    const onReload = () => setReload(reload + 1)
    const onCreate = () => canEditPersons && history.push(`${PERSONS}/create`)
    const onEdit = (person: PersonResponse) => canEditPersons && history.push(`${PERSONS}/edit/${person.id}`)
    const onDetails = (person: PersonResponse) => history.push(`${PERSONS}/details/${person.id}`)
    const onDelete = (person: PersonResponse) => {
        if (canEditPersons) {
            setCurrentPerson(person)
            setDeleteDialogOpen(true)
        }
    }
    const onConfirmDelete = async () => {
        try {
            if (currentPerson && canEditPersons) {
                const response = await deletePerson(currentPerson.id)
                onReload()
                toast.success(response.message)
                setCurrentPerson(null)
                setDeleteDialogOpen(false)
            }
        } catch (e) {
            const error = await getExceptionMessage(e);
            toast.error(error);
        }
    }

    return (
        <>
            <h1>Persons</h1>
            <DataTable<PersonResponse>
                url="/api/persons"
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
                        name: 'birthday',
                        field: 'birthday',
                        type: 'date',
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
                actions={canEditPersons ? [
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
                toolbar={canEditPersons ? (
                    <Button
                        startIcon={<Add/>}
                        variant="outlined"
                        onClick={onCreate}
                    >Add person
                    </Button>
                ) : undefined}
            />
            <ConfirmationDialog
                dialogTitle="Delete person?"
                confirmText="Delete"
                cancelText="Cancel"
                dialogContent="Are you sure to delete this person?"
                isDialogOpen={deleteDialogOpen}
                onConfirm={onConfirmDelete}
                onHide={() => setDeleteDialogOpen(false)}
                onCancel={() => setDeleteDialogOpen(false)}
            />
        </>

    )
}

export default PersonComponent
