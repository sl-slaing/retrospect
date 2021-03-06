import React from 'react';
import { connect } from 'react-redux';

import { Post } from './rest'; 
import { logout, showAvatarMenu, switchUiMode } from "./redux/sessionActions";
import { ADMINISTER_SYSTEM, MANAGE_TENANTS } from './redux/uiModes';

const Avatar = ({ tenant, menuVisible, loggedInUser, dynamicMenu, showAvatarMenu, logout, switchUiMode, showSystemAdministration }) => {
    const toggleMenu = (e) => {
        e.preventDefault();
        showAvatarMenu(!menuVisible);
    }

    const onLogout = (e) => {
        e.preventDefault();
        const logoutUri = e.target.getAttribute("href");

        Post(tenant, logoutUri,
            { },
            true)
            .then(
                () => {
                    logout();

                    document.location.href = "/";
                },
                err => {
                    alert("Could not logout: " + err);
                });
    }

    const administerSystem = (e) => {
        e.preventDefault();
        switchUiMode(ADMINISTER_SYSTEM);
    }

    const onManageTenants = (e) => {
        e.preventDefault();
        switchUiMode(MANAGE_TENANTS);
    }

    if (loggedInUser == null){
        return null;
    }

    const menu = (<div className="menu">
            <span className="menu-item menu-item-heading">{loggedInUser.displayName}</span>
            {dynamicMenu()}
            { showSystemAdministration ? (<a className="menu-item clickable" onClick={administerSystem}>System administration</a>) :  null }
            <a onClick={onManageTenants} className="menu-item clickable">Tenant: {tenant ? tenant.name : 'Select tenant'}</a>
            <span className="menu-item menu-item-note">{'Logged in with ' + loggedInUser.provider}</span>
            <a href="/logout" onClick={onLogout} className="menu-item clickable">Logout</a>
        </div>);

    return (<>
        <img src={loggedInUser.avatarUrl} className="avatar" onClick={toggleMenu} />
        {menuVisible ? menu : null}
    </>);
}

const mapStateToProps = (state) => {
    return {
        loggedInUser: state.session.loggedInUser,
        dynamicMenu: state.session.menu,
        menuVisible: state.session.menuVisible,
        showSystemAdministration: state.session.showSystemAdministration,
        tenant: state.session.selectedTenant
    }
}

export default connect(mapStateToProps, { logout, showAvatarMenu, switchUiMode })(Avatar);