import { SET_RETROSPECTIVES, ADD_RETROSPECTIVE, REMOVE_RETROSPECTIVE } from '../actionTypes';

const initialState = null;

export default (state = initialState, action) => {
    switch(action.type) {
        case SET_RETROSPECTIVES: {
            return action.retrospectiveMap;
        }
        case ADD_RETROSPECTIVE: {
            const overview = action.overview;

            let newState = {
                ...state
            };

            newState[overview.id] = overview;

            return newState;
        }
        case REMOVE_RETROSPECTIVE: {
            const idToRemove = action.id;

            if (state === null){
                return state;
            }

            let newState = {
                ...state
            };

            delete newState[idToRemove];

            return newState;
        }
        default: 
            return state;
    }
}