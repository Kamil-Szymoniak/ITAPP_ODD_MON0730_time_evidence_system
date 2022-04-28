import {Formik} from "formik";
import {FormControl, Grid} from "@material-ui/core";
import * as Yup from "yup";
import {toast} from "react-toastify";
import {getExceptionMessage} from "../../util/getMessage";
import {editMyPassword} from "../../endpoints/user";
import {Input} from "../shared/Input";
import {inputPropsFormik} from "../../util/inputProps";
import {SaveButton} from "../shared/Buttons";

toast.configure();

const validation = Yup.object().shape({
  oldPassword: Yup.string().required("Old password is required"),
  newPassword: Yup.string().required("New password is required"),
  repeatedNewPassword: Yup.string()
    .required("Repeat new password")
    .when('newPassword', (newPassword) => {
      if (newPassword) {
        return Yup.string().matches(new RegExp(`^${newPassword}$`))
      }
      return Yup.string()
    })
});

type FormValues = {
  oldPassword: string;
  newPassword: string;
  repeatedNewPassword: string;
};

export const ChangePassword = () =>
  (
    <Formik<FormValues>
      enableReinitialize={true}
      validationSchema={validation}
      initialValues={{
        oldPassword: '',
        newPassword: '',
        repeatedNewPassword: ''
      }}
      onSubmit={async (values) => {
        try {
          const response = await editMyPassword({oldPassword: values.oldPassword, newPassword: values.newPassword});
          toast.success(response.message);
        } catch (e) {
          const error = await getExceptionMessage(e);
          toast.error(error);
        }
      }}
    >
      {(formik) => (
        <Grid>
          <form onSubmit={formik.handleSubmit}>
            <h1>Change password</h1>
            <Grid container item xs={12}>
              <Grid item xs={6} style={{paddingRight: '10px'}}>
                <FormControl variant="outlined" fullWidth>
                  <Input
                    label="Old password"
                    placeholder="Enter old password"
                    type="password"
                    {...inputPropsFormik('oldPassword', formik)}
                  />
                  <Input
                    label="New password"
                    placeholder="Enter new password"
                    type="password"
                    {...inputPropsFormik('newPassword', formik)}
                  />
                  <Input
                    label="Repeat new password"
                    placeholder="Repeat new password"
                    type="password"
                    {...inputPropsFormik('repeatedNewPassword', formik)}
                  />
                </FormControl>
                <SaveButton disabled={formik.isSubmitting}/>
              </Grid>
            </Grid>
          </form>
        </Grid>
      )}
    </Formik>
  )