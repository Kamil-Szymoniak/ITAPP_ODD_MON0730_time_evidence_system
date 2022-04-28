import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {BrowserRouter, Route, Switch} from 'react-router-dom';
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
    <BrowserRouter>
        <AuthProvider>
            <Switch>
                <Route
                    path={LOGIN}
                    exact component={Login}
                />
                <App/>
            </Switch>
        </AuthProvider>
    </BrowserRouter>,
    document.getElementById('root')
);
reportWebVitals();
