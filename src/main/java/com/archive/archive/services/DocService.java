package com.archive.archive.services;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.archive.archive.config.IAuthenticationFacade;
import com.archive.archive.models.Doc;
import com.archive.archive.models.DocType;
import com.archive.archive.models.Employee;
import com.archive.archive.models.TestModel;
import com.archive.archive.models.TestModelSpecification;
import com.archive.archive.repositories.DepartmentRepo;
import com.archive.archive.repositories.DocRepo;
import com.archive.archive.repositories.DocTypeRepo;
import com.archive.archive.repositories.EmployeeRepo;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class DocService {
    @Autowired
    DocRepo docRepo;

    @Autowired
    DepartmentRepo departmentRepo;

    @Autowired
    DocTypeRepo docTypeRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    /////////////    doc actions    ///////////////
    @Autowired 
    DocActionService docActionService; 
    /////////////    doc actions    ///////////////

    @Autowired
    private IAuthenticationFacade authenticationFacade;


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


    public void save(Doc doc){
        doc.setDateAdded(LocalDate.now());
        if (doc.getClient().getId() == null){
            doc.setClient(null);
        }
        if (doc.getDocEmployee().getId() == null){
            doc.setDocEmployee(null);
        }
        doc.setPresent(true);

        DocType docType = docTypeRepo.findById(doc.getDocType().getId()).get();
        Integer storingTime = docType.getStoringTime();

        if (storingTime != null){
            doc.setDeletionDate(doc.getDocDate().plusYears(storingTime));        }

        docRepo.save(doc);

        /////////////    doc actions    ///////////////
        docActionService.addActionInfo(1, doc.getId(), getCurrentUser());
        /////////////    doc actions    ///////////////
    }

    public void delete(Integer id){

        /////////////    doc actions    ///////////////
        docActionService.addActionInfo(3, id, getCurrentUser());
        /////////////    doc actions    ///////////////

        docRepo.deleteById(id);
    }

    public Doc getById(Integer id){
        return docRepo.findById(id).get();
    }



    public void update(Doc doc){
        
        if (doc.getClient().getId() == null){
            doc.setClient(null);
        }
        if (doc.getDocEmployee().getId() == null){
            doc.setDocEmployee(null);
        }

        if (doc.getDeletionDate() == null){
            DocType docType = docTypeRepo.findById(doc.getDocType().getId()).get();
            Integer storingTime = docType.getStoringTime();

            if (storingTime != null){
                doc.setDeletionDate(doc.getDocDate().plusYears(storingTime));}
            }

        docRepo.save(doc);  
        /////////////    doc actions    ///////////////
        docActionService.addActionInfo(2, doc.getId(), getCurrentUser());
        /////////////    doc actions    ///////////////
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
