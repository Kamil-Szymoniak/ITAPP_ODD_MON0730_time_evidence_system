import {styled} from "@mui/material/styles";
import Tab from "@mui/material/Tab";
import * as React from "react";
import Tabs from "@mui/material/Tabs";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

export type TabPanelProps = {
    children?: React.ReactNode;
    index: number;
    value: number;
}

export const TabPanel = (props: TabPanelProps) => {
    const {children, value, index, ...other} = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            {...other}
        >
            {value === index && (
                <Box sx={{p: 3}}>
                    <Typography component={'span'}>{children}</Typography>
                </Box>
            )}
        </div>
    );
}

export const a11yProps = (index: number) => {
    return {
        id: `simple-tab-${index}`,
        'aria-controls': `simple-tabpanel-${index}`,
    };
}


export const StyledTab = styled((props: { label: string, disabled?: boolean }) => (
    <Tab disableRipple {...props} />
))(({theme}) => ({
    textTransform: 'none',
    fontSize: theme.typography.pxToRem(15),
    marginRight: theme.spacing(1),
    color: '#2A2A2A',
    borderTopLeftRadius: '1rem',
    borderTopRightRadius: '1rem',
    '&.Mui-selected': {
        color: '#ffffff',
        fontWeight: theme.typography.fontWeightBold,
        backgroundColor: '#747474'
    },
}));

export type StyledTabsProps = {
    children?: React.ReactNode;
    value: number;
    onChange: (event: React.SyntheticEvent, newValue: number) => void;
}

export const StyledTabs = styled((props: StyledTabsProps) => (
    <Tabs
        {...props}
        TabIndicatorProps={{children: <span className="MuiTabs-indicatorSpan"/>}}
    />
))({
    '& .MuiTabs-indicator': {
        display: 'flex',
        justifyContent: 'center',
        backgroundColor: 'transparent',
    },
    '& .MuiTabs-indicatorSpan': {
        width: '100%',
        backgroundColor: '#FF69B4',
    },
});