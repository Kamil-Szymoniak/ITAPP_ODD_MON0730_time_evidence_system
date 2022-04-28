import {useHistory, useParams} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {useApiRequest} from "../../../util/apiRequest";
import {addRole, editRole, getRole} from "../../../endpoints/role";
import {
    FormControl,
    Grid,
} from "@material-ui/core";
import {Formik} from "formik";
import * as Yup from 'yup';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {inputPropsFormik, selectInputProps} from "../../../util/inputProps";
import {Input} from "../../shared/Input";
import {Dropdown} from "../../shared/Dropdown";
import {ROLES} from "../../../routeNames";
import {getAllPermissions} from "../../../endpoints/permission";
import {ReturnButton, SaveButton} from "../../shared/Buttons";
import {RoleRequest} from "../../../dto/dto";
import {getExceptionMessage} from "../../../util/getMessage";


type Params = {
    roleId?: string
    mode: string
}

type FormValues = {
    name: string;
    description: string | null;
    permissions: number[];
}

const validation = Yup.object().shape({
    name: Yup.string().required("Name is required"),
    permissions: Yup.array()
        .required("Permissions are required")
        .min(1, 'Choose at least one permission'),
})

const RoleDetailsComponent = () => {
    const {roleId, mode} = useParams<Params>()
    const history = useHistory()
    const {canSeeRoles, canEditRoles} = usePermissions()

    const {data: roleData, reload: reloadRoleData} = useApiRequest(
        async () => {
            if (roleId != null && (mode === 'edit' || mode === 'details') && canSeeRoles) {
                return getRole(parseInt(roleId))
            }
            return null
        }, [roleId],
    );
    const {data: permissions} = useApiRequest(() => getAllPermissions(), []);

    const getTitle = () => {
        switch (mode) {
            case 'create':
                return 'Create role'
            case 'edit':
                return 'Edit role'
            case 'details':
                return 'role details'
            default:
                return ''
        }
    }

    const onSubmit = async (request: RoleRequest) => {
        if (request.description === '') request.description = null
        if (mode === 'edit' && roleId) {
            try {
                const response = await editRole(parseInt(roleId!), request)
                toast.success(response.message)
                reloadRoleData()
                history.push(ROLES)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        } else {
            try {
                const response = await addRole(request)
                toast.success(response.message)
                reloadRoleData()
                history.push(ROLES)
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
                name: roleData?.name ?? "",
                description: roleData?.description ?? '',
                permissions: roleData?.permissions.map(permission => permission.id!) ?? [],
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
                                      disabled={(mode !== 'create' && mode !== 'edit') || !canEditRoles}
                                        label="Name"
                                        placeholder="Enter name"
                                        {...inputPropsFormik('name', formik)}
                                    />
                                    <Input
                                      disabled={(mode !== 'create' && mode !== 'edit') || !canEditRoles}
                                        label="Description"
                                        placeholder="Enter description"
                                        {...inputPropsFormik('description', formik)}
                                    />
                                    <Dropdown
                                      disabled={(mode !== 'create' && mode !== 'edit') || !canEditRoles}
                                        multiple
                                        label="Permissions"
                                        dropdownData={permissions}
                                        {...selectInputProps('permissions', formik)}
                                    />
                                </FormControl>
                            </Grid>
                        </Grid>
                        {((mode === 'create' || mode === 'edit') && canEditRoles) && (
                            <SaveButton disabled={formik.isSubmitting}/>
                        )}
                        <ReturnButton
                            disabled={formik.isSubmitting}
                            returnPath={ROLES}
                        />
                    </form>
                </Grid>
            )}
        </Formik>
    )
}

export default RoleDetailsComponent
