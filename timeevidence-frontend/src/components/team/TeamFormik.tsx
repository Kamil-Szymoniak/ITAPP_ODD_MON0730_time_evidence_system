import {Formik} from "formik";
import {FormControl, Grid} from "@material-ui/core";
import {Input} from "../shared/Input";
import {inputPropsFormik, selectInputProps} from "../../util/inputProps";
import {Dropdown} from "../shared/Dropdown";
import {ReturnButton, SaveButton} from "../shared/Buttons";
import {TEAMS} from "../../routeNames";
import React from "react";
import * as Yup from "yup";
import {usePermissions} from "../../util/usePermissions";
import {TeamRequest, TeamResponse} from "../../dto/dto";
import {useApiRequest} from "../../util/apiRequest";
import {getAllPersons} from "../../endpoints/person";

type FormValues = {
    name: string
    description: string | null
    teamMembers: number[]
    teamLeader: number | null
}

type Props = {
    onSubmit: (request: TeamRequest) => Promise<void>
    mode?: string
    teamData: TeamResponse | null
}

const validation = Yup.object().shape({
    name: Yup.string().required("Name is required"),
    teamMembers: Yup.array()
        .required("Team members are required")
        .min(1, 'Choose at least one project members'),
})

const TeamFormik = (props: Props) => {
    const {canEditTeams} = usePermissions()
    const {data: persons} = useApiRequest(() => getAllPersons(), []);

    const getTitle = () => {
        switch (props.mode) {
            case 'create':
                return 'Create team'
            case 'edit':
                return 'Edit team'
            case 'details':
                return 'Team details'
            default:
                return ''
        }
    }

    return (
        <Formik<FormValues>
            enableReinitialize={true}
            validationSchema={validation}
            initialValues={{
                name: props.teamData?.name ?? '',
                description: props.teamData?.description ?? '',
                teamMembers: props.teamData?.teamMembers.map(value => value.id!) ?? [],
                teamLeader: props.teamData?.teamLeader?.id ?? null
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
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditTeams}
                                            label="Name"
                                            placeholder="Enter name"
                                            {...inputPropsFormik('name', formik)}
                                        />
                                        <Input
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditTeams}
                                            label="Description"
                                            placeholder="Enter description"
                                            {...inputPropsFormik('description', formik)}
                                            value={(formik.values['description'] as any) ?? ''}
                                        />
                                        <Dropdown
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditTeams}
                                            multiple
                                            label="Team members"
                                            dropdownData={persons}
                                            {...selectInputProps('teamMembers', formik)}
                                        />
                                        <Dropdown
                                            disabled={(props.mode !== 'create' && props.mode !== 'edit') || !canEditTeams}
                                            label="Team leader"
                                            dropdownData={persons}
                                            {...selectInputProps('teamLeader', formik)}
                                            value={(formik.values['teamLeader'] as any) ?? ''}
                                        />
                                    </FormControl>
                                </Grid>
                            </Grid>
                        </form>
                    </Grid>
                    {((props.mode === 'create' || props.mode === 'edit') && canEditTeams) && (
                        <SaveButton disabled={formik.isSubmitting} onClick={() => props.onSubmit(formik.values)}/>
                    )}
                    <ReturnButton
                        disabled={formik.isSubmitting}
                        returnPath={TEAMS}
                    />
                </>
            )}
        </Formik>
    )
}

export default TeamFormik
