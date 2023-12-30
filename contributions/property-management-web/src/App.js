import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import { useAuth0 } from "@auth0/auth0-react";
import configData from "./config.json";

const App = () => {
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(false);
    const { isLoading, error, isAuthenticated, user, getAccessTokenSilently, loginWithRedirect } = useAuth0();
    const [accessToken, setAccessToken] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const token = await getAccessTokenSilently({
                    domain: configData.domain,
                    clientId: configData.clientId,
                    useRefreshTokensFallback: true,
                    cacheLocation: "localstorage",
                    authorizationParams: {
                        redirect_uri: window.location.origin,
                        audience: configData.audience,
                        scope: configData.scope,
                        useRefreshTokensFallback: true
                    }
                });

                setAccessToken(token);

                const response = await fetch("api/v1/companies", {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }

                const data = await response.json();
                setCompanies(data);
                setLoading(false);
                console.log(data);
            } catch (e) {
                console.error("Error:", e.message);
                setLoading(false);
            }
        };

        if (isAuthenticated) {
            fetchData();
        }
    }, [getAccessTokenSilently, isAuthenticated]);

    if (error) {
        return <div>Oops... {error.message}</div>;
    }

    if (isLoading || loading) {
        return <div>Loading...</div>;
    }

    if (!isAuthenticated) {
        return loginWithRedirect();
    }

    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo" />
                <div className="App-intro"></div>
                <p>Hi {user.email}, You have successfully logged in.</p>
                <h2>Test - Property Management List</h2>
                {companies.map(company => (
                    <div key={company.id}>
                        {company.id}
                        {company.name}
                    </div>
                ))}
            </header>
        </div>
    );
};

export default App;