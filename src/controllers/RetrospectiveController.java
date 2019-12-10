package controllers;

import managers.RetrospectiveSecurityManager;
import managers.UserSessionManager;
import models.HttpRequestWrapper;
import models.HttpResponseWrapper;
import models.Retrospective;
import repositories.RetrospectiveRepository;
import repositories.UserRepository;
import serialisers.*;
import services.RetrospectiveService;

public class RetrospectiveController {
    private final RetrospectiveService service;
    private final UserSessionManager userSessionManager;

    public RetrospectiveController() {
        UserRepository userRepository = new UserRepository();
        UserSerialiser userSerialiser = new UserSerialiser(userRepository);
        AuditSerialiser auditSerialiser = new AuditSerialiser(userSerialiser);

        this.service = new RetrospectiveService(
                userRepository,
                new RetrospectiveSecurityManager(),
                new RetrospectiveRepository(
                        new RetrospectiveSerialiser(
                                new ActionSerialiser(auditSerialiser, userSerialiser),
                                new ObservationSerialiser(auditSerialiser, userSerialiser),
                                auditSerialiser,
                                userSerialiser))
        );
        this.userSessionManager = new UserSessionManager(
                userRepository,
                new HttpRequestWrapper(), //TODO: Scoped to each request
                new HttpResponseWrapper()  //TODO: Scoped to each request
        );
    }

    public Object index(String id){
        Retrospective retrospective = this.service.getRetrospective(id, userSessionManager.getLoggedInUser());
        if (retrospective == null){
            throw new RuntimeException("Not found");
        }

        return retrospective; //JSON-ify
    }
}
