import React, {useState, useEffect } from 'react';
import { connect } from "react-redux";

import { Post } from './rest';
import { MANAGE_TENANTS, MANAGE_RETROSPECTIVES } from './redux/uiModes';
import { addTenant, updateTenant, removeTenant, setSelectedTenant, switchUiMode } from './redux/sessionActions';

import Error from './Error';
import Working from './Working'

const ManageTenants = ({ loggedInUser, displayMode, tenant, tenants, showSystemAdministration, addTenant, updateTenant, removeTenant, setSelectedTenant, switchUiMode }) => {
    const [ mode, setMode ] = useState("main");
    const [ error, setError ] = useState(null);
    const [ newTenantName, setNewTenantName ] = useState("");
    const [ manageTenant, setManageTenant ] = useState(tenant);

    const userMapToArray = (usermap) => {
        return Object.keys(usermap);
    }

    const [ tenantName, setTenantName ] = useState("");
    const [ tenantState, setTenantState ] = useState("");
    const [ tenantAdministrators, setTenantAdministrators ] = useState([]); 
    const [ tenantUsers, setTenantUsers ] = useState([]);
    
    useEffect(() => {
        if (!manageTenant) {
            return;
        }

        setTenantName(manageTenant.name);
        setTenantState(manageTenant.state);
        setTenantAdministrators(userMapToArray(manageTenant.administrators));
        setTenantUsers(userMapToArray(manageTenant.users));
    }, [ manageTenant ]);

    const removeError = () => {
        setError(null);
        setMode("main");
    }

    if (error !== null) {
        return (<Error error={error} recover={removeError} recoverText="Try again..." />);
    }

    const updateNewTenantName = (e) => {
        e.preventDefault();
        setNewTenantName(e.currentTarget.value);
    }

    const updateTenantName = (e) => {
        e.preventDefault();
        setTenantName(e.currentTarget.value);
    }

    const updateTenantState = (e) => {
        e.preventDefault();
        setTenantState(e.currentTarget.value);
    }

    const onSelectTenant = (e) => {
        e.preventDefault();

        const tenantId = e.currentTarget.getAttribute("data-tenant-id");
        const matchingTenants = Object.values(tenants).filter(t => t.id === tenantId);
        if (matchingTenants.length === 1) {
            const selectTenant = matchingTenants[0];

            if (displayMode !== MANAGE_TENANTS) {
                onUseTenant(e, selectTenant);
                return;
            }

            setManageTenant(selectTenant);
        } else {
            alert("Unable to select tenant, please choose another or create a new one");
        }
    }

    const onUseTenant = (e, overrideManageTenant) => {
        e.preventDefault();

        const tenant = overrideManageTenant || manageTenant;

        if (tenant.state !== 'ACTIVE') {
            alert('You cannot select this tenant because it is inactive.');
            return;
        }

        setSelectedTenant(tenant);
        switchUiMode(MANAGE_RETROSPECTIVES);
    }

    const onCreateTenant = (e) => {
        e.preventDefault();

        setMode("creating");

        Post(null, '/createTenant',
            { name: newTenantName })
            .then(
                viewModel => {
                    addTenant(viewModel);
                    setNewTenantName("");
                    setMode("main");

                    if (displayMode === MANAGE_TENANTS) {
                        setManageTenant(viewModel);
                    }
                }, 
                err => {
                    setError(err);
                });
    }

    const onUpdateTenant = (e) => {
        e.preventDefault();

        setMode("updating");

        Post(null, '/updateTenant',
            {  
                id: manageTenant.id,
                name: tenantName,
                state: tenantState,
                users: tenantUsers,
                administrators: tenantAdministrators
            })
            .then(
                viewModel => {
                    updateTenant(viewModel);
                    setManageTenant(viewModel);
                    setMode("main");
                }, 
                err => {
                    setError(err);
                });
    }

    const onDeleteTenant = (e) => {
        e.preventDefault();

        if (!confirm("Are you sure you want to delete this tenant?")) {
            return;
        }

        setMode("deleting");

        Post(null, '/deleteTenant',
            {  
                id: manageTenant.id
            }, 
            true)
            .then(
                () => {
                    removeTenant(manageTenant.id);
                    setMode("main");
                }, 
                err => {
                    setError(err);
                });
    }

    const getTenantItemClassName = (tenant) => {
        var classNames = [
            'list-item',
            'no-wrap'
        ]

        if (tenant.state === 'ACTIVE' || displayMode === MANAGE_TENANTS) {
            classNames.push('clickable');
        }
        
        if (tenant.state === 'DISABLED') {
            classNames.push('list-item-disabled');
        }

        if (manageTenant && tenant.id === manageTenant.id) {
            classNames.push('list-item-selected');

            if (displayMode === MANAGE_TENANTS) {
                classNames.push('list-item-right-arrow');
            }
        }

        return classNames.join(' ');
    }

    const onChangeOfAdministrators = (e) => {
        e.preventDefault();

        const arrayOfUsernames = e.currentTarget.value === '' ? [] : e.currentTarget.value.split('\n');
        setTenantAdministrators(arrayOfUsernames);
    }

    const onChangeOfUsers = (e) => {
        e.preventDefault();

        const arrayOfUsernames = e.currentTarget.value === '' ? [] : e.currentTarget.value.split('\n');
        setTenantUsers(arrayOfUsernames);
    }

    const canEditTenant = () => {
        if (showSystemAdministration){
            return true;
        }

        const administrator = manageTenant.administrators[loggedInUser.username];
        return administrator ? true : false;
    }

    const tenantOptions = Object.values(tenants).map(t => (
        <a key={t.id} className={getTenantItemClassName(t)} onClick={onSelectTenant} data-tenant-id={t.id}>
            {t.name}
        </a>)
    );

    if (mode === "creating") {
        return (<Working message="Creating tenant..." />);
    }

    if (mode === "updating") {
        return (<Working message="Updating tenant..." />);
    }

    if (mode === "deleting") {
        return (<Working message="Deleting tenant..." />);
    }

    if (displayMode === MANAGE_TENANTS) {
        return (<div className="vertically-centered">
                <div className="white-panel">
                    <h4 className="center">Manage tenants</h4>
                    <div className="columns">
                        <div className="column list">
                            {tenantOptions}
                        </div>
                        <div className="column">
                            <div className="grey-panel stretch-panel margin-left-for-list-arrow">
                                <div>
                                    <label>
                                        Name: 
                                        <input value={tenantName} onChange={updateTenantName} disabled={!canEditTenant()} />
                                    </label>
                                </div>
                                <div>
                                    <label>
                                        State: 
                                        <select value={tenantState} onChange={updateTenantState} disabled={!canEditTenant()}>
                                            <option value="ACTIVE">Active</option>
                                            <option value="DISABLED">Disabled</option>
                                        </select>
                                    </label>
                                </div>
                                <div>
                                    <label>
                                        Administrators: (one username per line)<br />
                                        <textarea value={tenantAdministrators.join('\n')} disabled={!canEditTenant()} onChange={onChangeOfAdministrators}></textarea>
                                    </label>
                                </div>
                                <div>
                                    <label>
                                        Users: (one username per line)<br/>
                                        <textarea value={tenantUsers.join('\n')} disabled={!canEditTenant()} onChange={onChangeOfUsers}></textarea>
                                    </label>
                                </div>
                                <div className="buttons green-top-border">
                                    {canEditTenant() ? (<a className="button clickable" onClick={onUpdateTenant}>Update tenant</a>) : null}
                                    {canEditTenant() ? (<a className="button clickable" onClick={onDeleteTenant}>Delete tenant</a>) : null}
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="buttons green-top-border">
                        <a className="button clickable" onClick={onUseTenant}>Use '{manageTenant.name}'</a>
                    </div>
                    <h4 className="center">Create a tenant</h4>
                    <div>
                        <label>
                            Name: 
                            <input value={newTenantName} onChange={updateNewTenantName} />
                        </label>
                    </div>
                    <div className="buttons green-top-border">
                        <a className="button clickable" onClick={onCreateTenant}>Create tenant</a>
                    </div>
                </div>
            </div>);
    }

    return (<div className="vertically-centered">
                <div className="white-panel">
                    <h4 className="center">Select a tenant</h4>
                    <div className="list">
                        {tenantOptions}
                    </div>
                    <h4 className="center">Create a tenant</h4>
                    <div>
                        <label>
                            Name: 
                            <input value={newTenantName} onChange={updateNewTenantName} />
                        </label>
                    </div>
                    <div className="buttons green-top-border">
                        <a className="button clickable" onClick={onCreateTenant}>Create tenant</a>
                    </div>
                </div>
            </div>);
}

const mapStateToProps = (state) => {
    return {
        tenant: state.session.selectedTenant,
        tenants: state.session.tenants,
        displayMode: state.session.displayMode,
        loggedInUser: state.session.loggedInUser,
        showSystemAdministration: state.session.showSystemAdministration
    }
}

export default connect(mapStateToProps, { addTenant, updateTenant, removeTenant, setSelectedTenant, switchUiMode })(ManageTenants);