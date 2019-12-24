import React, { useState } from 'react';
import { connect } from "react-redux";

import { Post } from './rest'; 
import { showAvatarMenu, setContinueEditing } from './redux/sessionActions';
import { addAction } from './redux/retrospectiveActions';

const NewAction = ({ retrospectiveId, showAvatarMenu, continueEditing, setContinueEditing, addAction }) => {
    const [ updating, setUpdating ] = useState(false);
    const [ editing, setEditing ] = useState(continueEditing === "action");
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
        Post('/action/create',
            {
                retrospectiveId: retrospectiveId,
                title: title.trim()
            })
            .then(
                actionJson => {
                    if (continueEditing !== "action") {
                        setEditing(false);
                    }
                    setUpdating(false);
                    setTitle("");
                    addAction(actionJson);
                },
                err => {
                    alert("Could not create action: " + err);
                });
    }

    const onKeyUp = (e) => {
        if (e.keyCode === KEY_CODE_ENTER) {
            setContinueEditing("action");
            continueEditing = "action";
            onTitleChangeComplete(e);
        } else if (e.keyCode === KEY_CODE_ESCAPE){
            setEditing(false);
            setContinueEditing(null);
            continueEditing = null;
        }
    }

    const display = editing && !updating
        ? <textarea className="action-title font-reset" autoFocus onChange={onTitleChange} onBlur={onTitleChangeComplete} onKeyUp={onKeyUp} value={title} />
        : <a className="clickable no-underline" onClick={onStartEditing}>+</a>

    return (<div className="action bold-content extra-large-font center">
        {display}
    </div>);
}

const mapStateToProps = (state) => {
    return {
        continueEditing: state.session.continueEditing,
        retrospectiveId: state.retrospective.id
    }
}

export default connect(mapStateToProps, { showAvatarMenu, setContinueEditing, addAction })(NewAction);