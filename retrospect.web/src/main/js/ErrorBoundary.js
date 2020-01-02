import React from 'react';

import Error from './Error';

class ErrorBoundary extends React.Component {
    constructor(props){
        super(props);
        this.state = { error: null };
    }

    static getDerivedStateFromError(error) {
        return { error: error };
    }

    render() {
        if (this.state.error) {
            return (<Error error={this.state.error} />);
        }

        return this.props.children; 
    }
}

export default ErrorBoundary;