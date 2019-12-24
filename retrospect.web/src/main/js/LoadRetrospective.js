import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { MANAGE_RETROSPECTIVES } from './redux/uiModes';
import { Get } from './rest';
import { setRetrospective } from './redux/retrospectiveActions';
import { switchUiMode } from './redux/sessionActions';

import Error from './Error';
import Working from './Working';

const LoadRetrospective = ({ children, retrospective, setRetrospective, switchUiMode }) => {
   const getInitialState = (retrospective) => {
        if (!retrospective.id) {
            return "unknown";
        }

        if (retrospective.loadById) {
            return "unloaded"; //start getting the retro
        }

        return "loaded";
    }

    const [ condition, setCondition ] = useState(getInitialState(retrospective));
    const [ error, setError ] = useState(null);

    const loadRetrospective = (id) => {
        Get('/retrospective/' + id)
            .then(
                viewModel => {
                    setRetrospective(viewModel);
                    setCondition("loaded");
                },
                err => {
                    if (err.response && err.response.status === 404) {
                        setCondition("not-found");
                        return;
                    }

                    setError(err);
                });
    }

    const backToList = (e) => {
        switchUiMode(MANAGE_RETROSPECTIVES);
    }

    useEffect(
        () => {
            if (retrospective.loadById){
                setCondition("unloaded");
                setError(null);
            }
        },
        [retrospective.id, retrospective.loadById]);

    useEffect(
        () => {
            if (condition === "unloaded") {
                loadRetrospective(retrospective.id);
            }
        },
        [condition]);

    if (error) {
        return (<Error error={error} />);
    }
    
    switch (condition) {
        case "unloaded": {
            return (<Working message="Loading retrospective..." />);
        }
        case "loaded": {
            return children;
        }
        case "not-found": {
            return (<div className="vertically-centered">
                        <div className="white-panel">
                            <h4>Not found...</h4>
                            <div>
                                Retrospective not found.
                            </div>
                            <div className="green-top-border large-top-margin">You have the following options:</div>
                            <ul>
                                <li><a className="clickable button" onClick={backToList}>Back to list of retrospectives</a></li>
                                <li><a className="clickable button" href="/">Reload application</a></li>
                            </ul>
                        </div>
                    </div>);
        }
        default: {
            return (<div>{condition} is unknown</div>);
        }
    }
}

const mapStateToProps = (state) => {
	return {
		retrospective: state.retrospective
	}
}

export default connect(mapStateToProps, { setRetrospective, switchUiMode })(LoadRetrospective);