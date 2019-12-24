export const PRE_LOGIN_HASH_KEY = "pre-login-hash";

export const getDocumentHash = () => {
    if (!document.location.hash){
        restoreDocumentHash();
    }

    return document.location.hash
        ? parseDocumentHash(document.location.hash.substring(1))
        : null;
}

export const setDocumentHash = (additionalData) => {
    const currentData = getDocumentHash() || { };
    const newData = {
        ...currentData,
        ...additionalData
    };

    document.location.hash = serialiseDocumentHash(newData);
}

export const removeFromDocumentHash = (name) => {
    const currentData = getDocumentHash();
    if (currentData == null || !currentData[name]){
        return;
    }

    let newData = { ...currentData };
    delete newData[name];

    document.location.hash = serialiseDocumentHash(newData);
}

export const rememberDocumentHash = () => {
    if (!document.location.hash) {
        forgetDocumentHash();
        return;
    }

    window.localStorage.setItem(PRE_LOGIN_HASH_KEY, document.location.hash.substring(1));
}

export const forgetDocumentHash = () => {
    window.localStorage.removeItem(PRE_LOGIN_HASH_KEY);
}

export const restoreDocumentHash = () => {
    var rememberedHash = window.localStorage.getItem(PRE_LOGIN_HASH_KEY);
    window.localStorage.removeItem(PRE_LOGIN_HASH_KEY);
    if (rememberedHash) {
        document.location.hash = rememberedHash;
    }
    return rememberedHash;
}

const serialiseDocumentHash = (data) => {
    if (data && Object.keys(data).length >= 1) {
        if (Object.keys(data).length === 1 && Object.keys(data)[0] === "id") {
            return data["id"];
        }

        return JSON.stringify(data);
    }

    return "";
}

const parseDocumentHash = (potentialJson) => {
    if (!potentialJson) {
        return null;
    }

    if (potentialJson.indexOf("{") === 0) {
        return JSON.parse(decodeURIComponent(potentialJson));
    }

    return {
        id: potentialJson
    };
}

export const defineHelperFunctions = () => {
    if (window.helperFunctionsDefined) {
        return;
    }

    window.helperFunctionsDefined = true;

    Array.prototype.orderBy = function (propertyName) {
        let copyOfArray = this.slice();
       
        let sortFunction = propertyName instanceof Function
            ? propertyName
            : (a, b) => {
                const aValue = a[propertyName];
                const bValue = b[propertyName];
        
                if (aValue === bValue){
                    return 0;
                }
        
                if (aValue instanceof String){
                    return aValue.localeCompare(bValue);
                }
        
                return aValue > bValue ? 1 : -1;
            };

        copyOfArray.sort(sortFunction);
    
        return copyOfArray;
    };
}