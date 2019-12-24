import React from 'react';

const Working = ({ message }) => {
    return (<div className="vertically-centered">
                <div className="white-panel loading">
                    {message || 'Working...'}
                </div>
            </div>);
}

export default Working;