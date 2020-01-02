import React, {useState, useEffect} from 'react';
import { connect } from "react-redux";

import { Get } from './rest';
import { login } from "./redux/sessionActions";
import { rememberDocumentHash } from './helpers';

import Error from './Error';
import Working from './Working';

const Login = ({ tenant, login }) => {
    const [ loginProviders, setLoginProviders ] = useState(null);
    const [ error, setError ] = useState(null);
    const [ mode, setMode ] = useState("loading");

    useEffect(() => {
            if (!loginProviders) {
                Get(tenant, '/loginProviders')
                    .then(
                        entity => {
                            if (entity.loggedInUser){
                                login(entity.loggedInUser, entity.showSystemAdministration, entity.tenantsForLoggedInUser);
                            } else {
                                setLoginProviders(entity.loginProviders);
                                rememberDocumentHash();
                                setMode("select-provider");
                            }
                        },
                        setError);
            }
        }, 
        [ loginProviders ]);

    const useLoginProvider = (e) => {
        setMode("logging-in");
        document.location.href = e.currentTarget.href;
    }

    if (error) {
        return (<Error error={error} />);
    }

    if (mode === "logging-in"){
        return (<Working message="Logging in..." />);
    }
    
    if (mode === "loading"){
        return (<Working />);
    }

    const loginOptions = loginProviders.map(loginProvider => (
        <a key={loginProvider.loginPath} href={loginProvider.loginPath} className={'list-item ' + loginProvider.className} onClick={useLoginProvider}>
            {loginProvider.displayName}
        </a>)
    );

    return (<div className="vertically-centered">
                <div className="white-panel">
                    <h4 className="center">Login with:</h4>
                    <div className="list">
                        {loginOptions}
                    </div>
                </div>
            </div>);
}

const mapStateToProps = (state) => {
    return {
        tenant: state.session.selectedTenant
    }
}

export default connect(mapStateToProps, { login })(Login);