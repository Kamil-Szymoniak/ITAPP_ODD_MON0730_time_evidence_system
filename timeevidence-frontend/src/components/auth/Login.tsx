/* eslint-disable no-template-curly-in-string */
import React, {FC} from 'react';
import {RouteComponentProps, withRouter} from "react-router";
import {FormikProps, withFormik} from 'formik';
import {inputProps} from "../../util/inputProps";
import * as Yup from "yup";
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import {makeStyles} from "@material-ui/core";
import {LoginRequest} from "../../dto/dto";
import {AuthContextProps, withAuthContext} from "./AuthContext";
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {Input} from "../shared/Input";
import {FormControl} from "@mui/material";

toast.configure();

type Props = {
    loginData?: LoginRequest;
} & RouteComponentProps
    & AuthContextProps;


type FormValues = {
    login: string;
    password: string;
}

const formikEnhancer = withFormik<Props, FormValues>({
    enableReinitialize: true,
    validationSchema: Yup.object()
        .shape({
            login: Yup.string()
                .required('Login is required'),
            password: Yup.string()
                .required('Password is required')
                .max(30, 'Password should be at max ${max} characters long')
                .min(8, 'Password should be at least ${min} characters long'),
        }),

    mapPropsToValues: (props) => ({
        login: props.loginData ? props.loginData.login : '',
        password: props.loginData ? props.loginData.password : '',
    }),

    handleSubmit: async (values, {props}) => {
        await props.login(values.login, values.password);
    }
});


const Login: FC<Props & FormikProps<FormValues>> = (props) => {
    const classes = useStyles();

    return (
        <Grid container component="main" sx={{height: '100vh'}}>
            <CssBaseline/>
            <Grid item xs={12} sm={8} md={3} component={Paper} elevation={6} square>
                <Box
                    sx={{
                        my: 8,
                        mx: 4,
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar className={classes.avatar}>
                        <LockOutlinedIcon/>
                    </Avatar>
                    <Typography marginBottom={5} component="h1" variant="h5">
                        Sign in
                    </Typography>
                    <FormControl variant="outlined" fullWidth>
                        <Input
                            label="Login"
                            autoFocus
                            {...inputProps(props, 'login')}
                        />
                        <Input
                            label="Password"
                            type="password"
                            {...inputProps(props, 'password')}
                        />
                        <Button
                            style={{marginTop: 15, color: "#ffffff"}}
                            className={classes.submit}
                            onClick={() => props.handleSubmit()}
                        >
                            Sign In
                        </Button>
                    </FormControl>
                </Box>
            </Grid>
        </Grid>
    );
}

export default withRouter(withAuthContext(formikEnhancer(Login)));

const useStyles = makeStyles((theme) => (
    {
        paper: {
            margin: theme.spacing(8, 4),
            display: 'flex',
            flexDirection: 'column', alignItems: 'center',
        },
        avatar: {
            margin: theme.spacing(1),
            bgColor: "#ff0000",
            color: "#ff0000",
        },
        marginTop: {
            marginTop: '100px',
        },
        submit: {
            background: 'linear-gradient(45deg, #FF69B4 35%, #C71585 90%)',
            border: 0,
            color: "#ffffff",
            borderRadius: 3,
            boxShadow: '0 3px 5px 2px rgba(255, 105, 135, .3)',
            height: 48,
            marginTop: 30,
        },
    }
));

