package com.archive.archive.audit;

import java.time.LocalDateTime;
import java.util.List;

import com.archive.archive.document.Doc;
import com.archive.archive.employee.Employee;
import com.archive.archive.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.archive.archive.document.DocRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DocActionService {
    private final DocActionRepo docActionRepo;
    private final ActionStatusRepo actionStatusRepo;
    private final DocRepo docRepo;

    public DocActionService(DocActionRepo docActionRepo,
                            ActionStatusRepo actionStatusRepo,
                            DocRepo docRepo){
        this.docActionRepo = docActionRepo;
        this.actionStatusRepo = actionStatusRepo;
        this.docRepo = docRepo;
    }


    public List<DocAction> getAll(){
        return docActionRepo.findAll(Sort.by(Sort.Direction.DESC, "actionDate"));
    }



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
