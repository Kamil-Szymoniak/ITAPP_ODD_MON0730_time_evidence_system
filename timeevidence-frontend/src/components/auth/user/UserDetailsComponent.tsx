import {useHistory, useParams} from "react-router-dom";
import {usePermissions} from "../../../util/usePermissions";
import {useApiRequest} from "../../../util/apiRequest";
import {getAllRoles} from "../../../endpoints/role";
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
import {USERS} from "../../../routeNames";
import {ReturnButton, SaveButton} from "../../shared/Buttons";
import {UserRequest} from "../../../dto/dto";
import {getExceptionMessage} from "../../../util/getMessage";
import {editUser, getUser} from "../../../endpoints/user";
import {register} from "../../../endpoints/user";
import {getAllPersons} from "../../../endpoints/person";


type Params = {
  userId?: string
  mode: string
}

type FormValues = {
  username: string;
  email: string;
  password: string;
  person: number | null;
  roles: number[];
}

const validation = Yup.object().shape({
  username: Yup.string().required("Username is required"),
  email: Yup.string().required("Email is required").email("Must be a well formed email"),
  password: Yup.string().required("Name is required"),
  roles: Yup.array()
    .required("Roles are required"),
})

const UserDetailsComponent = () => {
  const {userId, mode} = useParams<Params>()
  const history = useHistory()
  const {canSeeUsers, canEditUsers} = usePermissions()
  
  const {data: userData, reload: reloadUserData} = useApiRequest(
    async () => {
      if (userId != null && (mode === 'edit' || mode === 'details') && canSeeUsers) {
        return getUser(parseInt(userId))
      }
      return null
    }, [userId],
  );
  const {data: roles} = useApiRequest(() => getAllRoles(), []);
  const {data: persons} = useApiRequest(() => getAllPersons(), []);
  
  const getTitle = () => {
    switch (mode) {
      case 'create':
        return 'Create user'
      case 'edit':
        return 'Edit user'
      case 'details':
        return 'user details'
      default:
        return ''
    }
  }
  
  const onSubmit = async (request: UserRequest) => {
    if (mode === 'edit' && userId) {
      try {
        const response = await editUser(parseInt(userId!), request)
        toast.success(response.message)
        reloadUserData()
        history.push(USERS)
      } catch (e) {
        const error = await getExceptionMessage(e)
        toast.error(error)
      }
    } else {
      try {
        const response = await register(request)
        toast.success(response.message)
        reloadUserData()
        history.push(USERS)
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
        username: userData?.username ?? "",
        email: userData?.email ?? "",
        password: "",
        person: userData?.person?.id ?? null,
        roles: userData?.roles.map(role => role.id) ?? [],
      }}
      onSubmit={() => {}}
    >
      {(formik) => (
        <Grid>
          <form onSubmit={formik.handleSubmit}>
            <h1> {getTitle()}</h1>
            <Grid container item xs={12}>
              <Grid item xs={6} style={{paddingRight: '10px'}}>
                <FormControl variant="outlined" fullWidth>
                  <Input
                    disabled={(mode !== 'create' && mode !== 'edit') || !canEditUsers}
                    label="Username"
                    placeholder="Enter username"
                    {...inputPropsFormik('username', formik)}
                  />
                  <Input
                    disabled={(mode !== 'create' && mode !== 'edit') || !canEditUsers}
                    label="Email"
                    placeholder="Enter email"
                    {...inputPropsFormik('email', formik)}
                  />
                  {mode === 'create' && canEditUsers ? (<>
                    <Input
                      label="Password"
                      placeholder="Enter temp password"
                      {...inputPropsFormik('password', formik)}
                    />
                  </>) : (<div/>)}
                  <Dropdown
                    disabled={(mode !== 'create' && mode !== 'edit') || !canEditUsers}
                    label="Person"
                    dropdownData={persons}
                    {...selectInputProps('person', formik)}
                    value={(formik.values['person'] as any) ?? ''}
                  />
                  <Dropdown
                    disabled={(mode !== 'create' && mode !== 'edit') || !canEditUsers}
                    multiple
                    label="Permissions"
                    dropdownData={roles}
                    {...selectInputProps('roles', formik)}
                    value={(formik.values['roles'] as any) ?? []}
                  />
                </FormControl>
              </Grid>
            </Grid>
            {((mode === 'create' || mode === 'edit') && canEditUsers) && (
              <SaveButton disabled={formik.isSubmitting} onClick={() => onSubmit(formik.values)}/>
            )}
            <ReturnButton
              disabled={formik.isSubmitting}
              returnPath={USERS}
            />
          </form>
        </Grid>
      )}
    </Formik>
  )
}

export default UserDetailsComponent
