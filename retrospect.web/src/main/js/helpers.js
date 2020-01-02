const PRE_LOGIN_HASH_KEY = "pre-login-hash";
const TENANT_KEY_PREFIX = "tenant-selection";

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

export const saveFile = (filename, fileContent, fileType) => {
    const file = new Blob([ fileContent ], { type: fileType });
    const a = document.createElement("A");
    const objectUrl = URL.createObjectURL(file);
    a.href = objectUrl;
    a.download = filename;
    document.body.append(a);
    a.click();
    setTimeout(function() {
        document.body.removeChild(a);
        URL.revokeObjectURL(objectUrl);
    });
}

export const openFile = (accept, multiple) => {
    return new Promise((resolve, reject) => {
        const input = document.createElement("INPUT");
        input.type = "file";
        if (multiple) {
            input.multiple = true;
        }
        if (accept) {
            input.accept = accept;
        }
        document.body.appendChild(input);
        input.addEventListener("change", function(e) {
            const files = e.target.files;
            var toLoad = files.length;
            if (!toLoad) {
                return; //NOTE: Reject isn't called as it makes it consistent with when the 'Cancel' button is clicked in the open dialog, which cannot be detected.
            }

            const fileContents = {};

            for (var index = 0; index < files.length; index++) {
                const file = files[index];
                const reader = new FileReader();
                reader.onload = function (loader) {
                    const contents = loader.target.result;
                    fileContents[file.name] = {
                        name: file.name,
                        size: file.size,
                        type: file.type,
                        content: contents
                    };
                    toLoad--;

                    if (toLoad === 0) {
                        resolve(fileContents);
                    }
                };
                reader.readAsText(file);
            }
        });

        input.click();

        setTimeout(function() {
            document.body.removeChild(input);
        });
    });
}

const getTenantStorageKey = (username) => {
    return `${TENANT_KEY_PREFIX}:${username}`;
}

export const rememberTenant = (username, tenant) => {
    const storageKey = getTenantStorageKey(username);
    window.localStorage.setItem(storageKey, tenant.id);
}

export const forgetTenant = (username) => {
    const storageKey = getTenantStorageKey(username);
    window.localStorage.removeItem(storageKey);
}

export const retrieveRememberedTenant = (username) => {
    const storageKey = getTenantStorageKey(username);
    const tenantId = window.localStorage.getItem(storageKey);
    return tenantId;
}