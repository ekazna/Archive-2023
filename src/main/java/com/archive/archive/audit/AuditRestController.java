package com.archive.archive.audit;

import com.archive.archive.audit.dto.AuditEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditRestController {

    private final DocActionService docActionService;

    public AuditRestController(
            DocActionService docActionService
    ) {
        this.docActionService = docActionService;
    }

    @GetMapping
    public Page<AuditEntryResponse> getAudit(
            Pageable pageable
    ) {
        return docActionService.getAll(pageable);
    }
}