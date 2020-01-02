import { LOGIN, LOGOUT, SET_HEADING, SET_MENU_CALLBACK, SHOW_AVATAR_MENU, CONTINUE_EDITING, SWITCH_UI_MODE, SET_ACTIVE_CONTROL, SET_SELECTED_TENANT, ADD_TENANT, UPDATE_TENANT, REMOVE_TENANT } from './actionTypes'

export const login = (user, showSystemAdministration, tenants) => ({
    type: LOGIN,
    user,
    showSystemAdministration,
    tenants
});

export const logout = () => ({
    type: LOGOUT
});

export const setHeading = (heading) => ({
    type: SET_HEADING,
    heading
});

export const setMenuCallback = (callback) => ({
    type: SET_MENU_CALLBACK,
    callback
});

export const showAvatarMenu = (visible) => ({
    type: SHOW_AVATAR_MENU,
    visible
});

export const setContinueEditing = (dataType) => ({
    type: CONTINUE_EDITING,
    dataType
});

export const switchUiMode = (mode) => ({
    type: SWITCH_UI_MODE,
    mode
});

export const setActiveControl = (controlId) => ({
    type: SET_ACTIVE_CONTROL,
    controlId
});

export const setSelectedTenant = (tenant) => ({
    type: SET_SELECTED_TENANT,
    tenant
});

export const addTenant = (tenant) => ({
    type: ADD_TENANT,
    tenant
});

export const updateTenant = (tenant) => ({
    type: UPDATE_TENANT,
    tenant
});

export const removeTenant = (id) => ({
    type: REMOVE_TENANT,
    id
});