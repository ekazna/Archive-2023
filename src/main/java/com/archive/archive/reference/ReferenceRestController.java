package com.archive.archive.reference;

import com.archive.archive.reference.dto.ReferenceOptionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reference")
public class ReferenceRestController {

    private final DepartmentService departmentService;
    private final ClientService clientService;
    private final DocTypeService docTypeService;

    public ReferenceRestController(
            DepartmentService departmentService,
            ClientService clientService,
            DocTypeService docTypeService
    ) {
        this.departmentService = departmentService;
        this.clientService = clientService;
        this.docTypeService = docTypeService;
    }

    @GetMapping("/departments")
    public List<ReferenceOptionResponse> getDepartments() {
        return departmentService.getAll().stream()
                .map(d -> new ReferenceOptionResponse(
                        d.getId(),
                        d.getName()
                ))
                .toList();
    }

    @GetMapping("/clients")
    public List<ReferenceOptionResponse> getClients() {
        return clientService.getAll().stream()
                .map(c -> new ReferenceOptionResponse(
                        c.getId(),
                        c.getName()
                ))
                .toList();
    }

    @GetMapping("/document-types")
    public List<ReferenceOptionResponse> getDocumentTypes() {
        return docTypeService.getAll().stream()
                .map(t -> new ReferenceOptionResponse(
                        t.getId(),
                        t.getName()
                ))
                .toList();
    }
}