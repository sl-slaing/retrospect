import React, { useState } from 'react';
import { connect } from "react-redux";

import { Post, Delete } from './rest'; 
import { showAvatarMenu } from './redux/sessionActions';
import { updateObservation, deleteObservation } from './redux/retrospectiveActions';

const Observation = ({ tenant, observation, retrospectiveId, copyAction, copyText, marker, markerTitle, showAvatarMenu, updateObservation, deleteObservation }) => {
    const [ updating, setUpdating ] = useState(false);
    const [ editing, setEditing ] = useState(false);
    const [ displayHeight, setDisplayHeight ] = useState(0);
    const [ title, setTitle ] = useState(observation.title);
    const KEY_CODE_ENTER = 13;
    const KEY_CODE_ESCAPE = 27;

    const toggleVotes = (e) => {
        e.preventDefault();
        showAvatarMenu(false);

        if (updating) {
            return;
        }

        setUpdating(true);
        Post(tenant, '/observation/vote',
            {
                retrospectiveId: retrospectiveId,
                observationId: observation.id,
                observationType: observation.type
            })
            .then(
                observationJson => {
                    setUpdating(false);
                    updateObservation(observationJson);
                },
                err => {
                    alert("Could not apply vote: " + err);
                });
    }

    const className = () => {
        let className = "votes ";

        if (observation.hasVoted) {
            className += "voted down-vote ";
        } else {
            className += "up-vote ";
        }

        return updating 
            ? className + "disabled" 
            : className;
    }

    const onStartEditing = (e) => {
        if (updating) {
            return;
        }

        setDisplayHeight(e.currentTarget.offsetHeight);

        e.preventDefault(true);
        showAvatarMenu(false);
        setEditing(true);
    }

    const onTitleChange = (e) => {
        setTitle(e.currentTarget.value);
    }

    const onTitleChangeComplete = (e) => {
        e.preventDefault(true);
        setUpdating(true);

        Post(tenant, '/observation/update',
            {
                retrospectiveId: retrospectiveId,
                observationId: observation.id,
                observationType: observation.type,
                title: title.trim()
            })
            .then(
                observationJson => {
                    setTitle(title.trim());
                    setEditing(false);
                    setUpdating(false);
                    updateObservation(observationJson);
                },
                err => {
                    alert("Could not update observation: " + err);
                });
    }

    const confirmDelete = (e) => {
        e.preventDefault();

        if (editing || updating || !confirm("Are you sure you want to delete this observation?")) {
            return;
        }

        setUpdating(true);
        Delete(tenant, '/observation',
            {
                retrospectiveId: retrospectiveId,
                observationId: observation.id,
                observationType: observation.type
            })
            .then(
                () => {
                    setEditing(false);
                    setUpdating(false);
                    deleteObservation(observation.id, observation.type);
                },
                err => {
                    alert("Could not delete observation: " + err);
                });
    }

    const onKeyUp = (e) => {
        if (e.keyCode === KEY_CODE_ENTER) {
            e.preventDefault();
            onTitleChangeComplete(e);
        } else if (e.keyCode === KEY_CODE_ESCAPE){
            setEditing(false);
            setTitle(observation.title);
        }
    }

    const onCopy = (e) => {
        e.preventDefault();

        copyAction(observation);
    }

    const titleDisplay = editing 
        ? <textarea className="observation-title font-reset space-for-delete" autoFocus onChange={onTitleChange} style={{height: displayHeight + 'px'}} onKeyUp={onKeyUp} onBlur={onTitleChangeComplete} value={title} />
        : <a className="observation-title space-for-delete" onClick={onStartEditing}>{title}</a>

    return (<div className="observation space-for-vote">
                {titleDisplay}
                <span className={className()} onClick={toggleVotes}>{observation.votes}</span>
                { marker ? (<div className="bottom-right marker" title={markerTitle}>{marker}</div>) : null }
                <div className="bottom-left">
                    {(updating) ? null : <a className="delete" onClick={confirmDelete}>Delete</a>}
                    {(!copyAction) ? null : <a className="copy" onClick={onCopy}>{copyText}</a>}
                </div>
            </div>);
}

const mapStateToProps = (state) => {
    return {
        retrospectiveId: state.retrospective.id,
        tenant: state.session.selectedTenant
    }
}

export default connect(mapStateToProps, { showAvatarMenu, updateObservation, deleteObservation })(Observation);