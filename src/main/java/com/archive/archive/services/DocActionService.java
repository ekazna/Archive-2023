package com.archive.archive.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.models.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.repositories.ActionStatusRepo;
import com.archive.archive.repositories.DocActionRepo;
import com.archive.archive.repositories.DocRepo;
import com.archive.archive.repositories.EmployeeRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DocActionService {
    private final DocActionRepo docActionRepo;
    private final ActionStatusRepo actionStatusRepo;
    private final EmployeeRepo employeeRepo;
    private final DocRepo docRepo;

    public DocActionService(DocActionRepo docActionRepo,
                            ActionStatusRepo actionStatusRepo,
                            EmployeeRepo employeeRepo,
                            DocRepo docRepo){
        this.docActionRepo = docActionRepo;
        this.actionStatusRepo = actionStatusRepo;
        this.employeeRepo = employeeRepo;
        this.docRepo = docRepo;
    }


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


    @Transactional
    public void recordAction(DocumentActionType actionType, Integer docId, Employee employee){
        DocAction docAction = new DocAction();
        docAction.setActionDate(LocalDateTime.now());

        ActionStatus actionStatus = actionStatusRepo.findById(actionType.getStatusId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Action status", actionType.getStatusId()));

        docAction.setActionStatus(actionStatus);

        docAction.setEmployee(employee);

        Doc doc = docRepo.findById(docId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", docId));

        docAction.setDoc(doc);
        docAction.setDocName(doc.getName());

        docActionRepo.save(docAction);
        
    }
}
