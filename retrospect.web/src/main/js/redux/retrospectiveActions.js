import { SET_RETROSPECTIVE, UPDATE_OBSERVATION, ADD_OBSERVATION, DELETE_OBSERVATION, SET_RETROSPECTIVE_BY_ID, UPDATE_ACTION, ADD_ACTION, DELETE_ACTION } from './actionTypes';

export const setRetrospective = (viewModel) => {
    return {
        type: SET_RETROSPECTIVE,
        viewModel
    };
};

export const updateObservation = (viewModel) => ({
    type: UPDATE_OBSERVATION,
    viewModel
});

export const addObservation = (viewModel) => ({
    type: ADD_OBSERVATION,
    viewModel
});

export const deleteObservation = (id, type) => ({
    type: DELETE_OBSERVATION,
    observationId: id,
    observationType: type
});

export const updateAction = (viewModel) => ({
    type: UPDATE_ACTION,
    viewModel
});

export const addAction = (viewModel) => ({
    type: ADD_ACTION,
    viewModel
});

export const deleteAction = (id) => ({
    type: DELETE_ACTION,
    actionId: id
});

export const setRetrospectiveById = (id, forceReload) => ({
    type: SET_RETROSPECTIVE_BY_ID,
    id,
    forceReload
});