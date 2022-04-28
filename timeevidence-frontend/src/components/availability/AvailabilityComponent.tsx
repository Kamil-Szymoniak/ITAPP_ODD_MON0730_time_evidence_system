import {
  FormControl,
  makeStyles,
} from "@material-ui/core";
import {Formik} from "formik";
import * as Yup from 'yup';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {datePickerProps, inputPropsFormik, selectInputProps} from "../../util/inputProps";
import {
  AvailabilityRequest,
  AvailabilityResponse,
  PeriodResponse,
} from "../../dto/dto";
import {Input} from "../shared/Input";
import {SaveButton} from "../shared/Buttons";
import {getExceptionMessage} from "../../util/getMessage";
import {useApiRequest} from "../../util/apiRequest";
import React, {useState} from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography
} from "@mui/material";
import ConfirmationDialog from "../shared/ConfirmationDialog";
import {HighlightingDatePicker} from "../shared/HighlightingDatePicker";
import {getAllUserTeams} from "../../endpoints/team";
import {
  addAvailability,
  deleteAvailability,
  editAvailability, getAvailabilityInAMonth,
  getUserAvailabilities
} from "../../endpoints/availability";
import {Dropdown} from "../shared/Dropdown";
import Grid from "@mui/material/Grid";
import {CustomTimePicker} from "../shared/TimePicker";
import useWindowDimensions from "../../util/useWindowDimensions";

type FormValues = {
  comment: string | null
  date: string
  team: number | null
  periods: { timeFrom: Date, timeTo: Date, minutes: number }[]
}

type SubFormValues = {
  timeFrom: Date
  timeTo: Date
}

const validation = Yup.object().shape({
  minutes: Yup.string().required('Minutes are required'),
  team: Yup.number().required('Team is required'),
})

const subValidation = Yup.object().shape({
  timeFrom: Yup.string().required('Time from is required'),
  timeTo: Yup.string().required('Time to is required').test(
    'testTimeConcurrency',
    'Time to should be after time from',
    (item, testContext) => {
      return testContext.parent.timeFrom < testContext.parent.timeTo
    })
})

const formatStringLocalTime = (value: string) => value.substring(0, 5)

const formatDateLocalTime = (value: Date) => {
  return `${value.getHours() < 10 ? `0${value.getHours()}` : value.getHours()}:${value.getMinutes() < 10 ? `0${value.getMinutes()}` : value.getMinutes()}`
}

const parseStringToDateTime = (time: string) => {
  return new Date(`2000-04-20T${time}`)
}

const minutesBetween = (timeFrom: string, timeTo: string): number => {
  return (Date.parse(`04/20/2000 ${timeTo}`) - Date.parse(`04/20/2000 ${timeFrom}`)) / 60000
}

const timeIsBetween = (time: Date, intervalStart: Date, intervalEnd: Date) =>
  (time < intervalEnd && time > intervalStart)


