import {useHistory, useParams} from "react-router-dom";
import {
    FormControl,
    Grid,
} from "@material-ui/core";
import {Formik} from "formik";
import * as Yup from 'yup';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {CustomDatePicker} from "../shared/DatePicker";
import {datePickerErrorProps, datePickerProps, inputPropsFormik} from "../../util/inputProps";
import {addPerson, editPerson, getPerson} from "../../endpoints/person";
import {PersonRequest} from "../../dto/dto";
import {PERSONS} from "../../routeNames";
import {Input} from "../shared/Input";
import {ReturnButton, SaveButton} from "../shared/Buttons";
import {getExceptionMessage} from "../../util/getMessage";
import {useApiRequest} from "../../util/apiRequest";
import {usePermissions} from "../../util/usePermissions";


type Params = {
    personId?: string
    mode: string
}

type FormValues = {
    name: string;
    surname: string;
    phone: string;
    birthday: string;
}

const validation = Yup.object().shape({
    name: Yup.string().required("Name is required"),
    surname: Yup.string().required("Surname is required"),
    phone: Yup.string().required("Phone is required"),
    birthday: Yup.string().required("Name is required"),
})

const PersonDetailsComponent = () => {
    const {personId, mode} = useParams<Params>()
    const history = useHistory()
    const {canSeePersons, canEditPersons} = usePermissions()

    const {data: personData, reload: reloadPersonData} = useApiRequest(
        async () => {
            if (personId != null && (mode === 'edit' || mode === 'details') && canSeePersons) {
                return getPerson(parseInt(personId))
            }
            return null
        }, [personId],
    );

    const getTitle = () => {
        switch (mode) {
            case 'create':
                return 'Create person'
            case 'edit':
                return 'Edit person'
            case 'details':
                return 'person details'
            default:
                return ''
        }
    }

    const onSubmit = async (request: PersonRequest) => {
        if (mode === 'edit' && personId) {
            try {
                const response = await editPerson(parseInt(personId!), request)
                toast.success(response.message)
                reloadPersonData()
                history.push(PERSONS)
            } catch (e) {
                const error = await getExceptionMessage(e)
                toast.error(error)
            }
        } else {
            try {
                const response = await addPerson(request)
                toast.success(response.message)
                reloadPersonData()
                history.push(PERSONS)
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
                name: personData?.name ?? '',
                surname: personData?.surname ?? '',
                phone: personData?.phone ?? '',
                birthday: personData?.birthday ?? ''
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
                                        disabled={(mode !== 'create' && mode !== 'edit') || !canEditPersons}
                                        label="Name"
                                        placeholder="Enter name"
                                        {...inputPropsFormik('name', formik)}
                                    />
                                    <Input
                                        disabled={(mode !== 'create' && mode !== 'edit') || !canEditPersons}
                                        label="Surname"
                                        placeholder="Enter surname"
                                        {...inputPropsFormik('surname', formik)}
                                    />
                                    <Input
                                        disabled={(mode !== 'create' && mode !== 'edit') || !canEditPersons}
                                        label="Phone"
                                        placeholder="Enter phone"
                                        {...inputPropsFormik('phone', formik)}
                                    />
                                    <CustomDatePicker
                                        label="Birthday"
                                        maxDate={Date.now()}
                                        {...datePickerProps('birthday', formik)}
                                        {...datePickerErrorProps('birthday', formik)}
                                    />
                                </FormControl>
                            </Grid>
                        </Grid>
                        {((mode === 'create' || mode === 'edit') && canEditPersons) && (
                            <SaveButton disabled={formik.isSubmitting}/>
                        )}
                        <ReturnButton
                            disabled={formik.isSubmitting}
                            returnPath={PERSONS}
                        />
                    </form>
                </Grid>
            )}
        </Formik>
    )
}

export default PersonDetailsComponent
