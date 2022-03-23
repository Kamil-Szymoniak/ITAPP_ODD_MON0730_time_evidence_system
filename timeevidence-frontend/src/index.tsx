import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {HashRouter, Route, Switch} from 'react-router-dom';
import {AuthProvider} from "./components/auth/AuthContext";
import {LOGIN} from "./routeNames";
import Login from "./components/auth/Login";

const localStorageVersion = 'V2';

if (window.localStorage.getItem('VERSION') !== localStorageVersion) {
    window.localStorage.clear();
    window.sessionStorage.clear();
    window.localStorage.setItem('VERSION', localStorageVersion);
}

ReactDOM.render(
    <React.StrictMode>
        <HashRouter>
            <AuthProvider>
                <Switch>
                    <Route
                        path={LOGIN}
                        exact component={Login}
                    />
                    <App/>
                </Switch>
            </AuthProvider>
        </HashRouter>,
    </React.StrictMode>,
    document.getElementById('root')
);
// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
