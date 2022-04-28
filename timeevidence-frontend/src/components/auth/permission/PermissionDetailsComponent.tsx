import {useHistory, useParams} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {useApiRequest} from "../../../util/apiRequest";
import {
    FormControl,
    Grid,
} from "@material-ui/core";
import {Formik} from "formik";
import * as Yup from 'yup';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {inputPropsFormik} from "../../../util/inputProps";
import {Input} from "../../shared/Input";
import {PERMISSIONS} from "../../../routeNames";
import {addPermission, editPermission, getPermission} from "../../../endpoints/permission";
import {ReturnButton, SaveButton} from "../../shared/Buttons";
import {getExceptionMessage} from "../../../util/getMessage";
import {PermissionRequest} from "../../../dto/dto";


type Params = {
    permissionId?: string
    mode: string
}

type FormValues = {
    name: string;
    description: string;
}

const validation = Yup.object().shape({
    name: Yup.string().required("Name is required"),
})

const PermissionDetailsComponent = () => {
    const {permissionId, mode} = useParams<Params>()
    const history = useHistory()
    const {canSeePermissions, canEditPermissions} = usePermissions()

    const {data: permissionData, reload: reloadPermissionData} = useApiRequest(
        async () => {
            if (permissionId != null && (mode === 'edit' || mode === 'details') && canSeePermissions) {
                return getPermission(parseInt(permissionId))
            }
            return null
        }, [permissionId],
    );

    const getTitle = () => {
        switch (mode) {
            case 'create':
                return 'Create permission'
            case 'edit':
                return 'Edit permission'
            case 'details':
                return 'permission details'
            default:
                return ''
        }
    }

    const onSubmit = async (request: PermissionRequest) => {
        if (mode === 'edit' && permissionId) {
            try {
                const response = await editPermission(parseInt(permissionId!), request)
                toast.success(response.message)
                reloadPermissionData()
                history.push(PERMISSIONS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        } else {
            try {
                const response = await addPermission(request)
                toast.success(response.message)
                reloadPermissionData()
                history.push(PERMISSIONS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        }
    }

    return (
        <Formik<FormValues>
            enableReinitialize={true}
            validationSchema={validation}
            initialValues={{
                name: permissionData?.name ?? "",
                description: permissionData?.description ?? "",
            }}
            onSubmit={async (values) => {
                await onSubmit(values);
            }}
        >
            {(formik) => (
                <Grid>
                    <form onSubmit={formik.handleSubmit}>
                        <h1> {getTitle()}</h1>
                        <Grid container item xs={12}>
                            <Grid item xs={6} style={{paddingRight: '10px'}}>
                                <FormControl variant="outlined" fullWidth>
                                    <Input
                                      disabled={(mode !== 'create' && mode !== 'edit') || !canEditPermissions}
                                        label="Name"
                                        placeholder="Enter name"
                                        {...inputPropsFormik('name', formik)}
                                    />
                                    <Input
                                      disabled={(mode !== 'create' && mode !== 'edit') || !canEditPermissions}
                                        label="Description"
                                        placeholder="Enter description"
                                        {...inputPropsFormik('description', formik)}
                                    />
                                </FormControl>
                            </Grid>
                        </Grid>
                        {((mode === 'create' || mode === 'edit') && canEditPermissions) && (
                            <SaveButton disabled={formik.isSubmitting}/>
                        )}
                        <ReturnButton
                            disabled={formik.isSubmitting}
                            returnPath={PERMISSIONS}
                        />
                    </form>
                </Grid>
            )}
        </Formik>
    )
}

export default PermissionDetailsComponent
