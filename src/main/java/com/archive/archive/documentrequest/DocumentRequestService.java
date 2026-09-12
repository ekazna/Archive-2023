package com.archive.archive.documentrequest;
import java.time.LocalDate;
import java.util.List;

import com.archive.archive.audit.DocumentActionType;
import com.archive.archive.document.Doc;
import com.archive.archive.document.DocumentStatus;
import com.archive.archive.documentrequest.dto.DocumentRequestFilter;
import com.archive.archive.documentrequest.dto.DocumentRequestResponse;
import com.archive.archive.employee.Employee;
import com.archive.archive.employee.Role;
import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.exceptions.InvalidRequestStateException;
import com.archive.archive.security.CurrentUserService;
import com.archive.archive.audit.DocActionService;
import com.archive.archive.document.DocService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DocumentRequestService {
    private final DocumentRequestRepo documentRequestRepo;
    private final DocService docService;
    private final DocActionService docActionService;
    private final DocumentRequestMapper documentRequestMapper;
    private final CurrentUserService currentUserService;


    public DocumentRequestService(DocumentRequestRepo documentRequestRepo,
                                  DocService docService,
                                  DocActionService docActionService,
                                  DocumentRequestMapper documentRequestMapper,
                                  CurrentUserService currentUserService) {
        this.documentRequestRepo = documentRequestRepo;
        this.docService= docService;
        this.docActionService = docActionService;
        this.documentRequestMapper = documentRequestMapper;
        this.currentUserService = currentUserService;
    }




    @Transactional
    public DocumentRequestResponse createRequest(RequestType requestType, Integer docId) {
        Employee employee = currentUserService.getCurrentEmployee();

        Doc doc = docService.getAccessibleActiveDocument(docId);

        boolean duplicateExists =
                documentRequestRepo
                        .existsByDoc_IdAndEmployee_IdAndRequestTypeAndRequestStatusIn(
                                doc.getId(),
                                employee.getId(),
                                requestType,
                                List.of(
                                        RequestStatus.REQUESTED,
                                        RequestStatus.ISSUED
                                )
                        );

        if (duplicateExists) {
            throw new InvalidRequestStateException(
                    "An active request of this type already exists for this document"
            );
        }

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

        return documentRequestMapper.toResponse(savedRequest);
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

        if(!Boolean.TRUE.equals(doc.getPresent())){
            throw new InvalidRequestStateException(
                    "Документ уже выдан"
            );
        }

        if (doc.getStatus() != DocumentStatus.ACTIVE) {
            throw new InvalidRequestStateException(
                    "Disposed document cannot be issued"
            );
        }

        doc.setPresent(false);

        request.setRequestStatus(RequestStatus.ISSUED);

        docActionService.recordAction(
                DocumentActionType.ISSUED_ORIGINAL,
                doc.getId(),
                currentUserService.getCurrentEmployee()
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

        if (request.getDoc().getStatus() != DocumentStatus.ACTIVE) {
            throw new InvalidRequestStateException(
                    "Request cannot be completed for a disposed document"
            );
        }

        request.setRequestStatus(RequestStatus.COMPLETED);

        docActionService.recordAction(
                DocumentActionType.SATISFIED_REQUEST, request.getDoc().getId(), currentUserService.getCurrentEmployee()
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
                currentUserService.getCurrentEmployee()
        );

        docActionService.recordAction(
                DocumentActionType.RETURNED_ORIGINAL,
                doc.getId(),
                request.getEmployee()
        );
    }

    @Transactional(readOnly = true)
    public DocumentRequestResponse getById(Integer id){

        Specification<DocumentRequest> specification =
                DocumentRequestSpecification.hasId(id)
                        .and(accessibleToCurrentUser());

        DocumentRequest request =
                documentRequestRepo.findOne(specification)
                        .orElseThrow(() -> new ResourceNotFoundException("Document request", id));

        return documentRequestMapper.toResponse(request);
    }



    @Transactional(readOnly = true)
    public Page<DocumentRequestResponse> getAll(DocumentRequestFilter filter, Pageable pageable){

        Specification<DocumentRequest> specification =
                DocumentRequestSpecification.withFilter(filter)
                        .and(accessibleToCurrentUser());


        return documentRequestRepo
                .findAll(specification, pageable)
                .map(documentRequestMapper::toResponse);
    }

    private Specification<DocumentRequest> accessibleToCurrentUser() {

        Employee currentUser = currentUserService.getCurrentEmployee();

        if (currentUser.getRole() == Role.ADMIN) {
            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.conjunction();
        }

        return DocumentRequestSpecification.requestedBy(
                currentUser.getId()
        );
    }
}