import React, { useState } from 'react';
import { connect } from "react-redux";

import { Post } from './rest'; 
import { showAvatarMenu, setContinueEditing } from './redux/sessionActions';
import { addObservation } from './redux/retrospectiveActions';

const NewObservation = ({ tenant, retrospectiveId, observationType, showAvatarMenu, continueEditing, setContinueEditing, addObservation }) => {
    const [ updating, setUpdating ] = useState(false);
    const [ editing, setEditing ] = useState(continueEditing === observationType);
    const [ title, setTitle ] = useState("");
    const KEY_CODE_ENTER = 13;
    const KEY_CODE_ESCAPE = 27;

    const onStartEditing = (e) => {
        if (updating) {
            return;
        }

        e.preventDefault(true);
        setEditing(true);
        showAvatarMenu(false);
    }

    const onTitleChange = (e) => {
        setTitle(e.currentTarget.value);
    }

    const onTitleChangeComplete = (e) => {
        e.preventDefault(true);

        if (title.trim() === "") {
            setEditing(false);
            setContinueEditing(null);
            continueEditing = null;
            return;
        }

        setUpdating(true);
        Post(tenant, '/observation/create',
            {
                retrospectiveId: retrospectiveId,
                observationType: observationType,
                title: title.trim()
            })
            .then(
                observationJson => {
                    if (continueEditing !== observationType) {
                        setEditing(false);
                    }
                    setUpdating(false);
                    setTitle("");
                    addObservation(observationJson);
                },
                err => {
                    alert("Could not create observation: " + err);
                });
    }

    const onKeyUp = (e) => {
        if (e.keyCode === KEY_CODE_ENTER) {
            e.preventDefault();
            setContinueEditing(observationType);
            continueEditing = observationType;
            onTitleChangeComplete(e);
        } else if (e.keyCode === KEY_CODE_ESCAPE){
            setEditing(false);
            setContinueEditing(null);
            continueEditing = null;
        }
    }

    const display = editing && !updating
        ? <textarea className="observation-title font-reset" autoFocus onChange={onTitleChange} onBlur={onTitleChangeComplete} onKeyUp={onKeyUp} value={title} />
        : <a className="clickable no-underline" onClick={onStartEditing}>+</a>

    return (<div className="observation bold-content extra-large-font center">
        {display}
    </div>);
}

const mapStateToProps = (state) => {
    return {
        continueEditing: state.session.continueEditing,
        retrospectiveId: state.retrospective.id,
        tenant: state.session.selectedTenant
    }
}

export default connect(mapStateToProps, { showAvatarMenu, setContinueEditing, addObservation })(NewObservation);