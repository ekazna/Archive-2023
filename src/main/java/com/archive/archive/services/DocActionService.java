package com.archive.archive.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.models.Doc;
import com.archive.archive.models.DocAction;
import com.archive.archive.models.Employee;
import com.archive.archive.repositories.ActionStatusRepo;
import com.archive.archive.repositories.DocActionRepo;
import com.archive.archive.repositories.DocRepo;
import com.archive.archive.repositories.EmployeeRepo;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class DocActionService {
    @Autowired
    DocActionRepo docActionRepo;

    @Autowired
    ActionStatusRepo actionStatusRepo;

    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    DocRepo docRepo;


    public List<DocAction> getAll(){
        return docActionRepo.findAll(Sort.by(Sort.Direction.DESC, "actionDate"));
    }

    //////////////////////////////////////////////////////////////////////////

    public HashMap<String, Long> actStatistics(){
        List<Object[]> objList = docActionRepo.statistics();
        HashMap<String, Long> statsList = new HashMap<>();
        for (Object[] obj : objList) {
            statsList.put((String) obj[0], (Long) obj[1]);
        }
        return statsList;
    }

    //////////////////////////////////////////////////////////////////////////


    public void addActionInfo(Integer statusId, Integer docId, Employee employee){
        DocAction docAction = new DocAction();
        docAction.setActionDate(LocalDateTime.now());
        
        docAction.setActionStatus(actionStatusRepo.findById(statusId).get());

        docAction.setEmployee(employee);

        Doc doc = docRepo.findById(docId).get();
        if (statusId == 3){
            docAction.setDocName(doc.getName());
        } else{
            docAction.setDoc(doc);
            docAction.setDocName(doc.getName());
        }

        docActionRepo.save(docAction);                
        
    }
}
