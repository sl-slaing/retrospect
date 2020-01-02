import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { Get } from './rest';
import { setActiveControl } from './redux/sessionActions';

const RetrospectiveSelection = ({ tenant, currentRetrospectives, setCurrentRetrospectives, requiredRetrospectives, maxRetrospectives, controlId, activeControlId, setActiveControl }) => {
    const [ expanded, setExpanded ] = useState(false);
    const [ allRetrospectives, setAllRetrospectives ] = useState(null);
    const [ error, setError ] = useState(null);

    const loadRetrospectives = () => {
        Get(tenant, '/retrospective')
            .then(json => {
                //add in Retrospectives that have been selected but cannot be found
                Object.values(currentRetrospectives).forEach(currentRetrospective => {
                    if (!json[currentRetrospective.id]) {
                        json[currentRetrospective.id] = currentRetrospective;
                        json[currentRetrospective.id].notFound = true;
                    }
                });

                setAllRetrospectives(json);
            },
            setError)
    }
    
    useEffect(
        () => {
            if (!allRetrospectives && expanded) {
                loadRetrospectives();
            }
        },
        [expanded]);

    useEffect(
        () => {
            if (activeControlId !== controlId) {
                setExpanded(false);
            }
        }, 
        [ activeControlId ]
    )

    const collapsedDisplay = () => {
        if (Object.keys(currentRetrospectives).length === 0) {
            return (<span>No retrospective/s</span>);
        }

        if (Object.keys(currentRetrospectives).length <= 2) {
            const retrospectivesToDisplay = Object.values(currentRetrospectives).orderBy("sortIdentifier");

            return (<span>
                {retrospectivesToDisplay.map(retrospective => renderSelectedRetrospectiveCollapsed(retrospective))}
            </span>);
        }

        return (<span>{Object.keys(currentRetrospectives).length} retrospectives</span>);
    }

    const renderSelectedRetrospectiveCollapsed = (retrospective) => {
        return (<span key={retrospective.id} className="collapsed-selection no-wrap">{retrospective.readableId} ({retrospective.createdOn})</span>);
    }

    const toggleExpanded = (e) => {
        e.preventDefault();
        e.stopPropagation();

        if (activeControlId !== controlId) {
            //cannot be expanded, so expand it

            setActiveControl(controlId);
            setExpanded(true);
            return;
        }

        const shouldBeExpanded = !expanded;
        if (shouldBeExpanded) {
            setActiveControl(controlId);
        } else {
            setActiveControl(null);
        }

        setExpanded(shouldBeExpanded);
    }

    const selectableRetrospectiveDisplay = (retrospective) => {
        const selected = isSelected(retrospective);
        const required = selected && isRequired(retrospective);
        let className = required ? " required": "";
        if (selected){
            className += " selected";
        }

        return (<div key={retrospective.id} className={'selectable' + className} data-id={retrospective.id} onClick={toggleRetrospectiveSelection}>
                    <span className="no-wrap selection-margins">
                        {retrospective.readableId} ({retrospective.createdOn})
                    </span>
                </div>);
    }

    const toggleRetrospectiveSelection = (e) => {
        e.preventDefault();
        e.stopPropagation();
        const id = e.currentTarget.getAttribute("data-id");

        const retrospective = allRetrospectives[id];

        if (isRequired(retrospective)) {
            return;
        }

        let newSelection = { ...currentRetrospectives };
        let singleSelection = maxRetrospectives == 1; /* intentionally a == comparison here, to cater for "1" == 1 */

        if (isSelected(retrospective)) {
            delete newSelection[retrospective.id];
        } else {
            let currentSelectionCount = Object.keys(newSelection).length;

            if (singleSelection) {
                newSelection = { };
                newSelection[retrospective.id] = retrospective;
            } else if (currentSelectionCount >= maxRetrospectives) {
                return;
            } else {
                newSelection[retrospective.id] = retrospective;
            }
        }

        setCurrentRetrospectives(newSelection);
        if (singleSelection) {
            setExpanded(false);
            setActiveControl(null);
        }
    }

    const isRequired = (retrospective) => {
        if (!requiredRetrospectives) {
            return false;
        }

        if (requiredRetrospectives === id || requiredRetrospectives.id === retrospective.id) {
            return true;
        }

        return requiredRetrospectives[retrospective.id];
    }

    const isSelected = (retrospective) => {
        return currentRetrospectives[retrospective.id];
    }

    const expandedDisplay = () => {
        if (error) {
            return (<div>Error: {error}</div>);
        }

        if (allRetrospectives === null) {
            return (<div className="floating-drop-down loading">Loading...</div>);
        }

        const selectedRetrospectives = Object.values(allRetrospectives)
            .filter(retrospective => isSelected(retrospective))
            .orderBy("sortIdentifier");
        const remainingRetrospectives = Object.values(allRetrospectives)
            .filter(retrospective => !isSelected(retrospective) && !retrospective.notFound)
            .orderBy("sortIdentifier");

        return (<div className="floating-drop-down">
            {selectedRetrospectives.map(selectableRetrospectiveDisplay)}
            {remainingRetrospectives.map(selectableRetrospectiveDisplay)}
        </div>);
    }

    const isActiveControl = () => {
        return activeControlId === null || activeControlId === controlId;
    }

	return (
        <div className="drop-down-multi-select" onClick={toggleExpanded}>
            { collapsedDisplay() }
            { expanded && isActiveControl() ? expandedDisplay() : null }
        </div>);
}


const mapStateToProps = (state) => {
	return {
        activeControlId: state.session.activeControlId,
        tenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { setActiveControl })(RetrospectiveSelection);