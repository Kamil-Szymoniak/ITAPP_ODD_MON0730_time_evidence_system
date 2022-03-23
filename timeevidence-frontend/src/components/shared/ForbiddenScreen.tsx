import React from 'react';
import {Grid, Typography} from "@mui/material";
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';


export const ForbiddenScreen = () => (
    <Grid container xs={12}>
        <Grid item xs={3}/>
        <Grid item xs={6}>
            <Grid style={{marginTop: 50, textAlign: 'center'}}>
                <ErrorOutlineIcon style={{fontSize: 200, color: '#ff0000'}}/>
                <Typography fontSize={50}>
                    Access forbidden
                </Typography>
                <Typography fontSize={25}>
                    Ask admin for permission to this view
                </Typography>
            </Grid>
        </Grid>
        <Grid item xs={3}/>
    </Grid>
)