import {Grid} from "@material-ui/core";
import 'react-toastify/dist/ReactToastify.css';
import {useParams} from "react-router-dom";
import {useApiRequest} from "../../util/apiRequest";
import {getTimeEvidence} from "../../endpoints/timeEvidence";
import {Input} from "../shared/Input";
import {formatTimeEvidenceStatus} from "./TimeEvidenceComponent";
import {TIME_EVIDENCE} from "../../routeNames";
import {ReturnButton} from "../shared/Buttons";

type Params = {
    timeEvidenceId: string
}

const TimeEvidenceDetailsComponent = () => {
    const {timeEvidenceId} = useParams<Params>()

    const {data: timeEvidenceData} = useApiRequest(
        async () => {
            console.log(timeEvidenceId)
            return getTimeEvidence(parseInt(timeEvidenceId))
        }, [timeEvidenceId]
    )

    return (
        <>
            {timeEvidenceData ? (
                <Grid>
                    <h1> {`Time evidence`}</h1>
                    <Grid container item xs={12}>
                        <Grid container>
                            <Grid item xs={8}>
                                <Input value={timeEvidenceData.person.name} disabled fullWidth label={'Person name'}/>
                            </Grid>
                            <Grid item xs={4} style={{paddingLeft: '5px'}}>
                                <Input value={formatTimeEvidenceStatus(timeEvidenceData.status)} disabled fullWidth label={'Status'}/>
                            </Grid>
                            <Grid item xs={10}>
                                <Input value={timeEvidenceData.project.name} disabled fullWidth label={'Project name'}/>
                            </Grid>
                            <Grid item xs={2} style={{paddingLeft: '5px'}}>
                                <Input value={timeEvidenceData.minutes.toString()} disabled fullWidth label={'Minutes'}/>
                            </Grid>
                            <Grid item xs={12}>
                                <Input value={timeEvidenceData.comment ?? ''} disabled fullWidth multiline minRows={6} label={'Comment'}/>
                            </Grid>
                            {timeEvidenceData.statusComment && (
                                <Grid item xs={12}>
                                    <Input value={timeEvidenceData.statusComment} disabled fullWidth multiline minRows={6} label={'Status comment'}/>
                                </Grid>
                            )}
                        </Grid>
                    </Grid>
                    <ReturnButton
                        returnPath={`${TIME_EVIDENCE}/manager`}
                    />
                </Grid>
            ) : (
                <></>
            )}
        </>
    )
}

export default TimeEvidenceDetailsComponent
