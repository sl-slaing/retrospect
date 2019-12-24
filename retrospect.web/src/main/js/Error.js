import React from 'react';
import { connect } from 'react-redux';

const Error = ({ error, recover, recoverText }) => {
    if (error) {
        console.error(error);
    }

    const getMessage = () => {
        if (error) {
            return error.message
                ? error.message
                : error;
        }

        return "Unknown error";
    }

    const getRecoverOption = () => {
        return (<li><a className="clickable button" onClick={recover}>{recoverText || 'Try to recover...'}</a></li>)
    }

    return (<div className="absolute-fill flex-column">
                <div className="workspace relative flex-grow">
                    <div className="vertically-centered">
                        <div className="white-panel error-panel">
                            <h4>Error...</h4>
                            <div className="pre-text white-panel">
                                { getMessage() }
                            </div>
                            <div className="green-top-border large-top-margin">You have the following options:</div>
                            <ul>
                                {recover ? getRecoverOption() : null}
                                <li><a className="clickable button" href="/">Reload application</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>);
}

export default connect()(Error);