import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { setActiveControl } from './redux/sessionActions';
import { Get } from './rest';

const UserSelection = ({ currentUsers, setCurrentUsers, maxUsers, requiredUsers, userType, controlId, activeControlId, setActiveControl, noSelectionText }) => {
    const [ expanded, setExpanded ] = useState(false);
    const [ allUsers, setAllUsers ] = useState(null);
    const [ error, setError ] = useState(null);

    const loadUsers = () => {
        Get('/users')
            .then(json => {
                //add in users that have been selected but cannot be found
                Object.values(currentUsers).forEach(currentUser => {
                    if (!json[currentUser.username]) {
                        json[currentUser.username] = currentUser;
                        json[currentUser.username].notFound = true;
                    }
                });

                setAllUsers(json);
            },
            err => {
                setError(err);
            })
    }
    
    useEffect(
        () => {
            if (!allUsers && expanded) {
                loadUsers();
            }
        },
        [expanded]);

    useEffect(
        () => {
            if (activeControlId !== controlId) {
                setExpanded(false);
            }
        }, 
        [ activeControlId ]
    )

    const userSortFunction = (a, b) => {
        const displayNameComparison = a.displayName.localeCompare(b.displayName);

        if (displayNameComparison !== 0) {
            return displayNameComparison;
        }

        const requiredComparison = isRequired(a) === isRequired(b);
        if (!requiredComparison) {
            return isRequired(a) ? -1 : 1;
        }

        return a.username.localeCompare(b.username);
    }

    const collapsedDisplay = () => {
        if (Object.keys(currentUsers).length === 0) {
            return (<span>{noSelectionText || ('No ' + userType + 's')}</span>);
        }

        if (Object.keys(currentUsers).length <= 2) {
            const usersToDisplay = Object.values(currentUsers).orderBy(userSortFunction);

            return (<span>
                {usersToDisplay.map(user => renderSelectedUserCollapsed(user))}
            </span>);
        }

        return (<span>{Object.keys(currentUsers).length} {userType}/s</span>);
    }

    const renderSelectedUserCollapsed = (user) => {
        const allUsersUser = allUsers ? allUsers[user.username] : { displayName: user.username, avatarUrl: "" };
        const displayName = user.displayName ? user.displayName : allUsersUser.displayName;
        const avatarUrl = user.avatarUrl ? user.avatarUrl : allUsersUser.avatarUrl;

        return (<span key={user.username} className="collapsed-selection no-wrap"><img className="selection-avatar" src={avatarUrl} />{displayName}</span>);
    }

    const toggleExpanded = (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (activeControlId !== controlId) {
            //cannot be expanded, so expand it

            setActiveControl(controlId);
            setExpanded(true);
            return;
        }

        const shouldBeExpanded = !expanded;
        if (shouldBeExpanded) {
            setActiveControl(controlId);
        } else {
            setActiveControl(null);
        }

        setExpanded(shouldBeExpanded);
    }

    const selectableUserDisplay = (user) => {
        const selected = isSelected(user);
        const required = selected && isRequired(user);
        let className = required ? " required": "";
        if (selected){
            className += " selected";
        }

        return (<div key={user.username} className={'selectable' + className} data-username={user.username} onClick={toggleUserSelection}>
                    <span className="no-wrap selection-margins">
                        <img className="selection-avatar" src={user.avatarUrl} />{user.displayName}
                    </span>
                </div>);
    }

    const toggleUserSelection = (e) => {
        e.preventDefault();
        e.stopPropagation();
        
        const username = e.currentTarget.getAttribute("data-username");

        const user = allUsers[username];

        if (isRequired(user)) {
            return;
        }

        let newSelection = { ...currentUsers };
        let singleSelection = maxUsers == 1; /* intentionally a == comparison here, to cater for "1" == 1 */

        if (isSelected(user)) {
            delete newSelection[user.username];
        } else {
            let currentSelectionCount = Object.keys(newSelection).length;

            if (singleSelection) {
                newSelection = { };
                newSelection[user.username] = user;
            } else if (currentSelectionCount >= maxUsers) {
                return;
            } else {
                newSelection[user.username] = user;
            }
        }

        setCurrentUsers(newSelection);
        if (singleSelection) {
            setExpanded(false);
            setActiveControl(null);
        }
    }

    const isRequired = (user) => {
        if (!requiredUsers) {
            return false;
        }

        if (requiredUsers === user.username || requiredUsers.username === user.username) {
            return true;
        }

        return requiredUsers[user.username];
    }

    const isSelected = (user) => {
        return currentUsers[user.username];
    }

    const expandedDisplay = () => {
        if (error) {
            return (<div>Error: {error}</div>);
        }

        if (allUsers === null) {
            return (<div className="floating-drop-down loading">Loading...</div>);
        }

        const selectedUsers = Object.values(allUsers)
            .filter(user => isSelected(user))
            .orderBy(userSortFunction);
        const remainingUsers = Object.values(allUsers)
            .filter(user => !isSelected(user) && !user.notFound)
            .orderBy(userSortFunction);

        return (<div className="floating-drop-down">
            {selectedUsers.map(selectableUserDisplay)}
            {remainingUsers.map(selectableUserDisplay)}
        </div>);
    }

    const isActiveControl = () => {
        return activeControlId === null || activeControlId === controlId;
    }

	return (
        <div className="drop-down-multi-select with-avatars" onClick={toggleExpanded}>
            { collapsedDisplay() }
            { expanded && isActiveControl() ? expandedDisplay() : null }
        </div>);
}


const mapStateToProps = (state) => {
	return {
        activeControlId: state.session.activeControlId
	}
}

export default connect(mapStateToProps, { setActiveControl })(UserSelection);