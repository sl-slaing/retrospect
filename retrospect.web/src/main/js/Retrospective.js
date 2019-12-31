import React, {useState, useEffect } from 'react';
import { connect } from "react-redux";

import { Post } from './rest'; 
import { MANAGE_RETROSPECTIVES, EDIT_RETROSPECTIVE } from './redux/uiModes';
import { setRetrospectiveById, addAction } from "./redux/retrospectiveActions";
import { setMenuCallback, switchUiMode, setHeading } from './redux/sessionActions';
import { getDocumentHash, setDocumentHash, removeFromDocumentHash } from './helpers';

import Action from './Action';
import Error from './Error';
import NewAction from './NewAction';
import NewObservation from './NewObservation';
import RetrospectiveMenuItems from './RetrospectiveMenuItems';
import Observation from './Observation';

const Retrospective = ({ tenant, heading, retrospective, setHeading, setMenuCallback, switchUiMode, setRetrospectiveById, addAction }) => {
    if (!retrospective){
        throw { message: "No retrospective provided" };
    }

    const modes = {
        REVIEW_PREVIOUS_ACTIONS: { 
            id: "REVIEW_PREVIOUS_ACTIONS", 
            name: "Review previous actions",
            prev: null,
            next: "RETROSPECT",
            condition: (retrospective) => {
                return retrospective.previousRetrospectiveId || false;
            }
        },
        RETROSPECT: { 
            id: "RETROSPECT", 
            name: `Retrospect (${retrospective.readableId})`,
            prev: "REVIEW_PREVIOUS_ACTIONS",
            next: "DEFINE_ACTIONS"
        },
        DEFINE_ACTIONS: { 
            id: "DEFINE_ACTIONS", 
            name: "Define actions",
            prev: "RETROSPECT",
            next: null
        },
    }

    const getFirstMode = (firstMode) => {
        const documentHash = getDocumentHash() || { };
        if (documentHash.mode && modes[documentHash.mode]) {
            const requestedMode = modes[documentHash.mode];
            if (!requestedMode.condition || requestedMode.condition(retrospective)){
                return requestedMode;
            }

            removeFromDocumentHash("mode");
        }

        while (firstMode.condition && !firstMode.condition(retrospective)) {
            firstMode = modes[firstMode.next];
        }

        return firstMode;
    }

    const [ currentMode, setCurrentMode ] = useState(getFirstMode(modes.RETROSPECT));
    const [ error, setError ] = useState(null);

    if (error) {
        return (<Error error={error} />);
    }

    useEffect(
        () => {
            setHeading(currentMode.name);
        },
        [currentMode])

    const switchToMode = (e) => {
        try {
            e.preventDefault();
            let modeId = e.currentTarget.getAttribute("data-mode-id");
            let mode = modes[modeId];
            setDocumentHash({ mode: mode.id });
            setCurrentMode(mode);
        } catch (e) {
            setError(e);
        }
    }

    const backToList = (e) => {
        try {
            e.preventDefault();

            removeFromDocumentHash("mode");
            setHeading(null);
            setMenuCallback(null);
            switchUiMode(MANAGE_RETROSPECTIVES);
        } catch (e) {
            setError(e);
        }
    }
    
    const navigateToPreviousRetrospective = (e) => {
        try {
            switchUiMode(EDIT_RETROSPECTIVE);
            removeFromDocumentHash("mode");
            setRetrospectiveById(retrospective.previousRetrospectiveId);
        } catch (e) {
            setError(e);
        }
    }

    const wentWellColumn = () => {
        const wentWellObservations = Object.values(retrospective.wentWell)
            .orderBy("sortIdentifier")
            .map(ob => (<Observation key={ob.id} observation={ob} />));

        return (
            <div className="column white-back relative flex-grow">
                <h4 className="center">What went well ({wentWellObservations.length})</h4>
                <div className="relative flex-grow">
                    <div className="column-content absolute-fill">
                        {wentWellObservations}
                        <NewObservation observationType="WENT_WELL" />
                    </div>
                </div>
            </div>);
    }

    const couldBeBetterColumn = (showNewObservation, markerLookup, copyAction, copyText) => {
        const couldBeBetterObservations = Object.values(retrospective.couldBeBetter)
            .orderBy("sortIdentifier")
            .map(ob => {
                const copiedToAnAction = Object.values(retrospective.actions).filter(a => a.fromObservationId === ob.id).length > 0;

                if (copiedToAnAction && markerLookup) {
                    const marker = getMarkerForObservation(markerLookup, ob.id, true);
                    const markerTitle = `Observation copied to an action, see (${marker}) marker in 'Actions' column`;

                    return (<Observation key={ob.id} observation={ob} marker={marker} markerTitle={markerTitle} />);
                }

                return (<Observation key={ob.id} observation={ob} copyAction={copyAction} copyText={copyText} />);
            });

        return (
            <div className="column white-back relative flex-grow">
                <h4 className="center">What could be better ({couldBeBetterObservations.length})</h4>
                <div className="relative flex-grow">
                    <div className="column-content absolute-fill">
                        {couldBeBetterObservations}
                        { showNewObservation ? (<NewObservation observationType="COULD_BE_BETTER" />) : null }
                    </div>
                </div>
            </div>);
    }

    const createActionFromCouldBeBetter = (observation) => {
        Post(tenant, '/action/create',
            {
                title: observation.title,
                retrospectiveId: retrospective.id,
                fromObservationId: observation.id
            })
            .then(
                actionJson => {
                    addAction(actionJson);
                },
                err => {
                    alert("Could not create action: " + err);
                });
    }

    const copyActionFromPreviousRetrospective = (action) => {
        Post(tenant, '/action/create',
            {
                ...action,
                retrospectiveId: retrospective.id,
                fromActionId: action.id,
                assignedToUsername: action.assignedTo ? action.assignedTo.username : null
            })
            .then(
                actionJson => {
                    addAction(actionJson);
                },
                err => {
                    alert("Could not create action: " + err);
                });
    }

    const getMarkerForAction = (markerLookup, fromActionId, createMarkerIfNotFound) => {
        if (markerLookup[fromActionId]) {
            return markerLookup[fromActionId];
        }

        if (!createMarkerIfNotFound) {
            return null;
        }

        let maxMarker = 0;
        Object.values(markerLookup).forEach(marker => {
            if (marker > maxMarker){
                maxMarker = marker;
            }
        });

        markerLookup[fromActionId] = ++maxMarker;
        return maxMarker;
    }

    const getMarkerForObservation = (markerLookup, fromObservationId, createMarkerIfNotFound) => {
        if (markerLookup[fromObservationId]) {
            return markerLookup[fromObservationId];
        }

        if (!createMarkerIfNotFound) {
            return null;
        }

        let maxMarker = 0;
        Object.values(markerLookup).forEach(marker => {
            if (marker > maxMarker){
                maxMarker = marker;
            }
        });

        markerLookup[fromObservationId] = ++maxMarker;
        return maxMarker;
    }

    const actionsFromPreviousRetrospectiveColumn = (markerLookup) => {
        const actions = Object.values(retrospective.previousRetrospectiveActions)
            .filter(action => !action.complete)
            .orderBy("sortIdentifier")
            .map(action => {
                const copiedToCurrentRetrospective = Object.values(retrospective.actions).filter(a => a.fromActionId === action.id).length > 0;

                if (copiedToCurrentRetrospective) {
                    const marker = getMarkerForAction(markerLookup, action.id, true);
                    const markerTitle = `Action copied to this retrospective, see (${marker}) marker in 'Actions' column`;

                    return (<Action key={action.id} allowCompletion={true} overrideRetrospectiveId={retrospective.previousRetrospectiveId} action={action} readonly={true} marker={marker} markerTitle={markerTitle} />);
                } else if (action.complete) {
                    return (<Action key={action.id} allowCompletion={true} overrideRetrospectiveId={retrospective.previousRetrospectiveId} action={action} readonly={true} />);
                } else {
                    return (<Action key={action.id} allowCompletion={true} overrideRetrospectiveId={retrospective.previousRetrospectiveId} action={action} readonly={true} copyAction={copyActionFromPreviousRetrospective} copyText="Copy to this retrospective →" />);
                }
            });

        return (
            <div className="column white-back relative flex-grow">
                <h4 className="center" title={retrospective.previousRetrospectiveId}>Previous actions (from <a className="clickable" onClick={navigateToPreviousRetrospective}>{retrospective.previousRetrospectiveReadableId}</a>)</h4>
                <div className="relative flex-grow">
                    <div className="column-content absolute-fill">
                        {actions}
                    </div>
                </div>
            </div>);
    }

    const actionsColumn = (markerLookupFunc) => {
        const actions = Object.values(retrospective.actions)
            .orderBy("sortIdentifier")
            .map(action => {
                const marker = markerLookupFunc(action);
                const markerTitle = marker ? `Action copied from the previous retrospective, see (${marker}) marker in 'Previous actions' column` : null

                return (<Action key={action.id} action={action} marker={marker} markerTitle={markerTitle} />);
            });

        return (
            <div className="column white-back relative flex-grow">
                <h4 className="center">Actions</h4>
                <div className="relative flex-grow">
                    <div className="column-content absolute-fill">
                        {actions}
                        <NewAction />
                    </div>
                </div>
            </div>);
    }

    const renderButton = (modeId, prefix, suffix) => {
        if (modeId === null) { 
            return (<a className="navigation-option clickable" onClick={backToList}></a>);
        }
        let modeToNavigateTo = modes[modeId];

        if (modeToNavigateTo.condition && !modeToNavigateTo.condition(retrospective)) {
            return (<a className="navigation-option"></a>);
        }
    
        return (
            <a className="navigation-option clickable" onClick={switchToMode} data-mode-id={modeId}>
                {prefix}{modeToNavigateTo.name}{suffix}
            </a>);
    }

    const renderMode = (mode) => {
        switch (mode.id) {
            case "REVIEW_PREVIOUS_ACTIONS": {
                const markerLookup = {};

                return (<>{actionsFromPreviousRetrospectiveColumn(markerLookup)}{actionsColumn(a => a.fromActionId ? getMarkerForAction(markerLookup, a.fromActionId, false) : null)}</>);
            }
            case "RETROSPECT": {
                return (<>{wentWellColumn()}{couldBeBetterColumn(true, null)}</>);
            }
            case "DEFINE_ACTIONS": {
                const markerLookup = {};

                return (<>
                        {couldBeBetterColumn(false, markerLookup, createActionFromCouldBeBetter, "Create action for this observation →")}
                        {actionsColumn(a => a.fromObservationId ? getMarkerForObservation(markerLookup, a.fromObservationId, false) : null)}
                        </>);
            }
            default: {
                throw { message: "Unknown render mode " + mode.id };
            }
        }
    }

    setMenuCallback(() => {
        return [
            <RetrospectiveMenuItems key="retrospectiveMenuItems" />
        ]
    });
    
    return (<>
                <div className="retrospective-navigation">
                    {renderButton(currentMode.prev, "← ", "")}
                    <h3 className="heading no-vertical-margin clickable grey-background" onClick={backToList}>{heading}</h3>
                    {renderButton(currentMode.next, "", " →")}
                </div>
                <div className="columns absolute-fill">
                    {renderMode(currentMode)}
                </div>
            </>);
}

const mapStateToProps = (state) => {
	return {
        retrospective: state.retrospective,
        heading: state.session.heading,
        tenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { setHeading, setMenuCallback, switchUiMode, setRetrospectiveById, addAction })(Retrospective);