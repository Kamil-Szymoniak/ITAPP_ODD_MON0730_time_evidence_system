import {Grid} from "@material-ui/core";
import 'react-toastify/dist/ReactToastify.css';
import {useParams} from "react-router-dom";
import {useApiRequest} from "../../util/apiRequest";
import {Input} from "../shared/Input";
import {AVAILABILITY} from "../../routeNames";
import {ReturnButton} from "../shared/Buttons";
import {getAvailability} from "../../endpoints/availability";

type Params = {
  availabilityId: string
}

const AvailabilityDetailsComponent = () => {
  const {availabilityId} = useParams<Params>()
  
  const {data: availabilityData} = useApiRequest(
    async () => {
      console.log(availabilityId)
      return getAvailability(parseInt(availabilityId))
    }, [availabilityId]
  )
  
  return (
    <>
      {availabilityData ? (
        <Grid>
          <h1> {`Availability, id: ${availabilityData.id}`}</h1>
          <Grid container item xs={12}>
            <Grid container>
              <Grid item xs={12}>
                <Input value={availabilityData.person.name} disabled fullWidth label={'Person name'}/>
              </Grid>
              <Grid item xs={12}>
                <Input value={availabilityData.team.name} disabled fullWidth label={'Team name'}/>
              </Grid>
              <Grid item xs={12}>
                <Input value={availabilityData.comment ?? ''} disabled fullWidth multiline minRows={6} label={'Comment'}/>
              </Grid>
              {availabilityData.periods.map((period, index) => (
                <Grid item container xs={12} spacing={2} key={`period-${index}`}>
                  <Grid item xs={5} key={`period-${index}-from`}>
                    <Input value={period.timeFrom} disabled fullWidth multiline label={'Time from'}/>
                  </Grid>
                  <Grid item xs={5} key={`period-${index}-to`}>
                    <Input value={period.timeTo} disabled fullWidth multiline label={'Time to'}/>
                  </Grid>
                  <Grid item xs={2} key={`period-${index}-minutes`}>
                    <Input value={period.minutes.toString()} disabled fullWidth multiline label={'Minutes'}/>
                  </Grid>
                </Grid>
              ))}
            </Grid>
          </Grid>
          <ReturnButton
            returnPath={`${AVAILABILITY}/manager`}
          />
        </Grid>
      ) : (
        <></>
      )}
    </>
  )
}

export default AvailabilityDetailsComponent
