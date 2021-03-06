import React from 'react';
import { connect } from 'react-redux';

import { MANAGE_RETROSPECTIVES, EDIT_RETROSPECTIVE, ADMINISTER_RETROSPECTIVE, ADMINISTER_SYSTEM, MANAGE_TENANTS } from './redux/uiModes';
import { setActiveControl } from './redux/sessionActions';
import { defineHelperFunctions } from './helpers';

import AdministerRetrospective from './AdministerRetrospective';
import Avatar from './Avatar';
import ErrorBoundary from './ErrorBoundary';
import Heading from './Heading';
import LoadRetrospective from './LoadRetrospective';
import LoadRetrospectives from './LoadRetrospectives';
import Login from './Login';
import ManageTenants from './ManageTenants';
import Retrospectives from './Retrospectives';
import Retrospective from './Retrospective';
import SystemAdministration from './SystemAdministration';

const App = ({ displayMode, loggedInUser, setActiveControl, selectedTenant }) => {
	const getComponent = () => {
		if (!loggedInUser){
			return (<Login />);
		}

		if (!selectedTenant) {
			return (<ManageTenants />);
		}

		switch (displayMode){
			case EDIT_RETROSPECTIVE: {
				return (<LoadRetrospective>
							<Retrospective />
						</LoadRetrospective>);
			}
			case MANAGE_RETROSPECTIVES: {
				return (<LoadRetrospectives>
							<Retrospectives />
						</LoadRetrospectives>);
			}
			case ADMINISTER_RETROSPECTIVE: {
				return (<LoadRetrospective>
							<AdministerRetrospective />
						</LoadRetrospective>);
			}
			case ADMINISTER_SYSTEM: {
				return (<SystemAdministration />);
			}
			case MANAGE_TENANTS: {
				return (<ManageTenants />);
			}
			default: 
				throw { message: "Unknown display mode: " + displayMode };
		}
	}

	const unsetActiveControl = (e) => {
		setActiveControl(null);
	}

	defineHelperFunctions();

	return (<ErrorBoundary>
				<div className="absolute-fill flex-column">
					<Heading />
					<Avatar />
					<div className="workspace relative flex-grow" onClick={unsetActiveControl}>
						{getComponent()}
					</div>
				</div>
			</ErrorBoundary>);
}

const mapStateToProps = (state) => {
	return {
		loggedInUser: state.session.loggedInUser,
		displayMode: state.session.displayMode,
		selectedTenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { setActiveControl })(App);