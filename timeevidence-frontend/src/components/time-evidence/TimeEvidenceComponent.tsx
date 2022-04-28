import {
  FormControl,
  Grid, makeStyles,
} from "@material-ui/core";
import {Formik} from "formik";
import * as Yup from 'yup';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {inputPropsFormik, selectInputProps} from "../../util/inputProps";
import {DictionaryResponse, TimeEvidenceRequest, TimeEvidenceResponse, TimeEvidenceStatus} from "../../dto/dto";
import {Input} from "../shared/Input";
import {SaveButton} from "../shared/Buttons";
import {getExceptionMessage} from "../../util/getMessage";
import {useApiRequest} from "../../util/apiRequest";
import React, {useState} from "react";
import {
  addTimeEvidence,
  deleteTimeEvidence,
  editTimeEvidence,
  getTimeEvidenceInAMonth,
  getUserTimeEvidences
} from "../../endpoints/timeEvidence";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography
} from "@mui/material";
import {Dropdown} from "../shared/Dropdown";
import {getAllUserProjects} from "../../endpoints/project";
import ConfirmationDialog from "../shared/ConfirmationDialog";
import {HighlightingDatePicker} from "../shared/HighlightingDatePicker";
import useWindowDimensions from "../../util/useWindowDimensions";

type FormValues = {
  minutes: number
  comment: string | null
  project: number | null
}

const validation = Yup.object().shape({
  minutes: Yup.string().required('Minutes are required'),
  project: Yup.number().required('Project is required'),
})

export const formatTimeEvidenceStatus = (status: TimeEvidenceStatus) => {
  switch (status) {
    case 'SENT':
      return 'Sent'
    case "ACCEPTED":
      return 'Accepted'
    case "REJECTED":
      return "Rejected"
  }
}

type TimeEvidenceProps = {
  open: boolean
  currentTimeEvidence: TimeEvidenceResponse | null
  currentDate: string
  onSubmit: (values: TimeEvidenceRequest) => Promise<void>
  onClose: () => void
  projects: readonly DictionaryResponse[] | null
}

