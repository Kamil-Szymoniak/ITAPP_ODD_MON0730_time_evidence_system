import {Formik} from "formik";
import {FormControl, Grid} from "@material-ui/core";
import {Input} from "../shared/Input";
import {datePickerErrorProps, datePickerProps, inputPropsFormik, selectInputProps} from "../../util/inputProps";
import {Dropdown} from "../shared/Dropdown";
import {ReturnButton, SaveButton} from "../shared/Buttons";
import {PROJECTS} from "../../routeNames";
import React from "react";
import * as Yup from "yup";
import {usePermissions} from "../../util/usePermissions";
import {ProjectRequest, ProjectResponse} from "../../dto/dto";
import {useApiRequest} from "../../util/apiRequest";
import {getAllPersons} from "../../endpoints/person";
import {CustomDatePicker} from "../shared/DatePicker";

type FormValues = {
    name: string
    inhouseName: string | null
    description: string | null
    clientName: string
    beginningDate: string
    projectMembers: number[]
    projectManager: number | null
}

type Props = {
    onSubmit: (request: ProjectRequest) => Promise<void>
    mode?: string
    projectData: ProjectResponse | null
}

const validation = Yup.object().shape({
    name: Yup.string().required("Name is required"),
    clientName: Yup.string().required("Client name is required"),
    beginningDate: Yup.string().required("Beginning date is required"),
    projectMembers: Yup.array()
        .required("Project members are required")
        .min(1, 'Choose at least one project members'),
})

const ProjectFormik = (props: Props) => {
    const {canEditProjects} = usePermissions()
    const {data: persons} = useApiRequest(() => getAllPersons(), []);

    const getTitle = () => {
        switch (props.mode) {
            case 'create':
                return 'Create project'
            case 'edit':
                return 'Edit project'
            case 'details':
                return 'Project details'
            default:
                return ''
        }
    }

    return (
        <Formik<FormValues>
            enableReinitialize={true}
            validationSchema={validation}
            initialValues={{
                name: props.projectData?.name ?? '',
                inhouseName: props.projectData?.inhouseName ?? '',
                description: props.projectData?.description ?? '',
                clientName: props.projectData?.clientName ?? '',
                beginningDate: props.projectData?.beginningDate ?? '',
                projectMembers: props.projectData?.projectMembers.map(value => value.id!) ?? [],
                projectManager: props.projectData?.projectManager?.id ?? null
            }}
            onSubmit={async (values) => {
                await props.onSubmit(values);
            }}
        >
            {(formik) => (
                <>
                    <Grid>
                        <form onSubmit={formik.handleSubmit}>
                            <h1> {getTitle()}</h1>
                            <Grid container item xs={12}>
                                <Grid item xs={6} style={{paddingRight: '10px'}}>
                                    <FormControl variant="outlined" fullWidth>
                                        <Input
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Name"
                                            placeholder="Enter name"
                                            {...inputPropsFormik('name', formik)}
                                        />
                                        <Input
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Inhouse name"
                                            placeholder="Enter inhouse name"
                                            {...inputPropsFormik('inhouseName', formik)}
                                            value={(formik.values['inhouseName'] as any) ?? ''}
                                        />
                                        <Input
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Description"
                                            placeholder="Enter description"
                                            {...inputPropsFormik('description', formik)}
                                            value={(formik.values['description'] as any) ?? ''}
                                        />
                                        <Input
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Client name"
                                            placeholder="Enter client name"
                                            {...inputPropsFormik('clientName', formik)}
                                        />
                                        <CustomDatePicker
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Beginning date"
                                            maxDate={Date.now()}
                                            {...datePickerProps('beginningDate', formik)}
                                            {...datePickerErrorProps('beginningDate', formik)}
                                        />
                                        <Dropdown
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            multiple
                                            label="Project members"
                                            dropdownData={persons}
                                            {...selectInputProps('projectMembers', formik)}
                                        />
                                        <Dropdown
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditProjects}
                                            label="Project manager"
                                            dropdownData={persons}
                                            {...selectInputProps('projectManager', formik)}
                                            value={(formik.values['projectManager'] as any) ?? ''}
                                        />
                                    </FormControl>
                                </Grid>
                            </Grid>
                        </form>
                    </Grid>
                    {((props.mode === 'create' || props.mode === 'edit') && canEditProjects) && (
                        <SaveButton disabled={formik.isSubmitting} onClick={() => props.onSubmit(formik.values)}/>
                    )}
                    <ReturnButton
                        disabled={formik.isSubmitting}
                        returnPath={PROJECTS}
                    />
                </>
            )}
        </Formik>
    )
}

export default ProjectFormik
