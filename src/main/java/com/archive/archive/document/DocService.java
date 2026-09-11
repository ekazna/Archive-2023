package com.archive.archive.document;

import java.time.LocalDate;
import java.util.List;

import com.archive.archive.audit.DocumentActionType;
import com.archive.archive.document.dto.DocumentCreateRequest;
import com.archive.archive.document.dto.DocumentDetailsResponse;
import com.archive.archive.document.dto.DocumentFilter;
import com.archive.archive.document.dto.DocumentUpdateRequest;
import com.archive.archive.documentrequest.DocumentRequestRepo;
import com.archive.archive.documentrequest.RequestStatus;
import com.archive.archive.employee.Employee;
import com.archive.archive.employee.EmployeeRepo;
import com.archive.archive.exceptions.InvalidRequestStateException;
import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.reference.*;
import com.archive.archive.security.CurrentUserService;
import com.archive.archive.audit.DocActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocService {
    private final DocRepo docRepo;
    private final DocTypeRepo docTypeRepo;
    private final EmployeeRepo employeeRepo;
    private final DocActionService docActionService;
    private final DepartmentRepo departmentRepo;
    private final ClientRepo clientRepo;
    private final CurrentUserService currentUserService;
    private final DocumentRequestRepo documentRequestRepo;


    public DocService(
            DocRepo docRepo,
            DocTypeRepo docTypeRepo,
            EmployeeRepo employeeRepo,
            DocActionService docActionService,
            DepartmentRepo departmentRepo,
            ClientRepo clientRepo,
            CurrentUserService currentUserService,
            DocumentRequestRepo documentRequestRepo
    ) {
        this.docRepo = docRepo;
        this.docTypeRepo = docTypeRepo;
        this.employeeRepo = employeeRepo;
        this.docActionService = docActionService;
        this.departmentRepo = departmentRepo;
        this.clientRepo = clientRepo;
        this.currentUserService = currentUserService;
        this.documentRequestRepo = documentRequestRepo;
    }


    public Page<Doc> getAll(DocumentFilter filter, Pageable pageable){

        Specification<Doc> specification = DocumentSpecification.withFilter(filter)
                .and(DocumentSpecification.isActive())
                .and(accessibleToCurrentUser());

        return docRepo.findAll(specification, pageable);
    }


    @Transactional
    public Doc create(DocumentCreateRequest request){

        validateAccessLevel(request.accessLevel());

        DocType docType = docTypeRepo.findById(request.docTypeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Document type", request.docTypeId())
                );
        Department department = departmentRepo.findById(request.departmentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Department", request.departmentId())
                );
        Employee fromEmployee = employeeRepo.findById(request.fromEmployeeId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee", request.fromEmployeeId())
                );

        Client client = null;
        if(request.clientId() != null){
            client = clientRepo.findById(request.clientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", request.clientId()));
        }
        Employee docEmployee = null;

        if (request.docEmployeeId() != null) {
            docEmployee = employeeRepo.findById(request.docEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.docEmployeeId()));
        }


        Doc doc = new Doc();

        doc.setName(request.name());
        doc.setFolder(request.folder());
        doc.setDocDate(request.docDate());
        doc.setAccessLevel(request.accessLevel());

        doc.setDocType(docType);
        doc.setDepartment(department);
        doc.setFromEmployee(fromEmployee);
        doc.setClient(client);
        doc.setDocEmployee(docEmployee);

        doc.setPresent(true);
        doc.setDateAdded(LocalDate.now());
        doc.setStatus(DocumentStatus.ACTIVE);

        LocalDate deletionDate = request.deletionDate();

        if (deletionDate == null && docType.getStoringTime() != null){
            deletionDate = request.docDate().plusYears(docType.getStoringTime());
        }

        doc.setDeletionDate(deletionDate);

        Doc savedDoc = docRepo.save(doc); // нужен т.к. Doc doc = new Doc() новый transient object

        docActionService.recordAction(
                DocumentActionType.ADDED,
                savedDoc.getId(),
                currentUserService.getCurrentEmployee()
        );

        return savedDoc;

    }


    @Transactional
    public void dispose(Integer id){

        Doc doc = getAccessibleActiveEntity(id);

        if (!Boolean.TRUE.equals(doc.getPresent())){
            throw new InvalidRequestStateException(
                    "Невозможно удалить документ пока оригинал не архиве (issued)"
            );
        }

        boolean hasActiveRequests = documentRequestRepo.existsByDoc_IdAndRequestStatusIn(
                id, List.of(RequestStatus.REQUESTED, RequestStatus.ISSUED));

        if (hasActiveRequests){
            throw new InvalidRequestStateException("Нельзя удалить документ пока есть активные requests");
        }

        doc.setStatus(DocumentStatus.DISPOSED);

        docActionService.recordAction(DocumentActionType.DISPOSED, doc.getId(), currentUserService.getCurrentEmployee());
    }


    @Transactional
    public Doc update(Integer id, DocumentUpdateRequest request){

        Doc doc = getAccessibleActiveDocument(id);

        validateAccessLevel(request.accessLevel());

        DocType docType = docTypeRepo.findById(request.docTypeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document type", request.docTypeId()));

        Department department = departmentRepo.findById(request.departmentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Department", request.departmentId()));

        Employee fromEmployee = employeeRepo.findById(request.fromEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.fromEmployeeId()));

        Client client = null;
        if (request.clientId() != null) {
            client = clientRepo.findById(request.clientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", request.clientId()));
        }

        Employee docEmployee = null;
        if (request.docEmployeeId() != null) {
            docEmployee = employeeRepo.findById(request.docEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.docEmployeeId()));
        }

        doc.setName(request.name());
        doc.setFolder(request.folder());
        doc.setDocDate(request.docDate());
        doc.setAccessLevel(request.accessLevel());

        doc.setDocType(docType);
        doc.setDepartment(department);
        doc.setFromEmployee(fromEmployee);
        doc.setClient(client);
        doc.setDocEmployee(docEmployee);

        LocalDate deletionDate = request.deletionDate();

        if (deletionDate == null && docType.getStoringTime() != null) {
            deletionDate =
                    request.docDate().plusYears(docType.getStoringTime());
        }

        doc.setDeletionDate(deletionDate);

        docActionService.recordAction(
                DocumentActionType.EDITED,
                doc.getId(),
                currentUserService.getCurrentEmployee()
        );

        return doc;
    }

    @Transactional(readOnly = true)
    public DocumentDetailsResponse getById(Integer id) {
        Doc doc = getAccessibleActiveEntity(id);
        return toDetailsResponse(doc);
    }

    @Transactional(readOnly = true)
    public Doc getAccessibleActiveDocument(Integer id) {
        return getAccessibleActiveEntity(id);
    }


    public Page<Doc> getExpired(Pageable pageable) {

        Specification<Doc> specification =
                DocumentSpecification.expiredAsOf(LocalDate.now())
                        .and(DocumentSpecification.isActive())
                        .and(accessibleToCurrentUser());

        return docRepo.findAll(specification, pageable);
    }


    private Specification<Doc> accessibleToCurrentUser() {
        Employee currentUser = currentUserService.getCurrentEmployee();

        return DocumentSpecification.accessibleTo(
                currentUser.getAccessLevel()
        );
    }

    public void validateAccessLevel(Integer requestedAccessLevel){
        Employee currentUser = currentUserService.getCurrentEmployee();

        if (requestedAccessLevel > currentUser.getAccessLevel()){
            throw new AccessDeniedException("Вы не можете получить доступ к документу с этим Access Level");
        }

    }

    private DocumentDetailsResponse toDetailsResponse(Doc doc) {
        return new DocumentDetailsResponse(
                doc.getId(),
                doc.getName(),
                doc.getFolder(),
                doc.getDocDate(),
                doc.getDateAdded(),
                doc.getDeletionDate(),
                doc.getAccessLevel(),
                doc.getPresent(),
                doc.getStatus(),

                doc.getDepartment().getId(),
                doc.getDepartment().getName(),

                doc.getDocType().getId(),
                doc.getDocType().getName(),

                doc.getClient() != null ? doc.getClient().getId() : null,
                doc.getClient() != null ? doc.getClient().getName() : null,

                doc.getFromEmployee().getId(),
                doc.getFromEmployee().getLogin(),

                doc.getDocEmployee() != null ? doc.getDocEmployee().getId() : null,
                doc.getDocEmployee() != null ? doc.getDocEmployee().getLogin() : null
        );
    }

    private Doc getAccessibleActiveEntity(Integer id) {
        Specification<Doc> specification =
                DocumentSpecification.hasId(id)
                        .and(DocumentSpecification.isActive())
                        .and(accessibleToCurrentUser());

        return docRepo.findOne(specification)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", id)
                );
    }

}
