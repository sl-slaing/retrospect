import React from 'react';
import { connect } from "react-redux";

import { Delete } from './rest'; 
import { MANAGE_RETROSPECTIVES, ADMINISTER_RETROSPECTIVE } from './redux/uiModes';
import { showAvatarMenu, switchUiMode } from './redux/sessionActions';
import { removeRetrospective } from './redux/retrospectivesActions';
import { removeFromDocumentHash } from './helpers';

const RetrospectiveMenuItems = ({ retrospective, showAvatarMenu, removeRetrospective, switchUiMode }) => {

    if (!retrospective.administrator) {
        return (<></>);
    }

    const editRetrospective = (e) => {
        e.preventDefault();
        showAvatarMenu(false);
        switchUiMode(ADMINISTER_RETROSPECTIVE);
    }

    const deleteRetrospective = (e) => {      
        e.preventDefault();
        showAvatarMenu(false);

        if (!confirm("Are you sure you want to delete this retrospective?")){
            return;
        }

        Delete('/retrospective',
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

    return (<>
            <a key="editRetrospective" className="menu-item clickable" onClick={editRetrospective}>Edit retrospective</a>
            <a key="deleteRetrospective" className="menu-item clickable" onClick={deleteRetrospective}>Delete retrospective</a>
        </>);
}

const mapStateToProps = (state) => {
	return {
        retrospective: state.retrospective
	}
}

export default connect(mapStateToProps, { showAvatarMenu, removeRetrospective, switchUiMode })(RetrospectiveMenuItems);