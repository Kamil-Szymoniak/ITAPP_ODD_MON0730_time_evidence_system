import {Grid} from "@mui/material";
import {ReactNode} from "react";

type Props = {
  children: ReactNode
  style?: any
  xs?: 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 'auto' | boolean
}

const MyGrid = (props: Props) => {
  return (
    <Grid container item xs={props.xs ?? 12}>
      <Grid item xs={12} style={{padding: '10px', ...props.style}}>
        {props.children}
      </Grid>
    </Grid>
  )
}

export default MyGrid