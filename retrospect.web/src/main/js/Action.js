import React, { useState, useEffect } from 'react';
import { connect } from "react-redux";

import { Post, Delete } from './rest'; 
import { showAvatarMenu } from './redux/sessionActions';
import { updateAction, deleteAction } from './redux/retrospectiveActions';

import UserSelection from './UserSelection';

const Action = ({ action, overrideRetrospectiveId, retrospectiveId, readonly, copyAction, copyText, marker, markerTitle, allowCompletion, showAvatarMenu, updateAction, deleteAction }) => {
    const [ updating, setUpdating ] = useState(false);
    const [ editing, setEditing ] = useState(false);
    const [ displayHeight, setDisplayHeight ] = useState(0);
    const [ title, setTitle ] = useState(action.title || '');
    const [ ticketAddress, setTicketAddress ] = useState(action.ticketAddress || '');
    const [ complete, setComplete ] = useState(action.complete);
    const [ assignedTo, setAssignedTo ] = useState(action.assignedTo);
    const [ saveRequired, setSaveRequired ] = useState(false);

    const onStartEditing = (e) => {
        if (updating || readonly) {
            return;
        }

        setDisplayHeight(e.currentTarget.offsetHeight);

        e.preventDefault(true);
        showAvatarMenu(false);
        setEditing(true);
    }

    const onTextualChangeComplete = (e) => {
        setTitle(title.trim());
        setSaveRequired(true);
    }

    const onTitleChange = (e) => {
        e.preventDefault();
        setTitle(e.currentTarget.value);
    }

    const onTicketAddressChange = (e) => {
        e.preventDefault();
        setTicketAddress(e.currentTarget.value);
    }

    const onCompleteChanged = (e) => {
        e.preventDefault();
        setComplete(e.currentTarget.checked);
        setSaveRequired(true);
    }

    useEffect(() => {
        if (saveRequired) {
            saveChanges();
        }
    },
    [saveRequired]);

    const saveChanges = () => {
        setUpdating(true);

        Post('/action/update',
            {
                retrospectiveId: overrideRetrospectiveId || retrospectiveId,
                actionId: action.id,
                title: title.trim(),
                ticketAddress: ticketAddress,
                assignedToUsername: assignedTo ? assignedTo.username : null,
                complete: complete,
                fromObservationId: action.fromObservationId,
                fromActionId: action.fromActionId
            })
            .then(
                actionJson => {
                    setEditing(false);
                    setUpdating(false);
                    setSaveRequired(false);
                    updateAction(actionJson);
                },
                err => {
                    alert("Could not update action: " + err);
                });
    }

    const confirmDelete = (e) => {
        e.preventDefault();

        if (editing || updating || !confirm("Are you sure you want to delete this action?")) {
            return;
        }

        setUpdating(true);
        Delete('/action',
            {
                retrospectiveId: overrideRetrospectiveId || retrospectiveId,
                actionId: action.id
            })
            .then(
                () => {
                    setEditing(false);
                    setUpdating(false);
                    deleteAction(action.id);
                },
                err => {
                    alert("Could not delete action: " + err);
                });
    }

    const onCopy = (e) => {
        e.preventDefault();

        copyAction(action);
    }

    const currentUsers = {};
    if (assignedTo){
        currentUsers[assignedTo.username] = assignedTo;
    }

    const setCurrentUsers = (users) => {
        const user = Object.values(users).length > 0 ? Object.values(users)[0]: null;

        setAssignedTo(user);
        setSaveRequired(true);
    }

    const titleDisplay = editing 
        ? <textarea className="action-title font-reset" autoFocus onChange={onTitleChange} style={{height: displayHeight + 'px'}} onBlur={onTextualChangeComplete} value={title} />
        : <a className="action-title" onClick={onStartEditing}>{title}</a>

    const ticketAddressDisplay = readonly
        ? (ticketAddress ? (<label className="horizontal-margin horizontal-padding inter-border">
                                Ticket: 
                                <a className="horizontal-margin horizontal-padding inter-border clickable" target="_blank" href={ticketAddress}>
                                    {ticketAddress}
                                </a>
                            </label>) : null)
        : ( <label className="horizontal-margin horizontal-padding inter-border">
                Ticket:
                <input type="text" value={ticketAddress} onChange={onTicketAddressChange} onBlur={onTextualChangeComplete} placeholder="https://ticket-address" />
            </label>);

    const assignedToDisplay = readonly
        ? (assignedTo ? (<span className="collapsed-selection no-wrap">
                            <img className="selection-avatar" src={assignedTo.avatarUrl} />
                            {assignedTo.displayName}
                        </span>) : "Unassigned")
        : (<UserSelection currentUsers={currentUsers} setCurrentUsers={setCurrentUsers} userType="user" maxUsers="1" noSelectionText="Unassigned" />);

    return (<div className={'action' + (complete ? ' action-complete' : '')}>
                {titleDisplay}
                <div className={'action-details' + (readonly && !copyAction ? null : ' space-for-delete')}>
                    {ticketAddressDisplay}
                    { !readonly || allowCompletion ? (
                    <label className="horizontal-margin horizontal-padding inter-border">
                        Complete: <input type="checkbox" checked={complete} onChange={onCompleteChanged} />
                    </label>) : null }
                    <label className="horizontal-padding inline-drop-down inter-border">
                        Assigned to:
                        {assignedToDisplay}
                    </label>
                </div>
                { marker ? (<div className="bottom-right marker" title={markerTitle}>{marker}</div>) : null }
                {(updating || readonly) ? null : <a className="delete bottom-left" onClick={confirmDelete}>Delete</a>}
                {(!copyAction) ? null : <a className="copy bottom-left" onClick={onCopy}>{copyText}</a>}
            </div>);
}

const mapStateToProps = (state) => {
    return {
        retrospectiveId: state.retrospective.id
    }
}

export default connect(mapStateToProps, { showAvatarMenu, updateAction, deleteAction })(Action);