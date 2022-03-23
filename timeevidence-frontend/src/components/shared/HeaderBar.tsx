import * as React from 'react';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import LogoutIcon from '@mui/icons-material/Logout';
import {FC, MouseEventHandler} from "react";
import {makeStyles} from "@material-ui/core";
import {AppBarProps as MuiAppBarProps} from "@mui/material/AppBar/AppBar";
import {styled} from "@mui/material/styles";
import MuiAppBar from "@mui/material/AppBar";
import {AuthConsumer} from '../auth/AuthContext';
import {Button} from "@mui/material";

type Props = MuiAppBarProps & {
    open: boolean;
    handleOpen: MouseEventHandler<HTMLButtonElement> | undefined;
}

const HeaderBar: FC<Props> = (props) => {
    const classes = useStyles();
    return (
        <div>
            <AppBar position="fixed" open={props.open} handleOpen={props.handleOpen}>
                <Toolbar className={classes.toolbar}>
                    <IconButton
                        color="inherit"
                        aria-label="open drawer"
                        onClick={props.handleOpen}
                        edge="start"
                        sx={{mr: 2, ...(props.open && {display: 'none'})}}
                    >
                        <MenuIcon/>
                    </IconButton>
                    <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                    <span
                        style={{
                            marginLeft: "20px",
                            color: '#2A2A2A'
                        }}
                    >
                        Time evidence system
                    </span>
                    </Typography>
                    <div>
                            <AuthConsumer>
                                {(({askForLogout}) => (
                                    <Button
                                        variant='outlined'
                                        onClick={() => askForLogout()}
                                        startIcon={<LogoutIcon/>}
                                        className={classes.logoutButton}
                                    >
                                        Logout
                                    </Button>
                                ))}
                            </AuthConsumer>

                    </div>
                </Toolbar>
            </AppBar>
        </div>
    );
}

export default HeaderBar

const useStyles = makeStyles({
    toolbar: {
        background: 'linear-gradient(45deg, #FF69B4 35%, #C71585 90%)',
        color: '#735364!important'
    },
    logoutButton: {
        color: '#735364!important',
        backgroundColor: '#F4B0D6!important',
        "&:hover": {
            backgroundColor: '#F7C3E9!important',
            color: '#000000!important',
            fontColor: '#000000!important',
            fontWeight: 500,
        },
    }
});

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== 'open',
})<Props>(({theme, open}) => ({
    transition: theme.transitions.create(['margin', 'width'], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
        width: `calc(100% - ${240}px)`,
        marginLeft: `${240}px`,
        transition: theme.transitions.create(['margin', 'width'], {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
    }),
}));
