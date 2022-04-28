import {useState} from "react";
import {useHistory} from "react-router-dom";
import EditIcon from '@mui/icons-material/Edit';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle} from "@mui/material";
import {TimeEvidenceChangeStatusRequest, TimeEvidenceResponse, TimeEvidenceStatus} from "../../dto/dto";
import {usePermissions} from "../../util/usePermissions";
import {TIME_EVIDENCE} from "../../routeNames";
import DataTable from "../shared/table/AppTable";
import * as Yup from "yup";
import {Formik} from "formik";
import {FormControl, Grid} from "@material-ui/core";
import {EnumDropdown} from "../shared/Dropdown";
import {inputPropsFormik} from "../../util/inputProps";
import {Input} from "../shared/Input";
import {SaveButton} from "../shared/Buttons";
import {editTimeEvidenceStatus} from "../../endpoints/timeEvidence";
import {toast} from "react-toastify";
import {getExceptionMessage} from "../../util/getMessage";


type FormValues = {
    status: TimeEvidenceStatus
    statusComment: string | null
}

const validation = Yup.object().shape({
    status: Yup.string()
        .required('Status is required')
        .matches(/^(ACCEPTED|REJECTED)$/, "Status must be either Accepted or Rejected"),
    statusComment: Yup.string().nullable(),
})


const TimeEvidenceManagerComponent = () => {
    const [currentTimeEvidence, setCurrentTimeEvidence] = useState<TimeEvidenceResponse | null>(null)
    const [reload, setReload] = useState<number>(0)
    const history = useHistory()
    const [dialogOpen, setDialogOpen] = useState<boolean>(false)
    const { canEditEvidence } = usePermissions()

    const onDetails = (timeEvidence: TimeEvidenceResponse) => {
        history.push(`${TIME_EVIDENCE}/manager/${timeEvidence.id}`)
    }


    const onChangeStatus = (timeEvidence: TimeEvidenceResponse) => {
        if (timeEvidence.status === 'SENT') {
            setCurrentTimeEvidence(timeEvidence)
            setDialogOpen(true)
        }
    }

    const onSubmit = async (request: TimeEvidenceChangeStatusRequest) => {
        try {
            const response = await editTimeEvidenceStatus(currentTimeEvidence!.id, request)
            toast.success(response.message)
            setReload(reload + 1)
        } catch (e) {
            const error = await getExceptionMessage(e)
            toast.error(error)
        }
        setCurrentTimeEvidence(null)
        setDialogOpen(false)
    }

    return (
        <>
            <Box>
                <h1>Time evidence</h1>
                <DataTable<TimeEvidenceResponse>
                    url="/api/time-evidence"
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
                            name: 'Date',
                            field: 'date',
                            type: 'date',
                            sort: true,
                            filter: true,
                        },
                        {
                            name: 'Minutes',
                            field: 'minutes',
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
                            name: 'Project',
                            field: 'project.name',
                            sort: true,
                            filter: true,
                        },
                        {
                            name: 'Status',
                            field: 'status',
                            sort: true,
                            filter: true,
                        }
                    ]}
                    actions={canEditEvidence ? [
                        {
                            onClick: onChangeStatus,
                            content: 'Change status',
                            icon: <EditIcon style={{color: '#FF69B4'}}/>,
                            disable: (row, index) => row.status !== 'SENT'
                        }
                    ] : undefined}
                />
            </Box>
            {currentTimeEvidence && (
                <Dialog open={dialogOpen}>
                    <Formik<FormValues>
                        enableReinitialize={true}
                        validationSchema={validation}
                        initialValues={{
                            status: currentTimeEvidence!.status,
                            statusComment: currentTimeEvidence!.statusComment,
                        }}
                        onSubmit={async (values) => {
                            await onSubmit(values);
                        }}
                    >
                        {(formik) => (
                            <Grid>
                                <form onSubmit={formik.handleSubmit}>
                                    <DialogTitle id='responsive-dialog-title'>
                                        Edit time evidence status
                                    </DialogTitle>
                                    <DialogContent dividers>
                                        <Grid container>
                                            <Grid item xs={12} style={{paddingRight: '10px'}}>
                                                <FormControl variant="outlined" fullWidth>
                                                    <EnumDropdown
                                                        id='status'
                                                        value={formik.values['status']}
                                                        onChange={(e: any) => {
                                                            formik.setFieldValue('status', e.target.value)
                                                            formik.setFieldTouched('status', true, true)
                                                        }}
                                                        dropdownData={['SENT', 'ACCEPTED', "REJECTED"]}
                                                        label={'Status'}
                                                        error={formik.errors['status'] != null && formik.touched['status']}
                                                        helperText={formik.errors['status']}
                                                    />
                                                </FormControl>
                                            </Grid>
                                            <Grid item xs={12} style={{paddingRight: '10px'}}>
                                                <FormControl variant="outlined" fullWidth>
                                                    <Input
                                                        label="Status comment"
                                                        placeholder="Enter comment"
                                                        multiline
                                                        {...inputPropsFormik('statusComment', formik)}
                                                        value={(formik.values['statusComment'] as any) ?? ''}
                                                    />
                                                </FormControl>
                                            </Grid>
                                        </Grid>

                                    </DialogContent>
                                    <DialogActions>
                                        <SaveButton disabled={formik.isSubmitting}/>
                                        <Button
                                            autoFocus color="primary"
                                            onClick={() => {
                                                setDialogOpen(false)
                                                setCurrentTimeEvidence(null)
                                                formik.handleReset()
                                            }}>
                                            Cancel
                                        </Button>
                                    </DialogActions>
                                </form>
                            </Grid>
                        )}
                    </Formik>
                </Dialog>
            )}
        </>
    )
}

export default TimeEvidenceManagerComponent