const AvailabilityComponent = () => {
  const [currentAvailability, setCurrentAvailability] = useState<AvailabilityResponse | null>(null)
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [reload, setReload] = useState<number>(0)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState<boolean>(false)
  const [currentDate, setCurrentDate] = useState<string>(new Date().toISOString())
  const { width } = useWindowDimensions()
  const classes = useStyles()
  
  const {data: availabilityData, reload: reloadAvailabilityData} = useApiRequest(
    async () => {
      if (currentDate != null) {
        return getUserAvailabilities(currentDate.slice(0, 10))
      }
      return null
    }, [currentDate],
  );
  
  const {data: teams} = useApiRequest(() => getAllUserTeams(), [])
  
  const onSubmit = async (values: FormValues) => {
    const request: AvailabilityRequest = {
      date: currentDate,
      comment: values.comment,
      team: values.team!,
      periods: values.periods.map(it => ({
        timeFrom: formatDateLocalTime(it.timeFrom),
        timeTo: formatDateLocalTime(it.timeTo),
        minutes: it.minutes
      }))
    }
    console.log(request)
    if (currentAvailability !== null) {
      try {
        const response = await editAvailability(currentAvailability.id, request)
        toast.success(response.message)
        reloadAvailabilityData()
        setCurrentAvailability(null)
        setReload(reload + 1)
      } catch (e) {
        const error = await getExceptionMessage(e)
        toast.error(error)
      }
    } else {
      try {
        const response = await addAvailability(request)
        toast.success(response.message)
        reloadAvailabilityData()
        setReload(reload + 1)
      } catch (e) {
        const error = await getExceptionMessage(e)
        toast.error(error)
      }
    }
    setDialogOpen(false)
  }
  
  const onClose = () => {
    setCurrentAvailability(null)
    setDialogOpen(false)
  }
  
  const onConfirmDelete = async () => {
    try {
      if (currentAvailability) {
        const response = await deleteAvailability(currentAvailability.id)
        reloadAvailabilityData()
        toast.success(response.message)
        setCurrentAvailability(null)
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
        <Grid key={"calendar-container"} container item xs={12} style={{minWidth: '500px'}}>
          <Grid key={"calendar-outline"} item xs={12} style={{
            padding: '10px',
            outline: '2px ridge rgb(211,211,211)',
            borderRadius: '1rem',
            maxHeight: '500px',
          }}>
            <HighlightingDatePicker
              value={new Date(currentDate)}
              onChange={e => setCurrentDate(e?.toISOString() ?? '')}
              style={{width: '100%'}}
              label={`Date`}
              fetchFunction={getAvailabilityInAMonth}
              maxDate={new Date()}
              reload={reload}
            />
          </Grid>
        </Grid>
      )}
      <Grid key={"availability-container"} container item xs={width < 1000 ? 12 : 6} style={{minWidth: '500px'}}>
        {availabilityData && availabilityData.map((data: AvailabilityResponse) => (
          <>
            <Grid key={data.id} container item xs={12}
                  style={{padding: '10px', outline: '2px ridge rgb(211,211,211)', borderRadius: '1rem'}}>
              <Grid key={`${data.id}-form`} item xs={12}>
                <Grid key={`${data.id}-form-container`} container spacing={1}>
                  <Grid key={`${data.id}-form-name-container`} item xs={12}>
                    <Input key={`${data.id}-form-name-input`} value={data.team.name} disabled fullWidth label={'Team'}/>
                  </Grid>
                  <Grid key={`${data.id}-form-comment-container`} item xs={12}>
                    <Input key={`${data.id}-form-comment-input`} value={data.comment ?? ''} disabled fullWidth multiline minRows={6} label={'Comment'}/>
                  </Grid>
                  <Grid key={`${data.id}-form-minutes-container`} item xs={12}>
                    <Input
                      key={`${data.id}-form-minutes-input`}
                      value={data.periods.map(it => it.minutes).reduce((sum, curr) => sum + curr).toString()}
                      disabled
                      fullWidth
                      label={'All minutes'}
                    />
                  </Grid>
                  <Grid key={`${data.id}-form-periods-container`} container item xs={12} spacing={1}>
                    {data.periods.map((period: PeriodResponse) => (
                      <>
                        <Grid key={`${data.id}-form-periods-timeFrom-container`} item xs={5}>
                          <Input key={`${data.id}-form-periods-timeFrom-input`} value={formatStringLocalTime(period.timeFrom)} disabled fullWidth label={'From'}/>
                        </Grid>
                        <Grid key={`${data.id}-form-periods-timeTo-container`} item xs={5}>
                          <Input key={`${data.id}-form-periods-timeTo-input`} value={formatStringLocalTime(period.timeTo)} disabled fullWidth label={'To'}/>
                        </Grid>
                        <Grid key={`${data.id}-form-periods-minutes-container`} item xs={2}>
                          <Input key={`${data.id}-form-periods-minutes-input`} value={period.minutes.toString()} disabled fullWidth label={'Minutes'}/>
                        </Grid>
                      </>
                    ))}
                  </Grid>
                </Grid>
              </Grid>
              <Grid key={`${data.id}-form-periods-buttons`} item xs={12}>
                <Grid key={`${data.id}-form-periods-buttons-container`} item xs={6}>
                  <Button
                    key={`${data.id}-form-periods-delete-button`}
                    className={classes.closeButton}
                    onClick={() => {
                      setCurrentAvailability(data)
                      setDeleteDialogOpen(true)
                      reloadAvailabilityData()
                    }}
                  >
                    Delete
                  </Button>
                  <Button
                    key={`${data.id}-form-periods-edit-button`}
                    className={classes.closeButton}
                    onClick={() => {
                      setCurrentAvailability(data)
                      setDialogOpen(true)
                    }}
                  >
                    Edit
                  </Button>
                </Grid>
              </Grid>
            </Grid>
            <div key={`${data.id}-div`} style={{minHeight: '10px', maxHeight: '10px', minWidth: '10px'}}/>
          </>
        ))}
        {availabilityData?.length === 0 && (
          <Input key={`no-availability-input`} fullWidth label='' disabled value="No availability specified for this date"/>
        )}
        <Grid item xs={12}>
          <Button
            color="primary"
            onClick={() => {
              setCurrentAvailability(null)
              setDialogOpen(true)
            }}>
            Add new availability
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
              fetchFunction={getAvailabilityInAMonth}
              maxDate={new Date()}
              reload={reload}
            />
          </Grid>
        </Grid>
      )}
      <Dialog open={dialogOpen}>
        <Formik<FormValues>
          enableReinitialize={true}
          validationSchema={validation}
          initialValues={{
            comment: currentAvailability?.comment ?? '',
            date: currentDate,
            team: currentAvailability?.team.id ?? null,
            periods: currentAvailability?.periods.map((it) => ({
              timeFrom: parseStringToDateTime(it.timeFrom),
              timeTo: parseStringToDateTime(it.timeTo),
              minutes: minutesBetween(it.timeFrom, it.timeTo),
            })) ?? [],
          }}
          onSubmit={() => {
          }}
        >
          {(formik) => (
            <Grid>
              <form onSubmit={formik.handleSubmit}>
                <DialogTitle id='responsive-dialog-title'>
                  {currentAvailability ? 'Edit availability' : 'Create availability'}
                </DialogTitle>
                <DialogContent dividers>
                  {teams && teams.length > 0 ? (
                    <Grid key={`form-container`} container>
                      <Grid key={`team-container`} item xs={12} style={{paddingRight: '10px'}}>
                        <FormControl variant="outlined" fullWidth>
                          <Dropdown
                            key={`team-input`}
                            dropdownData={teams}
                            label="Team"
                            {...selectInputProps('team', formik)}
                            error={false}
                            value={(formik.values['team'] as any) ?? ''}
                          />
                        </FormControl>
                      </Grid>
                      <Grid key={`comment-container`} item xs={12} style={{paddingRight: '10px'}}>
                        <FormControl variant="outlined" fullWidth>
                          <Input
                            key={`comment-input`}
                            label="Comment"
                            placeholder="Enter comment"
                            multiline
                            {...inputPropsFormik('comment', formik)}
                          />
                        </FormControl>
                      </Grid>
                      <Grid key={`minutes-container`} item xs={12} style={{paddingRight: '10px'}}>
                        <FormControl variant="outlined" fullWidth>
                          <Input
                            key={`minutes-input`}
                            label="Minutes"
                            placeholder="0"
                            disabled
                            value={formik.values.periods
                              .map(it => it.minutes)
                              .reduce((prev, curr) => prev + curr, 0)
                              .toString()
                            }
                            id='minutes'
                          />
                        </FormControl>
                      </Grid>
                      <Grid container item xs={12}>
                        {formik.values.periods.map((period, index) => (
                          <>
                            <Formik<SubFormValues>
                              key={`${index}-form`}
                              enableReinitialize={true}
                              validationSchema={subValidation}
                              initialValues={{
                                timeFrom: period.timeFrom,
                                timeTo: period.timeTo,
                              }}
                              onSubmit={() => {
                              }}
                            >
                              {(subFormik) => (
                                <>
                                  <Grid key={`${index}-from-container`} item xs={4} style={{paddingRight: '10px'}}>
                                    <FormControl variant="outlined" fullWidth>
                                      <CustomTimePicker
                                        key={`${index}-from-input`}
                                        label="From"
                                        {...datePickerProps('timeFrom', subFormik)}
                                      />
                                    </FormControl>
                                  </Grid>
                                  <Grid key={`${index}-to-container`} item xs={4} style={{paddingRight: '10px'}}>
                                    <FormControl variant="outlined" fullWidth>
                                      <CustomTimePicker
                                        key={`${index}-to-input`}
                                        label="To"
                                        {...datePickerProps('timeTo', subFormik)}
                                      />
                                    </FormControl>
                                  </Grid>
                                  <Grid key={`${index}-remove-container`} item xs={2}>
                                    <Button
                                      key={`${index}-remove-button`}
                                      style={{
                                        marginRight: 10,
                                        color: '#adadad',
                                        backgroundColor: '#735364',
                                        top: '34%'
                                      }}
                                      onClick={() => {
                                        formik.values.periods.splice(index, 1)
                                        formik.setFieldValue('periods', formik.values.periods)
                                        subFormik.handleReset()
                                      }}
                                    >
                                      Remove
                                    </Button>
                                  </Grid>
                                  <Grid key={`${index}-save-container`} item xs={2}>
                                    <Button
                                      key={`${index}-save-button`}
                                      style={{
                                        marginRight: 10,
                                        color: '#adadad',
                                        backgroundColor: '#735364',
                                        top: '34%'
                                      }}
                                      onClick={() => {
                                        if (formik.values.periods.length === 0
                                          || formik.values.periods.filter((it, ind) => {
                                            return (timeIsBetween(subFormik.values.timeFrom, it.timeFrom, it.timeTo)
                                                || timeIsBetween(subFormik.values.timeTo, it.timeFrom, it.timeTo))
                                              && ind !== index
                                          }).length === 0) {
                                          formik.values.periods.splice(index, 1, {
                                            timeFrom: subFormik.values.timeFrom,
                                            timeTo: subFormik.values.timeTo,
                                            minutes: (subFormik.values.timeTo.getTime() - subFormik.values.timeFrom.getTime()) / 60000,
                                          })
                                          formik.setFieldValue('periods', formik.values.periods)
                                          toast.success("Period edited successfully")
                                        }
                                      }}>
                                      Save
                                    </Button>
                                  </Grid>
                                </>
                              )}
                            </Formik>
                          </>
                        ))}
                      </Grid>
                      <Formik<SubFormValues>
                        enableReinitialize={true}
                        validationSchema={subValidation}
                        initialValues={{
                          timeFrom: new Date('2000-04-20T00:00'),
                          timeTo: new Date('2000-04-20T01:00'),
                        }}
                        onSubmit={(values) => {
                          if (formik.values.periods.length === 0 || formik.values.periods.filter(it => {
                            return timeIsBetween(values.timeFrom, it.timeFrom, it.timeTo)
                              || timeIsBetween(values.timeTo, it.timeFrom, it.timeTo)
                          }).length === 0) {
                            formik.setFieldValue('periods', formik.values.periods.concat([{
                              timeFrom: values.timeFrom,
                              timeTo: values.timeTo,
                              minutes: (values.timeTo.getTime() - values.timeFrom.getTime()) / 60000,
                            }]))
                          }
                        }}
                      >
                        {(subFormik) => (
                          <>
                            <Grid key={`new-period-from-container`} item xs={5} style={{paddingRight: '10px'}}>
                              <FormControl variant="outlined" fullWidth>
                                <CustomTimePicker
                                  key={`new-period-from-input`}
                                  label="From"
                                  {...datePickerProps('timeFrom', subFormik)}
                                />
                              </FormControl>
                            </Grid>
                            <Grid key={`new-period-to-container`} item xs={5} style={{paddingRight: '10px'}}>
                              <FormControl variant="outlined" fullWidth>
                                <CustomTimePicker
                                  key={`new-period-to-input`}
                                  label="To"
                                  {...datePickerProps('timeTo', subFormik)}
                                />
                              </FormControl>
                            </Grid>
                            <Grid key={`new-period-add-container`} item xs={2}>
                              <Button
                                key={`new-period-add-button`}
                                style={{marginRight: 10, color: '#adadad', backgroundColor: '#735364', top: '34%'}}
                                onClick={() => subFormik.handleSubmit()}
                              >
                                Add
                              </Button>
                            </Grid>
                          </>
                        )}
                      </Formik>
                    </Grid>
                  ) : (
                    <Typography>
                      You are not assigned to any team
                    </Typography>
                  )}
                </DialogContent>
                <DialogActions>
                  {
                    (teams && teams.length > 0) &&
                      <SaveButton
                          onClick={() => {
                            if (formik.values.team === null) {
                              toast.error("Team field cannot be empty")
                              return
                            }
                            if (formik.values.periods.length === 0) {
                              toast.error("Add at least one period")
                              return
                            }
                            onSubmit(formik.values)
                          }}
                          disabled={formik.isSubmitting}
                      />
                  }
                  <Button
                    color="primary"
                    onClick={() => {
                      onClose()
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

export default AvailabilityComponent



