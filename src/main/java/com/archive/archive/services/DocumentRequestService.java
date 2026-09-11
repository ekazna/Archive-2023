package com.archive.archive.services;
import java.time.LocalDate;
import java.util.List;

import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.exceptions.InvalidRequestStateException;
import com.archive.archive.models.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.archive.archive.config.IAuthenticationFacade;
import com.archive.archive.repositories.EmployeeRepo;
import com.archive.archive.repositories.DocumentRequestRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DocumentRequestService {
    private final DocumentRequestRepo documentRequestRepo;
    private final EmployeeRepo employeeRepo;
    private final DocService docService;
    private final DocActionService docActionService;
    private final IAuthenticationFacade authenticationFacade;

    public DocumentRequestService(DocumentRequestRepo documentRequestRepo,
                                  EmployeeRepo employeeRepo,
                                  DocService docService,
                                  DocActionService docActionService,
                                  IAuthenticationFacade authenticationFacade) {
        this.documentRequestRepo = documentRequestRepo;
        this.employeeRepo = employeeRepo;
        this.docService= docService;
        this.docActionService = docActionService;
        this.authenticationFacade = authenticationFacade;
    }


    public Employee getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return employeeRepo.findByLogin(authentication.getName());
    }


    @Transactional
    public DocumentRequest createRequest(RequestType requestType, Integer docId) {
        Employee employee = getCurrentUser();

        Doc doc = docService.getById(docId);

        DocumentRequest request = new DocumentRequest();

        request.setDoc(doc);
        request.setEmployee(employee);
        request.setRequestDate(LocalDate.now());

        request.setRequestType(requestType);
        request.setRequestStatus(RequestStatus.REQUESTED);

        DocumentRequest savedRequest = documentRequestRepo.save(request);

        if (requestType == RequestType.COPY) {
            docActionService.recordAction(
                    DocumentActionType.REQUESTED_COPY, docId, employee);
        } else {
            docActionService.recordAction(
                    DocumentActionType.REQUESTED_ORIGINAL, docId, employee);
        }

        return savedRequest;
    }

    @Transactional
    public void issueOriginal(Integer id) {

        DocumentRequest request = documentRequestRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document request", id));

        if (request.getRequestType() != RequestType.ORIGINAL){
            throw new InvalidRequestStateException("Выдать можно только оригиналы");
        }
        if (request.getRequestStatus() != RequestStatus.REQUESTED){
            throw new InvalidRequestStateException("Можно выдать только запрошенные оригиналы");
        }

        Doc doc = request.getDoc();

        doc.setPresent(false);

        request.setRequestStatus(RequestStatus.ISSUED);

        docActionService.recordAction(
                DocumentActionType.ISSUED_ORIGINAL,
                doc.getId(),
                getCurrentUser()
        );

        docActionService.recordAction(
                DocumentActionType.TOOK_ORIGINAL,
                doc.getId(),
                request.getEmployee()
        );
    }

    @Transactional
    public void completeCopyRequest(Integer id) {

        DocumentRequest request = documentRequestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document request", id));

        if (request.getRequestType() != RequestType.COPY){
            throw new InvalidRequestStateException("Так можно отправить только копию");
        }
        if (request.getRequestStatus() != RequestStatus.REQUESTED) {
            throw new InvalidRequestStateException("Только запрошенные копии можно отправить");
        }

        request.setRequestStatus(RequestStatus.COMPLETED);

        docActionService.recordAction(
                DocumentActionType.SATISFIED_REQUEST, request.getDoc().getId(), getCurrentUser()
        );
    }

    @Transactional
    public void returnOriginal(Integer id) {

        DocumentRequest request = documentRequestRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document request", id));

        if (request.getRequestType() != RequestType.ORIGINAL) {
            throw new InvalidRequestStateException(
                    "Вернуть можно только оригинал"
            );
        }

        if (request.getRequestStatus() != RequestStatus.ISSUED) {
            throw new InvalidRequestStateException(
                    "Оригинал должен быть выдан перед возвращением его в архив"
            );
        }


        Doc doc = request.getDoc();

        doc.setPresent(true);

        request.setRequestStatus(RequestStatus.COMPLETED);

        docActionService.recordAction(
                DocumentActionType.ACCEPTED_ORIGINAL,
                doc.getId(),
                getCurrentUser()
        );

        docActionService.recordAction(
                DocumentActionType.RETURNED_ORIGINAL,
                doc.getId(),
                request.getEmployee()
        );
    }




    public List<DocumentRequest> findByTypeAndStatusAndSort(
            RequestType requestType,
            RequestStatus requestStatus,
            OrderSorting orderSorting
    ) {
        String sortType = orderSorting.getSortType();

        if (sortType == null) {
            return documentRequestRepo.findByRequestTypeAndRequestStatus(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("employeeEmail")) {
            return documentRequestRepo.findByRequestTypeAndRequestStatusOrderByEmployee_Email(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("docName")) {
            return documentRequestRepo.findByRequestTypeAndRequestStatusOrderByDoc_Name(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("docFolder")) {
            return documentRequestRepo.findByRequestTypeAndRequestStatusOrderByDoc_Folder(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("orderDate")) {
            return documentRequestRepo.findByRequestTypeAndRequestStatusOrderByOrderDate(
                    requestType,
                    requestStatus
            );
        }

        return documentRequestRepo.findByRequestTypeAndRequestStatus(
                requestType,
                requestStatus
        );
    }
    }