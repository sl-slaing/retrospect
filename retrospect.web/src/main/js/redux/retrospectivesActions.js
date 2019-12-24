import { SET_RETROSPECTIVES, ADD_RETROSPECTIVE, REMOVE_RETROSPECTIVE } from './actionTypes';

export const setRetrospectives = (retrospectiveMap) => ({
    type: SET_RETROSPECTIVES,
    retrospectiveMap
});

export const addRetrospective = (overview) => ({
    type: ADD_RETROSPECTIVE,
    overview
});

export const removeRetrospective = (id) => ({
    type: REMOVE_RETROSPECTIVE,
    id
});

