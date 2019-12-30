import { LOGIN, LOGOUT, SET_MENU_CALLBACK, SHOW_AVATAR_MENU, CONTINUE_EDITING, SET_HEADING, SWITCH_UI_MODE, SET_ACTIVE_CONTROL } from '../actionTypes';
import { MANAGE_RETROSPECTIVES, EDIT_RETROSPECTIVE, ADMINISTER_RETROSPECTIVE } from '../uiModes';
import { getDocumentHash, removeFromDocumentHash, setDocumentHash } from '../../helpers';

const getInitialDisplayMode = () => {
    const documentHash = getDocumentHash() || { };

    if (documentHash.displayMode) {
        return documentHash.displayMode;
    }

    return documentHash.id
        ? EDIT_RETROSPECTIVE 
        : MANAGE_RETROSPECTIVES;
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
    showSystemAdministration: false
};

export default (state = initialState, action) => {
    switch(action.type) {
        case LOGIN: {
            return {
                ...state,
                loggedInUser: action.user,
                showSystemAdministration: action.showSystemAdministration
            };
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
                activeControlId: null
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