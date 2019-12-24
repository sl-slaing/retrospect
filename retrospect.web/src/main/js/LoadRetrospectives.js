import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { Get } from './rest';
import { setRetrospectives } from "./redux/retrospectivesActions";

import Error from './Error';
import Working from './Working';

const LoadRetrospectives = ({ children, retrospectives, setRetrospectives }) => {
   const getInitialState = (retrospectives) => {
        if (!retrospectives || Object.keys(retrospectives).length === 0) {
            return "unloaded"; //start getting the retros
        }

        return "loaded";
    }

    const [ condition, setCondition ] = useState(getInitialState(retrospectives));
    const [ error, setError ] = useState(null);

    const loadRetrospectives = () => {
        Get('/retrospective')
            .then(
                overviews => {
                    setRetrospectives(overviews);
                    setCondition("loaded");
                },
                err => {
                    setError(err);
                });
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
		retrospectives: state.retrospectives
	}
}

export default connect(mapStateToProps, { setRetrospectives })(LoadRetrospectives);