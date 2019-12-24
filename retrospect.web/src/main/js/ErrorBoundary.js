import React from 'react';
import { connect } from 'react-redux';

import Error from './Error';

class ErrorBoundary extends React.Component {
    constructor(props){
        super(props);
        this.state = { error: null };
    }

    static getDerivedStateFromError(error) {
        return { error: error };
    }

    componentDidCatch(error, errorInfo) {
        //console.error(error);
        //console.error(errorInfo);
    }

    render() {
        if (this.state.error) {
            //console.log(this.state.error);
            return (<Error error={this.state.error} />);
        }

        return this.props.children; 
    }
}

export default ErrorBoundary;