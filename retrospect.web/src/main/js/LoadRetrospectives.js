import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { Get } from './rest';
import { setRetrospectives } from "./redux/retrospectivesActions";

import Error from './Error';
import Working from './Working';

const LoadRetrospectives = ({ tenant, children, retrospectives, setRetrospectives }) => {
   const getInitialState = (retrospectives) => {
        if (!retrospectives || Object.keys(retrospectives).length === 0) {
            return "unloaded"; //start getting the retros
        }

        return "loaded";
    }

    const [ condition, setCondition ] = useState(getInitialState(retrospectives));
    const [ error, setError ] = useState(null);

    const loadRetrospectives = () => {
        Get(tenant, '/retrospective')
            .then(
                overviews => {
                    setRetrospectives(overviews);
                    setCondition("loaded");
                },
                setError);
    }

    useEffect(
        () => {
            if (condition === "unloaded") {
                loadRetrospectives();
            }
        },
        [condition]);

    if (error) {
        return (<Error error={error} />);
    }
    
    switch (condition) {
        case "unloaded": {
            return (<Working message="Loading retrospectives..." />);
        }
        case "loaded": {
            return children;
        }
        default: {
            return (<div>{condition} is unknown</div>);
        }
    }
}

const mapStateToProps = (state) => {
	return {
        retrospectives: state.retrospectives,
        tenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { setRetrospectives })(LoadRetrospectives);