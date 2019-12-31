import React, { useState } from 'react';
import { connect } from 'react-redux';

import { Post } from './rest'; 
import { setRetrospectives } from './redux/retrospectivesActions';
import { saveFile, openFile } from './helpers';

import Error from './Error';
import Working from './Working';

const SystemAdministration = ({ tenant, setRetrospectives }) => {
    const [ mode, setMode ] = useState("main");
    const [ error, setError ] = useState(null);
    const [ importResult, setImportResult ] = useState(null);
    const [ exportDeleted, setExportDeleted ] = useState(false);
    const [ importDeleted, setImportDeleted ] = useState(false);
    const [ importDryRun, setImportDryRun ] = useState(true);
    const [ restoreMode, setRestoreMode ] = useState(false);
    const [ importRequest, setImportRequest ] = useState(null);

    const backToAdministration = (e) => {
        e.preventDefault();

        setRetrospectives(null); //to invalidate the cached data for this retrospective
        setMode("main");
    }

    const exportAll = (e) => {
        e.preventDefault(e);
        setMode("exporting");

        Post(tenant, '/export',
            {
                ids: [ ],
                version: '1.0',
                settings: {
                    includeDeleted: true
                }
            },
            true)
            .then(
                response => {
                    response.text().then(
                        text => {
                            saveFile(`Retrospect.json`, text, 'text/plain');
                            setMode('main');
                        },
                        err => {
                            setError(err);
                        });
                },
                err => {
                    setError(err);
                }
            )
    }

    const executeImport = (e) => {
        e.preventDefault();
        setImportResult(null);

        openFile('.json', true)
            .then(
                files => {
                    const versions = {};
                    const retrospectives = [];
                    Object.values(files).forEach(file => {
                        var fileContent = JSON.parse(file.content);
                        if (!fileContent.retrospectives) {
                            return;
                        }

                        if (versions[fileContent.version]) {
                            versions[fileContent.version] = versions[fileContent.version] + 1
                        } else {
                            versions[fileContent.version] = 1;
                        }

                        fileContent.retrospectives.forEach(retroJson => {
                            retrospectives.push(retroJson);
                        });
                    });

                    if (Object.keys(versions).length > 1) {
                        setError({ message: "Unable to import with different versions of exported data" });
                        return;
                    }

                    const request = {
                        version: Object.keys(versions)[0],
                        retrospectives: retrospectives,
                        settings: {
                            permitMerge: true,
                            restoreData: restoreMode,
                            restoreDeleted: importDeleted,
                            dryRun: importDryRun
                        }
                    };

                    executeImportRequest(request);
                });  
    }

    const repeatImportWithoutDryRun = (e) => {
        const request = { ...importRequest };
        request.settings.dryRun = false;

        executeImportRequest(request);
    }

    const onExportDeletedChange = (e) => {
        setExportDeleted(e.currentTarget.checked);
    }

    const onImportDeletedChange = (e) => {
        setImportDeleted(e.currentTarget.checked);
    }

    const onImportDryRunChange = (e) => {
        setImportDryRun(e.currentTarget.checked);
    }

    const onRestoreModeChange = (e) => {
        setRestoreMode(e.currentTarget.checked);
    }

    const executeImportRequest = (request) => {
        setImportRequest(request);
        setMode("importing");

        Post(tenant, '/import',
            request)
            .then(
                importResultJson => {
                    setImportResult(importResultJson);
                    setMode('view-import-result');
                },
                err => {
                    setError(err);
                });
    }

    if (error !== null) {
        return (<Error error={error} recover={recover} recoverText="Review settings..." />);
    }

    if (mode === "exporting") {
        return (<Working message="Exporting data..." />);
    }

    if (mode === "importing") {
        return (<Working message="Importing data..." />);
    }
    
    if (mode === "view-import-result") {
        return (<div className="vertically-centered visible-overflow">
                    <div className="white-panel">
                        <h3>Import result</h3>
                        <h5>General messages</h5>
                        <ol>
                            {importResult.generalMessages.map((msg, index) => (<li key={index}>{msg}</li>))}
                        </ol>
                        <h5>Per retrospective messages</h5>
                        <ol>
                            {Object.keys(importResult.messages).map((key) => (<li key={key}>{importResult.messages[key]}</li>))}
                        </ol>
                        <div className="buttons green-top-border ">
                            {importRequest.settings.dryRun ? (<a className="button clickable" onClick={repeatImportWithoutDryRun}>Apply changes...</a>) : null}
                            <a className="button clickable" onClick={backToAdministration}>Back to administration</a>
                        </div>
                    </div>
                </div>);
    }

    return (<div className="vertically-centered visible-overflow">
                <div className="white-panel">
                    <h3>System Administration</h3>
                    <div>
                        <h4>Export</h4>
                        <div>
                            <label className="block-display">
                                <input type="checkbox" checked={exportDeleted} onChange={onExportDeletedChange} />
                                Export deleted data?
                            </label>
                        </div>
                        <div>
                            To export one retrospective, open the administration for the retrospective and click 'Export'
                        </div>
                    </div>
                    <div className="buttons green-top-border ">
                        <a className="button clickable" onClick={exportAll}>Export all data...</a>
                    </div>
                    <div>
                        <h4>Import</h4>
                        <div>
                            <label>
                                <input type="checkbox" checked={importDeleted} onChange={onImportDeletedChange} />
                                Import deleted data?
                            </label>
                        </div>
                        <div>
                            <label>
                                <input type="checkbox" checked={importDryRun} onChange={onImportDryRunChange} />
                                Test the import?
                            </label>
                        </div>
                        <div>
                            <label>
                                <input type="checkbox" checked={restoreMode} onChange={onRestoreModeChange} />
                                Replace all data (restore mode)
                            </label>
                        </div>
                    </div>
                    <div className="buttons green-top-border ">
                        <a className="button clickable" onClick={executeImport}>Select file/s and execute...</a>
                    </div>
                </div>
            </div>);
}



const mapStateToProps = (state) => {
	return {
        tenant: state.session.selectedTenant
	}
}

export default connect(mapStateToProps, { setRetrospectives })(SystemAdministration);
