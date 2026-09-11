package com.archive.archive.audit;

import java.time.LocalDateTime;

import com.archive.archive.audit.dto.AuditEntryResponse;
import com.archive.archive.document.Doc;
import com.archive.archive.employee.Employee;
import com.archive.archive.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<AuditEntryResponse> getAll(Pageable pageable) {

        return docActionRepo.findAll(pageable)
                .map(action -> new AuditEntryResponse(
                        action.getId(),
                        action.getActionDate(),
                        action.getDoc() != null
                                ? action.getDoc().getId()
                                : null,
                        action.getDocName(),
                        action.getEmployee().getId(),
                        action.getEmployee().getLogin(),
                        action.getActionStatus().getName()
                ));
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
