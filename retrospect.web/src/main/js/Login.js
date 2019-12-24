import React, {useState, useEffect} from 'react';
import { connect } from "react-redux";

import { Get } from './rest';
import { login } from "./redux/sessionActions";
import { rememberDocumentHash } from './helpers';

import Working from './Working';

const Login = ({ login }) => {
    const [loginProviders, setLoginProviders] = useState(null);
    const [ error, setError ] = useState(null);

    if (error) {
        return (<Error error={error} />);
    }
    
    useEffect(() => {
        if (!loginProviders) {
            Get('/loginProviders')
                .then(
                    entity => {
                        if (entity.loggedInUser){
                            login(entity.loggedInUser)
                        } else {
                            setLoginProviders(entity.loginProviders);
                            rememberDocumentHash();
                        }
                    },
                    err => setError(err));
        }
    });

    if (!loginProviders){
        return (<Working />);
    }

    const loginOptions = loginProviders.map(loginProvider => (
        <a key={loginProvider.loginPath} href={loginProvider.loginPath} className={'login-with ' + loginProvider.className}>
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

export default connect(null, { login })(Login);