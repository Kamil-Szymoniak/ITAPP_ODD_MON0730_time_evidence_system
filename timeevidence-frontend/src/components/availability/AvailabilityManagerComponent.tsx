import {useHistory} from "react-router-dom";
import {Box} from "@mui/material";
import {AvailabilityResponse,} from "../../dto/dto";
import {AVAILABILITY} from "../../routeNames";
import DataTable from "../shared/table/AppTable";


const AvailabilityManagerComponent = () => {
    const history = useHistory()

    const onDetails = (availability: AvailabilityResponse) => {
        history.push(`${AVAILABILITY}/manager/${availability.id}`)
    }
    
    return (
        <>
            <Box>
                <h1>Availability</h1>
                <DataTable<AvailabilityResponse>
                    url="/api/availability"
                    onClick={onDetails}
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
                            name: 'Person',
                            field: 'person.name',
                            sort: true,
                            filter: true,
                        },
                        {
                            name: 'Team',
                            field: 'team.name',
                            sort: true,
                            filter: true,
                        },
                    ]}
                    actions={undefined}
                />
            </Box>
        </>
    )
}

export default AvailabilityManagerComponent
