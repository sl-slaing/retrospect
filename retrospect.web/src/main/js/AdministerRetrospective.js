import React, { useState } from 'react';
import { connect } from 'react-redux';

import { Get, Post, Delete } from './rest'; 
import { EDIT_RETROSPECTIVE, MANAGE_RETROSPECTIVES } from './redux/uiModes';
import { switchUiMode, showAvatarMenu } from './redux/sessionActions';
import { setRetrospectiveById } from './redux/retrospectiveActions';
import { setRetrospectives, removeRetrospective } from './redux/retrospectivesActions';
import { removeFromDocumentHash, saveFile, openFile } from './helpers';

import Error from './Error';
import RetrospectiveSelection from './RetrospectiveSelection';
import UserSelection from './UserSelection';
import Working from './Working';

const AdministerRetrospective = ({ tenant, retrospective, loggedInUser, retrospectives, switchUiMode, setRetrospectiveById, setRetrospectives, removeRetrospective, showAvatarMenu }) => {
    const getInitialPreviousRetrospectiveSelection = () => {
        const previousRetrospectiveId = retrospective.previousRetrospectiveId;

        if (!previousRetrospectiveId) {
            return {};
        }

        let selection = { };
        selection[previousRetrospectiveId] = {
            id: previousRetrospectiveId,
            readableId: retrospective.previousRetrospectiveReadableId,
            createdOn: retrospective.previousRetrospectiveCreatedOn
        };

        return selection;
    }

    const [ readableId, setReadableId ] = useState(retrospective.readableId);
    const [ previousRetrospectiveId, setPreviousRetrospectiveId ] = useState(getInitialPreviousRetrospectiveSelection());
    const [ administrators, setAdministrators ] = useState(retrospective.administrators);
    const [ members, setMembers ] = useState(retrospective.members);
    const [ mode, setMode ] = useState("edit");
    const [ error, setError ] = useState(null);

    const getReadableIdClassName = () => {
        if (retrospectives === null) {
            loadRetrospectives();
        }

        if (readableId === '') {
            return 'validation-error';
        }
        
        const reserved = Object.values(retrospectives || {})
            .filter(r => r.id !== retrospective.id)
            .filter(r => r.readableId === readableId).length > 0;
        return reserved ? 'validation-error' : null;
    }

    const loadRetrospectives = () => {
        Get(tenant, '/retrospective')
            .then(
                overviews => {
                    setRetrospectives(overviews);
                });
    }

    const updateReadableId = (e) => {
        e.preventDefault();
        setReadableId(e.currentTarget.value);
    }

    const saveChanges = (e) => {
        const retrospectiveDetails = {
            id: retrospective.id,
            readableId: readableId,
            previousRetrospectiveId: Object.keys(previousRetrospectiveId).length === 0 ? null : Object.keys(previousRetrospectiveId)[0],
            members: Object.keys(members),
            administrators: Object.keys(administrators)
        };

        if (getReadableIdClassName() !== null || retrospectiveDetails.administrators.length === 0){
            alert('Cannot save, the readable-id must be populated and unique and there must be at least one administrator');
            return;
        }

        setMode("saving");

        Post(tenant, '/retrospective/administration',
            retrospectiveDetails)
            .then(response => {
                setRetrospectiveById(retrospective.id, true); //to invalidate the cached data for this retrospective
                setRetrospectives(null);
                switchUiMode(EDIT_RETROSPECTIVE);
            },
            err => {
                if (err.response) {
                    err.response.text()
                        .then(
                            message => {
                                setError("Unable to save: " + message);
                            },
                            parseError => setError(parseError));
                } else {
                    setError(err);
                }
            })

    }

    const returnToRetrospective = (e) => {
        switchUiMode(EDIT_RETROSPECTIVE);
    }

    const deleteRetrospective = (e) => {
        e.preventDefault();
        showAvatarMenu(false);

        if (!confirm("Are you sure you want to delete this retrospective?")){
            return;
        }

        Delete(tenant, '/retrospective',
            {
                id: retrospective.id
            })
            .then(
                () => {
                    removeFromDocumentHash("mode");
                    removeRetrospective(retrospective.id);
                    switchUiMode(MANAGE_RETROSPECTIVES);
                },
                err => {
                    setError(err);
                });
    }

    const recover = (e) => {
        setMode("edit");
        setError(null);
    }

    const exportRetrospective = (e) => {
        e.preventDefault(e);
        setMode("exporting");

        Post(tenant, '/export',
            {
                ids: [ retrospective.id ],
                version: '1.0',
                settings: {
                    includeDeleted: true
                }
            },
            true)
            .then(
                response => {
                    response.text().then(
                        text => {
                            saveFile(`${retrospective.readableId}.json`, text, 'text/plain');
                            setMode('edit');
                        },
                        err => {
                            setError(err);
                        });
                },
                err => {
                    setError(err);
                }
            )
    }

    if (error !== null) {
        return (<Error error={error} recover={recover} recoverText="Review settings..." />);
    }

    if (mode === "saving") {
        return (<Working message="Saving changes..." />);
    }

    if (mode === "exporting") {
        return (<Working message="Exporting data..." />);
    }

	return (<div className="vertically-centered visible-overflow">
                <div className="white-panel">
                    <h3>Administration</h3>
                    <div className="tabular-content">
                        <div className="tabular-row">
                            <div className="tabular-cell">Id</div>
                            <div className="tabular-cell" title={retrospective.id}>
                                <input className={getReadableIdClassName()} value={readableId} onChange={updateReadableId} />
                            </div>
                        </div>
                        <div className="tabular-row">
                            <div className="tabular-cell">Prev. retrospective</div>
                            <div className="tabular-cell" title={previousRetrospectiveId}>
                                <RetrospectiveSelection currentRetrospectives={previousRetrospectiveId} setCurrentRetrospectives={setPreviousRetrospectiveId} controlId="previousRetrospectiveId" maxRetrospectives="1" />
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
                        <a className="button clickable" onClick={saveChanges}>Save changes</a>
                        <a className="button clickable" onClick={deleteRetrospective}>Delete retrospective</a>
                        <a className="button clickable" onClick={returnToRetrospective}>Return to retrospective</a>
                        <a className="button clickable" onClick={exportRetrospective}>Export</a>
                    </div>
                </div>
            </div>);
}

const mapStateToProps = (state) => {
	return {
        retrospective: state.retrospective,
        retrospectives: state.retrospectives,
        loggedInUser: state.session.loggedInUser,
        tenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { switchUiMode, setRetrospectiveById, setRetrospectives, removeRetrospective, showAvatarMenu })(AdministerRetrospective);
