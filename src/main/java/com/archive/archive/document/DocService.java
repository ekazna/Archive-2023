package com.archive.archive.document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.archive.archive.audit.DocumentActionType;
import com.archive.archive.document.dto.DocumentCreateRequest;
import com.archive.archive.document.dto.DocumentFilter;
import com.archive.archive.document.dto.DocumentUpdateRequest;
import com.archive.archive.employee.Employee;
import com.archive.archive.employee.EmployeeRepo;
import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.legacy.filter.TestModel;
import com.archive.archive.legacy.filter.TestModelSpecification;
import com.archive.archive.reference.*;
import com.archive.archive.security.CurrentUserService;
import com.archive.archive.audit.DocActionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


    public DocService(
            DocRepo docRepo,
            DocTypeRepo docTypeRepo,
            EmployeeRepo employeeRepo,
            DocActionService docActionService,
            DepartmentRepo departmentRepo,
            ClientRepo clientRepo,
            CurrentUserService currentUserService
    ) {
        this.docRepo = docRepo;
        this.docTypeRepo = docTypeRepo;
        this.employeeRepo = employeeRepo;
        this.docActionService = docActionService;
        this.departmentRepo = departmentRepo;
        this.clientRepo = clientRepo;
        this.currentUserService = currentUserService;
    }


    
    public List<Doc> getAll(){
        return docRepo.findAll();
    }


    public Page<Doc> getAll(DocumentFilter filter, Pageable pageable){

        Specification<Doc> specification = DocumentSpecification.withFilter(filter)
                .and(DocumentSpecification.isActive())
                .and(accessibleToCurrentUser());

        return docRepo.findAll(specification, pageable);
    }

    @Deprecated
    public List<Doc> getFilteredSpecification(TestModel testModel){
        Employee emp = currentUserService.getCurrentEmployee();
        Integer deptId = emp.getDepartment().getId();
        if (deptId == 13){
            testModel.setAccessLevel(10000);
        } else{
            Integer empAccessLevel = emp.getAccessLevel();
            testModel.setAccessLevel(empAccessLevel);
        }

        if (testModel.getSortingType() == 0){
            TestModelSpecification tspec = new TestModelSpecification(testModel);
            return docRepo.findAll(tspec, Sort.by(Sort.Direction.ASC, testModel.getSortBy()));
        }else{
            TestModelSpecification tspec = new TestModelSpecification(testModel);
            return docRepo.findAll(tspec, Sort.by(Sort.Direction.DESC, testModel.getSortBy()));
        }
        
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


    @Deprecated
    @Transactional
    public void save(Doc doc){
        doc.setDateAdded(LocalDate.now());
        if (doc.getClient().getId() == null){
            doc.setClient(null);
        }
        if (doc.getDocEmployee().getId() == null){
            doc.setDocEmployee(null);
        }
        doc.setPresent(true);

        Integer docTypeId = doc.getDocType().getId();
        DocType docType = docTypeRepo.findById(docTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Document type", docTypeId));

        Integer storingTime = docType.getStoringTime();

        if (storingTime != null){
            doc.setDeletionDate(doc.getDocDate().plusYears(storingTime));        }

        docRepo.save(doc);


    }

    @Transactional
    public void dispose(Integer id){
        Doc doc = getById(id);

        doc.setStatus(DocumentStatus.DISPOSED);

        docActionService.recordAction(DocumentActionType.DELETED, doc.getId(), currentUserService.getCurrentEmployee());
    }




    @Transactional
    public Doc update(Integer id, DocumentUpdateRequest request){

        Doc doc = getById(id);

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




    public List<Doc> getDocsToDelete(){
        LocalDate today = LocalDate.now();
        return docRepo.getDocsToDelete(today);
    }





     //////////////////////////////////////////////////////////////////////////

     public HashMap<String, Long> deptStatistics(){
        List<Object[]> objList = docRepo.deptStatistics();
        HashMap<String, Long> statsList = new HashMap<>();
        for (Object[] obj : objList) {
            statsList.put((String) obj[0], (Long) obj[1]);
        }
        return statsList;
    }

    //////////////////////////////////////////////////////////////////////////


    private Specification<Doc> accessibleToCurrentUser() {
        Employee currentUser = currentUserService.getCurrentEmployee();

        return DocumentSpecification.accessibleTo(
                currentUser.getAccessLevel()
        );
    }

    public Doc getById(Integer id){

        Specification<Doc> specification =
                DocumentSpecification.hasId(id)
                        .and(DocumentSpecification.isActive())
                        .and(accessibleToCurrentUser());
        return docRepo.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
    }

    public void validateAccessLevel(Integer requestedAccessLevel){
        Employee currentUser = currentUserService.getCurrentEmployee();

        if (requestedAccessLevel > currentUser.getAccessLevel()){
            throw new AccessDeniedException("Вы не можете получить доступ к документу с этим Access Level");
        }

    }

}
