import { SET_RETROSPECTIVE, UPDATE_OBSERVATION, ADD_OBSERVATION, DELETE_OBSERVATION, SET_RETROSPECTIVE_BY_ID, UPDATE_ACTION, ADD_ACTION, DELETE_ACTION } from '../actionTypes';
import { getDocumentHash, setDocumentHash } from '../../helpers';

const getObservationMap = (retrospective, type) => {
    switch (type) {
        case "WENT_WELL": {
            return retrospective.wentWell;
        }
        case "COULD_BE_BETTER": {
            return retrospective.couldBeBetter;
        }
        default:
            throw "Unknown observation type";
    }
}

const getInitialState = () => {
    var hashData = getDocumentHash() || { };
    if (hashData.id) {
        return {
            id: hashData.id,
            loadById: true
        };
    }

    return null;
}

const initialState = getInitialState();

export default (state = initialState, action) => {
    switch(action.type) {
        case SET_RETROSPECTIVE: {
            setDocumentHash({ id: action.viewModel.id });

            return action.viewModel;
        }
        case SET_RETROSPECTIVE_BY_ID: {
            setDocumentHash({ id: action.id });

            if (state && state.id === action.id && !action.forceReload) {
                return state;
            }

            return {
                id: action.id,
                loadById: true 
            };
        }
        case UPDATE_OBSERVATION: {
            const observation = action.viewModel;
            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            var observations = getObservationMap(newState, observation.type);
            observations[observation.id] = observation;

            return newState;
        }
        case ADD_OBSERVATION: {
            const observation = action.viewModel;
            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            var observations = getObservationMap(newState, observation.type);
            observations[observation.id] = observation;

            return newState;
        }
        case DELETE_OBSERVATION: {
            const observationId = action.observationId;
            const observationType = action.observationType;

            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            var observations = getObservationMap(newState, observationType);
            delete observations[observationId];

            return newState;
        }
        case UPDATE_ACTION: {
            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            const actionExists = newState.actions[action.viewModel.id];
            const actionExistsInPreviousRetrospective = newState.previousRetrospectiveActions ? newState.previousRetrospectiveActions[action.viewModel.id] : false;

            if (actionExists) {
                newState.actions[action.viewModel.id] = action.viewModel;
            } else if (actionExistsInPreviousRetrospective) {
                newState.previousRetrospectiveActions[action.viewModel.id] = action.viewModel;
            }

            return newState;
        }
        case ADD_ACTION: {
            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            newState.actions[action.viewModel.id] = action.viewModel;

            return newState;
        }
        case DELETE_ACTION: {
            const actionId = action.actionId;

            if (state === null){
                throw new Error("No retrospective currently set.");
            }

            let newState = {
                ...state
            };

            delete newState.actions[actionId];
            if (newState.previousRetrospectiveActions) {
                delete newState.previousRetrospectiveActions[actionId];
            }

            return newState;
        }
        default: 
            return state;
    }
}