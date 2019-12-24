import React, { useState } from 'react';
import { connect } from "react-redux";

import { Post, Get } from './rest'; 
import { EDIT_RETROSPECTIVE } from './redux/uiModes';
import { switchUiMode } from "./redux/sessionActions";
import { setRetrospectiveById } from "./redux/retrospectiveActions";
import { addRetrospective } from "./redux/retrospectivesActions";

import Error from './Error';
import RetrospectiveSelection from './RetrospectiveSelection';
import UserSelection from './UserSelection';
import Working from './Working';
import { setDocumentHash } from './helpers';

const Retrospectives = ({ loggedInUser, retrospectives, addRetrospective, setRetrospectiveById, switchUiMode }) => {
    const getInitialAdministrators = () => {
        let administrators = {};

        administrators[loggedInUser.username] = loggedInUser;

        return administrators;
    }

    const [ error, setError ] = useState(null);
    const [ mode, setMode ] = useState("view");
    const [ previousRetrospectiveId, setPreviousRetrospectiveId ] = useState({});
    const [ readableId, setReadableId ] = useState("");
    const [ administrators, setAdministrators ] = useState(getInitialAdministrators());
    const [ members, setMembers ] = useState({});

    if (error) {
        return (<Error error={error} />);
    }

    const getReadableIdClassName = () => {
        const reserved = Object.values(retrospectives || {})
            .filter(r => r.readableId === readableId).length > 0;
        return reserved ? 'validation-error' : null;
    }

    const onPreviousRetrospectiveChanged = (previousRetrospectiveId) => {
        setPreviousRetrospectiveId(previousRetrospectiveId);

        const id = Object.keys(previousRetrospectiveId || {}).length >= 1
            ? Object.keys(previousRetrospectiveId)[0]
            : null;

        if (id === null){
            return;
        }

        Get('/retrospective/' + id)
            .then(viewModel => {
                const copyOfMembers = viewModel.members;
                const copyOfAdministrators = viewModel.administrators;
                if (!copyOfAdministrators[loggedInUser.username]) {
                    copyOfAdministrators[loggedInUser.username] = loggedInUser;
                }
        
                setMembers(copyOfMembers);
                setAdministrators(copyOfAdministrators);        
            });
    }

    const updateReadableId = (e) => {
        e.preventDefault();
        setReadableId(e.currentTarget.value);
    }

    const onSelectRetrospective = (e) => {
        try {
            e.preventDefault();

            const id = e.currentTarget.getAttribute("data-id");
            setRetrospectiveById(id);
            switchUiMode(EDIT_RETROSPECTIVE);
        } catch (e) {
            setError(e);
        }
    }

    const createRetrospective = (e) => {
        if (getReadableIdClassName() != null) {
            alert('Cannot save, the provided readable-id is already in use.');
            return;
        }

        setMode("creating");

        Post('/retrospective/create',
            {
                previousRetrospectiveId: Object.keys(previousRetrospectiveId).length === 0 ? null : Object.keys(previousRetrospectiveId)[0],
                readableId: readableId,
                members: Object.keys(members),
                administrators: Object.keys(administrators)
            })
            .then(
                newOverview => {
                    addRetrospective(newOverview);
                    setRetrospectiveById(newOverview.id);
                    setDocumentHash({ mode: "REVIEW_PREVIOUS_ACTIONS" });
                    switchUiMode(EDIT_RETROSPECTIVE);
                },
                err => {
                    setError(err)
                });
    }

    const onAddRetrospective = (e) => {
        try {
            e.preventDefault();

            setPreviousRetrospectiveId({});
            setMode("add");            
        } catch (e) {
            setError(e);
        }
    }

    const onCancel = (e) => {
        try {
            e.preventDefault();
            setMode("view");
        } catch (e) {
            setError(e);
        }
    }

    const retrospectiveLinks = Object.values(retrospectives).orderBy("sortIdentifier").map(retrospective =>
        <a key={retrospective.id} data-id={retrospective.id} href={'#' + retrospective.id} className="block-flow-item hover-shadow bold-content" onClick={onSelectRetrospective}>
            <div>{retrospective.readableId}</div>
            <div>{retrospective.createdOn}</div>
            <div className="ignore-whitespace">
                <span className="positive">{retrospective.wentWellCount}</span>
                <span className="negative">{retrospective.couldBeBetterCount}</span>
                <span className="result">{retrospective.actionCount}</span>
            </div>
        </a>
    );

    const createLink = (
        <a onClick={onAddRetrospective} href="#new" className="block-flow-item dark hover-shadow bold-content extra-large-font">
            <span>+</span>
        </a>);

    switch (mode) {
        case "view": {
            return (<div className="vertically-centered">
                        {retrospectiveLinks}
                        { createLink }
                    </div>);
        }
        case "add": {
            return (<div className="vertically-centered visible-overflow">
                        <div className="white-panel">
                            <h3>Create new retrospective</h3>
                            <div className="tabular-content">
                                <div className="tabular-row">
                                    <div className="tabular-cell">Id</div>
                                    <div className="tabular-cell">
                                        <input className={getReadableIdClassName()} value={readableId} onChange={updateReadableId} />
                                        <div className="small-font">Optional: An id will be generated if not provided</div>
                                    </div>
                                </div>
                                <div className="tabular-row">
                                    <div className="tabular-cell">Prev. retrospective</div>
                                    <div className="tabular-cell">
                                        <RetrospectiveSelection currentRetrospectives={previousRetrospectiveId} setCurrentRetrospectives={onPreviousRetrospectiveChanged} controlId="previousRetrospectiveId" maxRetrospectives="1" />
                                        <div className="small-font">Optional: Link the new retrospective to a previous occurrence</div>
                                    </div>
                                </div>
                                <div className="tabular-row">
                                    <div className="tabular-cell">Administrators</div>
                                    <div className="tabular-cell">
                                        <UserSelection currentUsers={administrators || {}} controlId="administrators" setCurrentUsers={setAdministrators} requiredUsers={loggedInUser} userType="administrator" />
                                    </div>
                                </div>
                                <div className="tabular-row">
                                    <div className="tabular-cell">Members</div>
                                    <div className="tabular-cell">
                                        <UserSelection currentUsers={members || {}} setCurrentUsers={setMembers}  controlId="members" userType="member" />
                                    </div>
                                </div>
                            </div>
                            <div className="buttons green-top-border ">
                                <a className="button clickable" onClick={createRetrospective}>Create retrospective</a>
                                <a className="button clickable" onClick={onCancel}>Return to list</a>
                            </div>
                        </div>
                    </div>);
        }
        case "creating": {
            return (<Working message="Creating retrospective..." />);
        }
    }
}

const mapStateToProps = (state) => {
	return {
        retrospectives: state.retrospectives,
        loggedInUser: state.session.loggedInUser
	}
}

export default connect(mapStateToProps, { addRetrospective, setRetrospectiveById, switchUiMode })(Retrospectives);