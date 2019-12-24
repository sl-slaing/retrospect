import React from 'react';
import { connect } from 'react-redux';

import { MANAGE_RETROSPECTIVES } from './redux/uiModes';
import { switchUiMode } from './redux/sessionActions'; 

const Heading = ({ displayMode, heading, loggedInUser, switchUiMode }) => {
    const isClickable = () => {
        return loggedInUser && displayMode !== MANAGE_RETROSPECTIVES;
    }

    const className = isClickable()
        ? "center heading clickable"
        : "center heading";

    const onHeadingClick = (e) => {
        if (isClickable()){
            e.preventDefault();
            switchUiMode(MANAGE_RETROSPECTIVES);
        }
    }

    return (<h3 className={className} onClick={onHeadingClick}>
                {heading}
            </h3>);
}

const mapStateToProps = (state) => {
    return {
        heading: state.session.heading,
        loggedInUser: state.session.loggedInUser,
        retrospective: state.retrospective,
        displayMode: state.session.displayMode
    }
}

export default connect(mapStateToProps, { switchUiMode })(Heading);