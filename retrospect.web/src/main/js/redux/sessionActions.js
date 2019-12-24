import { LOGIN, LOGOUT, SET_HEADING, SET_MENU_CALLBACK, SHOW_AVATAR_MENU, CONTINUE_EDITING, SWITCH_UI_MODE, SET_ACTIVE_CONTROL } from './actionTypes'

export const login = (user) => ({
    type: LOGIN,
    user
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