const TimeEvidenceDialog = (props: TimeEvidenceProps) => (
  <Dialog open={props.open}>
    <Formik<FormValues>
      enableReinitialize={true}
      validationSchema={validation}
      initialValues={{
        minutes: props.currentTimeEvidence?.minutes ?? 0,
        comment: props.currentTimeEvidence?.comment ?? '',
        project: props.currentTimeEvidence?.project.id ?? null
      }}
      onSubmit={async (values) => {
        const newValues = {
          date: props.currentDate,
          minutes: values.minutes,
          comment: values.comment,
          project: values.project!
        }
        await props.onSubmit(newValues);
      }}
    >
      {(formik) => (
        <Grid>
          <form onSubmit={formik.handleSubmit}>
            <DialogTitle id='responsive-dialog-title'>
              {props.currentTimeEvidence ? 'Edit time evidence' : 'Create time evidence'}
            </DialogTitle>
            <DialogContent dividers>
              {props.projects && props.projects.length > 0 ? (
                <Grid container>
                  <Grid item xs={10} style={{paddingRight: '10px'}}>
                    <FormControl variant="outlined" fullWidth>
                      <Dropdown
                        dropdownData={props.projects}
                        label="Project"
                        {...selectInputProps('project', formik)}
                      />
                    </FormControl>
                  </Grid>
                  <Grid item xs={2} style={{paddingRight: '10px'}}>
                    <FormControl variant="outlined" fullWidth>
                      <Input
                        label="Minutes"
                        type="number"
                        {...inputPropsFormik('minutes', formik)}
                      />
                    </FormControl>
                  </Grid>
                  <Grid item xs={12} style={{paddingRight: '10px'}}>
                    <FormControl variant="outlined" fullWidth>
                      <Input
                        label="Comment"
                        placeholder="Enter comment"
                        multiline
                        {...inputPropsFormik('comment', formik)}
                      />
                    </FormControl>
                  </Grid>
                </Grid>
              ) : (
                <Typography>
                  You are not assigned to any project
                </Typography>
              )}
            
            </DialogContent>
            <DialogActions>
              {(props.projects && props.projects.length > 0) && <SaveButton disabled={formik.isSubmitting}/>}
              <Button
                autoFocus color="primary"
                onClick={() => {
                  props.onClose()
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
)

const TimeEvidenceComponent = () => {
  const [currentTimeEvidence, setCurrentTimeEvidence] = useState<TimeEvidenceResponse | null>(null)
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [reload, setReload] = useState<number>(0)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
  const [currentDate, setCurrentDate] = useState<string>(new Date().toISOString())
  const { width } = useWindowDimensions()
  const classes = useStyles()
  
  const {data: timeEvidenceData, reload: reloadTimeEvidenceData} = useApiRequest(
    async () => {
      if (currentDate != null) {
        return getUserTimeEvidences(`search=date:${currentDate.slice(0, 10)}`)
      }
      return null
    }, [currentDate],
  );
  
  const {data: projects} = useApiRequest(() => getAllUserProjects(), [])
  
  const onSubmit = async (request: TimeEvidenceRequest) => {
    if (currentTimeEvidence !== null) {
      try {
        const response = await editTimeEvidence(currentTimeEvidence.id, request)
        toast.success(response.message)
        reloadTimeEvidenceData()
        setCurrentTimeEvidence(null)
        setReload(reload + 1)
      } catch (e) {
        const error = await getExceptionMessage(e)
        toast.error(error)
      }
    } else {
      try {
        const response = await addTimeEvidence(request)
        toast.success(response.message)
        reloadTimeEvidenceData()
        setReload(reload + 1)
      } catch (e) {
        const error = await getExceptionMessage(e)
        toast.error(error)
      }
    }
    setDialogOpen(false)
  }
  
  const onClose = () => {
    setCurrentTimeEvidence(null)
    setDialogOpen(false)
  }
  
  const onConfirmDelete = async () => {
    try {
      if (currentTimeEvidence) {
        const response = await deleteTimeEvidence(currentTimeEvidence.id)
        reloadTimeEvidenceData()
        toast.success(response.message)
        setCurrentTimeEvidence(null)
        setDeleteDialogOpen(false)
        setReload(reload + 1)
      }
    } catch (e) {
      const error = await getExceptionMessage(e);
      toast.error(error);
    }
  }
  
  return (
    <Grid container spacing={3}>
      {width < 1000 && (
        <Grid container item xs={12} style={{minWidth: '500px'}}>
          <Grid item xs={12} style={{
            padding: '10px',
            outline: '2px ridge rgb(211,211,211)',
            borderRadius: '1rem',
            maxHeight: '500px'
          }}>
            <HighlightingDatePicker
              value={new Date(currentDate)}
              onChange={e => setCurrentDate(e?.toISOString() ?? '')}
              style={{width: '100%'}}
              label={`Date`}
              fetchFunction={getTimeEvidenceInAMonth}
              maxDate={new Date()}
              reload={reload}
            />
          </Grid>
        </Grid>
      )}
      <Grid container item xs={width < 1000 ? 12 : 6} spacing={1} style={{minWidth: '500px'}}>
        {timeEvidenceData?.items.map((data: TimeEvidenceResponse) => (
          <Grid key={data.id} container item style={{
            outline: '2px ridge rgb(211,211,211)',
            borderRadius: '1rem',
            minWidth: '500px'
          }}>
            <Grid item xs={12} container spacing={1}>
                <Grid item xs={8}>
                  <Input value={data.project.name} disabled fullWidth label={'Project'}/>
                </Grid>
                <Grid item xs={2}>
                  <Input value={formatTimeEvidenceStatus(data.status)} disabled fullWidth label={'Status'}/>
                </Grid>
                <Grid item xs={2}>
                  <Input value={data.minutes.toString()} disabled fullWidth label={'Minutes'}/>
                </Grid>
                <Grid item xs={12}>
                  <Input value={data.comment ?? ''} disabled fullWidth multiline minRows={6} label={'Comment'}/>
                </Grid>
                {data.statusComment && (
                  <Grid item xs={12}>
                    <Input value={data.statusComment} disabled fullWidth multiline minRows={6}
                           label={'Status comment'}/>
                  </Grid>
                )}
            </Grid>
            <Grid item xs={12}>
              <Grid item xs={6}>
                <Button
                  disabled={data.status === 'ACCEPTED'}
                  className={classes.closeButton}
                  onClick={() => {
                    setCurrentTimeEvidence(data)
                    setDeleteDialogOpen(true)
                    reloadTimeEvidenceData()
                  }}
                >
                  Delete
                </Button>
                <Button
                  disabled={data.status === 'ACCEPTED'}
                  className={classes.closeButton}
                  onClick={() => {
                    setCurrentTimeEvidence(data)
                    setDialogOpen(true)
                  }}
                >
                  Edit
                </Button>
              </Grid>
            </Grid>
          </Grid>
        ))}
        {timeEvidenceData?.items.length === 0 && (
          <Input fullWidth label='' disabled value="No work time evidence present for the specified date"/>
        )}
        <Grid item xs={12}>
          <Button
            color="primary"
            onClick={() => {
              setCurrentTimeEvidence(null)
              setDialogOpen(true)
            }}>
            Add new time evidence
          </Button>
        </Grid>
      </Grid>
      {width >= 1000 && (
        <Grid container item xs={6} style={{minWidth: '500px'}}>
          <Grid item xs={12} style={{
            padding: '10px',
            outline: '2px ridge rgb(211,211,211)',
            borderRadius: '1rem',
            maxHeight: '500px'
          }}>
            <HighlightingDatePicker
              value={new Date(currentDate)}
              onChange={e => setCurrentDate(e?.toISOString() ?? '')}
              style={{width: '100%'}}
              label={`Date`}
              fetchFunction={getTimeEvidenceInAMonth}
              maxDate={new Date()}
              reload={reload}
            />
          </Grid>
        </Grid>
      )}
      <TimeEvidenceDialog
        open={dialogOpen}
        currentDate={currentDate}
        onSubmit={onSubmit}
        onClose={onClose}
        projects={projects}
        currentTimeEvidence={currentTimeEvidence}
      />
      <ConfirmationDialog
        dialogTitle="Delete time evidence?"
        confirmText="Delete"
        cancelText="Cancel"
        dialogContent="Are you sure to delete this time evidence?"
        isDialogOpen={deleteDialogOpen}
        onConfirm={onConfirmDelete}
        onHide={() => setDeleteDialogOpen(false)}
        onCancel={() => setDeleteDialogOpen(false)}
      />
    </Grid>
  )
}

const useStyles = makeStyles((theme) => ({
    closeButton: {
      position: 'absolute',
      right: theme.spacing(1),
      top: theme.spacing(1),
      color: theme.palette.grey[500],
    },
  }
));

export default TimeEvidenceComponent



