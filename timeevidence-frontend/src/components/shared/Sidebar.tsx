import {styled} from "@mui/material";
import {Grid, makeStyles} from "@material-ui/core";
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import {Collapse, ListItem, ListItemIcon, MenuList, ListItemText} from "@material-ui/core";
import Drawer from '@mui/material/Drawer';
import {useState} from "react";
import {usePermissions} from "../../util/usePermissions";
import Routes from "./Routes";
import HeaderBar from "./HeaderBar";
import {Link} from 'react-router-dom';
import * as routeNames from '../../routeNames'
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import SupervisorAccountIcon from '@mui/icons-material/SupervisorAccount';

const Sidebar = () => {
    const classes = useStyles()
    const [open, setOpen] = useState<boolean>(false)
    const [admOpen, setAdmOpen] = useState<boolean>(false)
    const permissions = usePermissions()

    const handleOpen = () => setOpen(true)
    const handleClose = () => setOpen(false)

    const sidebarItem = (path: string, label: string, permission: boolean, icon: JSX.Element, listItem: string) => {
        return permission ? (
            <Link to={path} style={{textDecoration: 'none'}}>
                <ListItem className={listItem}>
                    <ListItemIcon className={classes.listItemIcon}>{icon}</ListItemIcon>
                    <ListItemText className={classes.underline} primary={label}/>
                </ListItem>
            </Link>
        ) : (<div/>)
    }

    return (
        <div>
            <HeaderBar
                open={open}
                handleOpen={handleOpen}
            />
            <Drawer
                className={classes.drawer}
                variant="persistent"
                anchor="left"
                open={open}
            >
                <Grid className={classes.drawerHeader}>
                    <IconButton onClick={handleClose}>
                        <ChevronLeftIcon className={classes.drawerIcon}/>
                    </IconButton>
                </Grid>
                <Divider/>
                <MenuList>

                    {sidebarItem(routeNames.PERSONS, "Persons", permissions.canSeePersons, <div/>, classes.mainMenu)}
                    {sidebarItem(routeNames.TEAMS, "Teams", permissions.canSeeTeams, <div/>, classes.mainMenu)}
                    {sidebarItem(routeNames.PROJECTS, "Projects", permissions.canSeeProjects, <div/>, classes.mainMenu)}
                    {sidebarItem(routeNames.TIME_EVIDENCE, "Time evidence", permissions.canSeeEvidence, <div/>, classes.mainMenu)}
                    {sidebarItem(routeNames.AVAILABILITY, "Availability", permissions.canSeeAvailability, <div/>, classes.mainMenu)}
                    {permissions.hasAdmPermission ? (
                        <ListItem onClick={() => setAdmOpen(!admOpen)} className={classes.mainMenu}>
                            <SupervisorAccountIcon className={classes.marginRight}/>
                            <ListItemText primary="Admin"/>
                            {admOpen ? <ArrowDropUpIcon/> : <ArrowDropDownIcon/>}
                        </ListItem>
                    ) : (<div/>)}
                    <Collapse in={admOpen} timeout={'auto'} unmountOnExit>
                        {sidebarItem(routeNames.PERMISSIONS, "Permissions", permissions.canSeePermissions, <div/>, classes.mainMenu)}
                        {sidebarItem(routeNames.ROLES, "Roles", permissions.canSeeRoles, <div/>, classes.mainMenu)}
                        {sidebarItem(routeNames.USERS, "Users", permissions.canSeeUsers, <div/>, classes.mainMenu)}
                    </Collapse>
                </MenuList>
            </Drawer>
            <Main open={open}>
                <Grid className={classes.drawerHeader}/>
                <Routes/>
            </Main>
        </div>
    )
}

export default Sidebar

const sidebarWidth = 240;

const useStyles = makeStyles((theme) => ({
    drawer: {
        width: sidebarWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
            width: sidebarWidth,
            boxSizing: 'border-box',
            backgroundColor: '#FF69B4',
            color: '#735364!important'
        },
    },
    drawerIcon: {
        color: '#ffffff'
    },
    drawerHeader: {
        display: 'flex',
        alignItems: 'center',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar,
        // justifyContent: 'flex-end',
    },
    background: {
        backgroundImage: `url(${Image})`
    },
    mainMenu: {
        color: '#000000',
        paddingTop: 0,
        paddingBottom: 0,
        "&:hover": {
            backgroundColor: '#C0C0C0'
        },
        "&:active": {
            backgroundColor: '#C0C0C0'
        },
    },
    secondaryMenu: {
        color: '#3333ff',
        paddingTop: 0,
        paddingBottom: 0,
        "&:hover": {
            backgroundColor: '#C0C0C0'
        },
        "&:active": {
            backgroundColor: '#C0C0C0'
        },
    },
    listItemIcon: {
        minWidth: "30px",
        paddingTop: 0,
    },
    listPadding: {
        paddingTop: '0!important',
        paddingBottom: '0!important',
    },
    marginRight: {
        marginRight: "5px"
    },
    subMenu: {
        width: 200,
        color: '#000000',
        marginLeft: '30px',
        paddingTop: "0px",
        paddingBottom: "0px",
        "&:hover": {
            backgroundColor: '#C0C0C0'
        },
    },
    passwordColor: {
        color: '#3333ff',
    },
    menuItemIcon: {
        color: '#97c05c',
    },
    underline: {
        textDecoration: 'none',
        fontStyle: 'none',
    }
}))

const Main = styled('main', {shouldForwardProp: (prop) => prop !== 'open'})<{
    open?: boolean;
}>(({theme, open}) => ({
    flexGrow: 1,
    padding: theme.spacing(3),
    // backgroundColor: '#d3d3d3',
    transition: theme.transitions.create('margin', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: `-${sidebarWidth}px`,
    ...(open && {
        // backgroundColor: '#d3d3d3',
        transition: theme.transitions.create('margin', {
            easing: theme.transitions.easing.easeOut,
            duration: theme.transitions.duration.enteringScreen,
        }),
        marginLeft: 0,
    }),
}));