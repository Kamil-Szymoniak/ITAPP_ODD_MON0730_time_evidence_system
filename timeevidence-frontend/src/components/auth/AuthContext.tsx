import React, {ComponentType, useContext} from 'react';
import {UserResponse} from "../../dto/dto";
import {RouteComponentProps, withRouter} from "react-router";
import {getLoggedUserData} from "../../endpoints/auth";
import {LOGIN, TIME_EVIDENCE} from "../../routeNames";
import {loginUser as loginRequest, logout as logoutRequest} from "../../endpoints/auth";
import ConfirmationDialog from "../shared/ConfirmationDialog";
import {getExceptionMessage} from "../../util/getMessage";
import {toast} from "react-toastify";


export type AuthContextProps = {
    user: UserResponse;
    login: (login: string, password: string) => Promise<any>;
    askForLogout: any;
    logout: any;
    forceLogout: any;
    reloadUserData: any;
};

type Props = {} & RouteComponentProps;

const AuthContext = React.createContext<AuthContextProps>(null!);

type State = {
    showSplashScreen: boolean;
    logoutDialogVisible: boolean;
    user: UserResponse | {
        authorities: [];
    };
}

class AuthProvider extends React.Component<Props, State> {
    constructor(props: Props) {
        super(props);

        this.state = {
            showSplashScreen: true,
            logoutDialogVisible: false,
            user: {
                authorities: [],
            },
        };
    }

    componentDidMount() {
        this.reloadUserData();
    }

    componentDidUpdate(prevProps: Props) {
        const {location: nextLocation} = this.props;
        const {location: prevLocation} = prevProps;

        const didLocationChange = prevLocation.pathname !== nextLocation.pathname;

        if (didLocationChange) {
            const isAuthorized = (this.state.user as UserResponse).username !== undefined;

            // when the user enters a path that require being loggesd in while being logged out
            if (!isAuthorized && !this.isPathAnonymous(nextLocation.pathname)) {
                //TODO add toast
                const urlParams = new URLSearchParams();
                urlParams.append('origin', nextLocation.pathname);
                this.redirectToLoginPage(urlParams);
            }

            // when the user enters a path that does not require logging in while being logged in
            if (isAuthorized && this.isPathAnonymous(nextLocation.pathname)) {
                this.redirectToMainPage();
            }
        }
    }

    getAnonymousPathNames() {
        return [
            LOGIN,
        ];
    }

    /**
     * Checks if entering the given path should require being logged in.
     */
    isPathAnonymous = (pathname: string) => this.getAnonymousPathNames().includes(pathname);

    /**
     * It realizes the following flow:
     * - We download user data (endpoint / api / users / me) and set them in the context of the application
     * - If we do not have permission (e.g. session has expired), then:
     * - If this is not a login / password reset page, it means user
     * tried to display something, but there is no token:
     * - We display a message to them that he needs to log in again
     * - We redirect to the login page
     */
    reloadUserData = () => {
        const {pathname} = this.props.location;
        getLoggedUserData()
            .then(this.setUserDataFromResponse)
            .then(this.hideSplashScreen)
            .then(() => {
                if (this.isPathAnonymous(pathname)) {
                    this.redirectToMainPage();
                }
            })
            .catch((r) => {
                if (r.status === 401) {
                    if (!this.isPathAnonymous(pathname)) {
//          TODO: toast with info for user
                        console.log("Log in again")
                        const urlParams = new URLSearchParams();
                        urlParams.append('origin', pathname);
                        this.redirectToLoginPage(urlParams);
                    }
                    this.hideSplashScreen();
                }
            });
    }

    login = (login: string, password: string) => loginRequest({
        login,
        password,
    })
        .then(getLoggedUserData)
        .then(this.setUserDataFromResponse)
        .then(this.hideSplashScreen)
        .then(this.redirectToMainPage)
        .then(() => toast.success("User logged in successfully"))
        .catch(async e => {
            const error = await getExceptionMessage(e)
            toast.error(error)
        })

    logout = async () => {
        logoutRequest()
            .then(() => {
                this.setState({
                    showSplashScreen: false,
                    user: {
                        authorities: [],
                    },
                    logoutDialogVisible: false
                });
                this.redirectToLoginPage();
            });
    };

    forceLogout = () => {
        console.log("Your session has expired, please login again")
        this.logout();
        // TODO: force logout, toast with info for user
    };

    setUserDataFromResponse = (response: UserResponse) => {
        this.setState({user: {...response}});
        return {
            user: {...response},
        };
    };

    redirectToMainPage = () => {
        const urlParam = new URLSearchParams(this.props.location.search);
        const param = urlParam.get('origin');
        if (param != null) {
            this.props.history.replace(param);
        } else {
            this.props.history.replace(TIME_EVIDENCE);
        }
    };

    redirectToLoginPage = (urlParams?: URLSearchParams) => {
        if (urlParams != null) {
            this.props.history.replace(`${LOGIN}?${urlParams}`);
        } else {
            this.props.history.replace(LOGIN);
        }
    };

    hideSplashScreen = () => {
        this.setState({
            showSplashScreen: false,
        });
    };

    hideLogoutDialog = () => {
        this.setState({
            logoutDialogVisible: false,
        });
    }

    showLogoutDialog = () => {
        this.setState({
            logoutDialogVisible: true,
        });
    }

    onLogoutDialogConfirm = async () => {
        await this.logout();
    }

    render() {
        const {children} = this.props;

        return (
            <AuthContext.Provider value={{
                user: this.state.user as UserResponse,
                login: this.login,
                askForLogout: this.showLogoutDialog,
                logout: this.logout,
                forceLogout: this.forceLogout,
                reloadUserData: this.reloadUserData,
            }}
            >
                <ConfirmationDialog
                    dialogContent={"Are you sure you want to log out?"}
                    isDialogOpen={this.state.logoutDialogVisible}
                    onConfirm={this.onLogoutDialogConfirm}
                    onHide={this.hideLogoutDialog}
                    onCancel={this.hideLogoutDialog}
                    dialogTitle={"Log out"}
                    confirmText={"Logout"}
                    cancelText={"Cancel"}
                />
                {children}
            </AuthContext.Provider>
        )
    }
}


const AuthProviderComponent = withRouter(AuthProvider)

const AuthConsumer = AuthContext.Consumer;

// eslint-disable-next-line max-len
function withAuthContext<T>(Component: React.ComponentType<AuthContextProps & T>): ComponentType<T> {
    return (props) => (
        <AuthConsumer>
            {
            ({
                user,
                login,
                askForLogout,
                logout,
                forceLogout,
                reloadUserData,
            }) => (
        <Component
            {...props}
    user={user}
    askForLogout={askForLogout}
    login={login}
    logout={logout}
    forceLogout={forceLogout}
    reloadUserData={reloadUserData}
    />
)
}
    </AuthConsumer>
);
}

function useAuthContext() {
    return useContext(AuthContext);
}

export {AuthContext, AuthProviderComponent as AuthProvider, withAuthContext, AuthConsumer, useAuthContext}