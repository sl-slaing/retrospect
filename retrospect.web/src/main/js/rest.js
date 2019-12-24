import cookie from 'cookie';
import { rememberDocumentHash } from './helpers';

const handleJsonParseError = (err) => {
    rememberDocumentHash();

    return new Promise((resolve, reject) => {
        if (err.name === "SyntaxError" && err.message === "Unexpected token < in JSON at position 0"){
            reject(new Error("You appear to have been logged out"));
        }  

        reject(err);
    });
}

const handleRequestError = (err) => {
    return new Promise((resolve, reject) => {
        reject(err);
    });
}

const error = (message, response) => {
    let error = new Error(message);
    error.response = response;

    return error;
}

const handleNotOkResponse = (response) => {
    return new Promise((resolve, reject) => {
        if (response.status === 404) {
            reject(error(`Requested URL ${response.url} was not found`, response));
        }

        if (response.status === 500) {
            reject(error(`Requested URL ${response.url} failed`, response));
        }

        reject(error(`Requested URL ${response.url} returned ${response.status}`, response));
    });
}

export const Post = (url, body, ignoreResponse = false) => {
    return fetch(url,
        {
            method: "POST",
            headers: {
                "X-XSRF-TOKEN": cookie.parse(document.cookie)["XSRF-TOKEN"],
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
        .then(
            response => {
                if (!response.ok) {
                    return handleNotOkResponse(response);
                }

                if (ignoreResponse) {
                    return response;
                }

                return response.json()
                    .then(
                        json => json,
                        handleJsonParseError);
            },
            handleRequestError)
}

export const Delete = (url, body) => {
    return fetch(url,
        {
            method: "DELETE",
            headers: {
                "X-XSRF-TOKEN": cookie.parse(document.cookie)["XSRF-TOKEN"],
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        })
        .then(
            response => {
                if (!response.ok) {
                    return handleNotOkResponse(response);
                }

                return response;
            }, 
            handleRequestError);
};

export const Get = (url) => {
    return fetch(url)
        .then(
            response => {
                if (!response.ok) {
                    return handleNotOkResponse(response);
                }

                return response.json()
                    .then(
                        json => json,
                        handleJsonParseError);
            },
            err => handleRequestError(err));
};
