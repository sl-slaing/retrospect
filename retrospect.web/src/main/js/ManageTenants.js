import React, {useState} from 'react';
import { connect } from "react-redux";

import { Post } from './rest';
import { MANAGE_RETROSPECTIVES } from './redux/uiModes';
import { addTenant, setSelectedTenant, switchUiMode } from './redux/sessionActions';

import Error from './Error';
import Working from './Working'

const ManageTenants = ({ tenant, tenants, addTenant, setSelectedTenant, switchUiMode }) => {
    const [ mode, setMode ] = useState("main");
    const [ error, setError ] = useState(null);
    const [ newTenantName, setNewTenantName ] = useState("");

    if (error !== null) {
        return (<Error error={error} />);
    }

    const updateNewTenantName = (e) => {
        setNewTenantName(e.currentTarget.value);
    }

    const selectTenant = (e) => {
        e.preventDefault();

        var tenantId = e.currentTarget.getAttribute("data-tenant-id");
        var matchingTenants = tenants.filter(t => t.id === tenantId);
        if (matchingTenants.length === 1) {
            var selectTenant = matchingTenants[0];
            setSelectedTenant(selectTenant);
            switchUiMode(MANAGE_RETROSPECTIVES);
        } else {
            alert("Unable to select tenant, please choose another or create a new one");
        }
    }

    const createTenant = (e) => {
        e.preventDefault();

        setMode("creating");

        Post(null, '/createTenant',
            { name: newTenantName })
            .then(
                viewModel => {
                    addTenant(viewModel);
                    setNewTenantName("");
                    setMode("main");
                }, 
                err => {
                    setError(err);
                });
    }

    const tenantOptions = tenants.map(t => (
        <a key={t.id} className={'clickable' + (tenant && tenant.id === t.id) ? ' selected' : ''``} onClick={selectTenant} data-tenant-id={t.id}>
            {t.name}
        </a>)
    );

    if (mode === "creating") {
        return (<Working message="Creating tenant..." />);
    }

    return (<div className="vertically-centered">
    <div className="white-panel">
        <h4 className="center">Select a tenant</h4>
        <div className="list">
            {tenantOptions}
        </div>
        <div>
            <h5>Create a new tenant</h5>
            <label>
                Name: 
                <input value={newTenantName} onChange={updateNewTenantName} />
            </label>
        </div>
        <div className="buttons green-top-border">
            <a className="button clickable" onClick={createTenant}>Create tenant</a>
        </div>
    </div>
</div>);
}

const mapStateToProps = (state) => {
    return {
        tenant: state.session.selectedTenant,
        tenants: state.session.tenants
    }
}

export default connect(mapStateToProps, { addTenant, setSelectedTenant, switchUiMode })(ManageTenants);