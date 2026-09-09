package com.archive.archive.services;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.models.*;
import com.archive.archive.repositories.*;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.archive.archive.config.IAuthenticationFacade;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DocService {
    private final DocRepo docRepo;
    private final DocTypeRepo docTypeRepo;
    private final EmployeeRepo employeeRepo;
    private final DocActionService docActionService;
    private final IAuthenticationFacade authenticationFacade;
    private final DepartmentRepo departmentRepo;
    private final ClientRepo clientRepo;


    public DocService(
            DocRepo docRepo,
            DocTypeRepo docTypeRepo,
            EmployeeRepo employeeRepo,
            DocActionService docActionService,
            IAuthenticationFacade authenticationFacade,
            DepartmentRepo departmentRepo,
            ClientRepo clientRepo
    ) {
        this.docRepo = docRepo;
        this.docTypeRepo = docTypeRepo;
        this.employeeRepo = employeeRepo;
        this.docActionService = docActionService;
        this.authenticationFacade = authenticationFacade;
        this.departmentRepo = departmentRepo;
        this.clientRepo = clientRepo;
    }


    public Employee getCurrentUser(){
        Authentication authentication = authenticationFacade.getAuthentication();
        return employeeRepo.findByLogin(authentication.getName());
    }

    
    public List<Doc> getAll(){
        return docRepo.findAll();
    }     
        
    public List<Doc> getFilteredSpecification(TestModel testModel){
        Employee emp = getCurrentUser();
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

        /////////////    doc actions    ///////////////
        docActionService.addActionInfo(1, doc.getId(), getCurrentUser());
        /////////////    doc actions    ///////////////
    }

    @Transactional
    public void delete(Integer id){

        /////////////    doc actions    ///////////////
        docActionService.addActionInfo(3, id, getCurrentUser());
        /////////////    doc actions    ///////////////

        docRepo.deleteById(id);
    }

    public Doc getById(Integer id){
        return docRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
    }



    @Transactional
    public void update(Integer id, Doc changes){

        Doc doc = docRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        doc.setName(changes.getName());
        doc.setFolder(changes.getFolder());
        doc.setDocDate(changes.getDocDate());
        doc.setAccessLevel(changes.getAccessLevel());

        Integer docTypeId = changes.getDocType().getId();
        DocType docType = docTypeRepo.findById(docTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Document type", docTypeId));
        doc.setDocType(docType);

        Integer departmentId = changes.getDepartment().getId();
        Department department = departmentRepo.findById(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department", departmentId));
        doc.setDepartment(department);

        Integer fromEmployeeId = changes.getFromEmployee().getId();
        Employee fromEmployee = employeeRepo.findById(fromEmployeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Employee", fromEmployeeId));
        doc.setFromEmployee(fromEmployee);


        if (changes.getClient() == null || changes.getClient().getId() == null){
            doc.setClient(null);
        }else{
            Integer clientId = changes.getClient().getId();
            Client client = clientRepo.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client", clientId));
            doc.setClient(client);
        }

        if (changes.getDocEmployee() == null || changes.getDocEmployee().getId() == null) {
            doc.setDocEmployee(null);
        } else {
            Integer employeeId = changes.getDocEmployee().getId();
            Employee employee = employeeRepo.findById(employeeId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Employee", employeeId));
            doc.setDocEmployee(employee);
        }

        LocalDate deletionDate = changes.getDeletionDate();
        if (deletionDate == null && docType.getStoringTime() != null) {
            deletionDate = changes.getDocDate()
                    .plusYears(docType.getStoringTime());
        }
        doc.setDeletionDate(deletionDate);


        docActionService.addActionInfo(2, doc.getId(), getCurrentUser());
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

}
