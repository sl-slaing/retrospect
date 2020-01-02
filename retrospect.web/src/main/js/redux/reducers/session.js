import { LOGIN, LOGOUT, SET_MENU_CALLBACK, SHOW_AVATAR_MENU, CONTINUE_EDITING, SET_HEADING, SWITCH_UI_MODE, SET_ACTIVE_CONTROL, SET_SELECTED_TENANT, ADD_TENANT, UPDATE_TENANT, REMOVE_TENANT } from '../actionTypes';
import { MANAGE_RETROSPECTIVES, EDIT_RETROSPECTIVE } from '../uiModes';
import { getDocumentHash, removeFromDocumentHash, setDocumentHash, rememberTenant, forgetTenant, retrieveRememberedTenant as retrieveRememberedTenantId } from '../../helpers';

const getInitialDisplayMode = () => {
    const documentHash = getDocumentHash() || { };

    if (documentHash.displayMode) {
        return documentHash.displayMode;
    }

    return documentHash.id
        ? EDIT_RETROSPECTIVE 
        : MANAGE_RETROSPECTIVES;
}

const getInitialTenant = (loginAction) => {
    if (loginAction === null) {
        return null;
    }

    if (!loginAction.tenants || loginAction.tenants.length === 0) {
        forgetTenant(loginAction.user.username);
        return null;
    }

    if (loginAction.tenants.length === 1) {
        const tenant = loginAction.tenants[0];
        rememberTenant(loginAction.user.username, tenant);
        return tenant;
    }

    const rememberedTenantId = retrieveRememberedTenantId(loginAction.user.username);
    const tenant = loginAction.tenants[rememberedTenantId];
    if (!tenant || tenant.state !== 'ACTIVE') {
        return null;
    }

    return tenant;
}

const defaultHeading = "Retrospect";
const initialState = {
    loggedInUser: null,
    menu: () => [],
    menuVisible: false,
    continueEditing: null,
    heading: defaultHeading,
    displayMode: getInitialDisplayMode(),
    activeControlId: null,
    showSystemAdministration: false,
    tenants: {},
    selectedTenant: getInitialTenant(null)
};

export default (state = initialState, action) => {
    switch(action.type) {
        case LOGIN: {
            return {
                ...state,
                loggedInUser: action.user,
                showSystemAdministration: action.showSystemAdministration,
                tenants: action.tenants,
                selectedTenant: getInitialTenant(action)
            };
        }
        case SET_SELECTED_TENANT: {
            rememberTenant(state.loggedInUser.username, action.tenant);

            return {
                ...state,
                selectedTenant: action.tenant
            }
        }
        case ADD_TENANT: {
            const newState = {
                ...state
            };

            newState.tenants[action.tenant.id] = action.tenant;

            return newState;
        }
        case UPDATE_TENANT: {
            const newState = {
                ...state
            };

            newState.tenants[action.tenant.id] = action.tenant;

            const rememberedTenantId = retrieveRememberedTenantId(state.loggedInUser.username);
            if (rememberedTenantId && rememberedTenantId === action.tenant.id && action.tenant.state !== 'ACTIVE') {
                forgetTenant(state.loggedInUser.username); //tenant is now inactive
                newState.selectedTenant = null;
            }

            return newState;
        }
        case REMOVE_TENANT: {
            const newState = {
                ...state
            };

            delete newState.tenants[action.id];
            if (newState.selectedTenant && newState.selectedTenant.id === action.id) {
                newState.selectedTenant = null;
            }

            const rememberedTenantId = retrieveRememberedTenantId(state.loggedInUser.username);
            if (rememberedTenantId && rememberedTenantId === action.id) {
                forgetTenant(state.loggedInUser.username);
            }

            return newState;
        }
        case SET_ACTIVE_CONTROL : {
            return {
                ...state,
                activeControlId: action.controlId,
                menuVisible: false
            }
        }
        case SWITCH_UI_MODE: {
            if (action.mode === EDIT_RETROSPECTIVE || action.mode === MANAGE_RETROSPECTIVES) {
                removeFromDocumentHash("displayMode");
            } else {
                setDocumentHash({ displayMode: action.mode });
            }

            if (action.mode === MANAGE_RETROSPECTIVES) {
                removeFromDocumentHash("id");
            }

            return {
                ...state,
                menuVisible: false,
                menu: (() => []),
                continueEditing: false,
                displayMode: action.mode,
                activeControlId: null
            };
        }
        case LOGOUT: {
            return {
                ...state,
                loggedInUser: null,
                activeControlId: null,
                showSystemAdministration: false,
                tenants: [],
                selectedTenant: null
            };
        }
        case SET_MENU_CALLBACK: {
            return {
                ...state,
                menu: action.callback || (() => [])
            }
        }
        case SHOW_AVATAR_MENU: {
            return {
                ...state,
                menuVisible: action.visible,
                activeControlId: null
            };
        }
        case CONTINUE_EDITING: {
            return {
                ...state,
                continueEditing: action.dataType
            };
        }
        case SET_HEADING: {
            return {
                ...state,
                heading: action.heading || defaultHeading
            };
        }
        default: 
            return state;
    }
}