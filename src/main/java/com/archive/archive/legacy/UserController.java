package com.archive.archive.legacy;

import java.util.List;


import com.archive.archive.document.Doc;
import com.archive.archive.documentrequest.RequestType;
import com.archive.archive.employee.Employee;
import com.archive.archive.legacy.filter.TestModel;
import com.archive.archive.reference.*;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


import com.archive.archive.document.DocService;
import com.archive.archive.employee.EmployeeService;
import com.archive.archive.documentrequest.DocumentRequestService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final DepartmentService departmentService;
    private final ClientService clientService;
    private final DocService docService;
    private final DocTypeService docTypeService;
    private final EmployeeService employeeService;
    private final DocumentRequestService documentRequestService;

    public UserController(
            DepartmentService departmentService,
            ClientService clientService,
            DocService docService,
            DocTypeService docTypeService,
            EmployeeService employeeService,
            DocumentRequestService documentRequestService
    ) {
        this.departmentService = departmentService;
        this.clientService = clientService;
        this.docService = docService;
        this.docTypeService = docTypeService;
        this.employeeService = employeeService;
        this.documentRequestService = documentRequestService;
    }

 

    @GetMapping("/")
    public ModelAndView viewAllDocs(Model model, @Param("testModel") TestModel testModel){
        List<Doc> listDocs = docService.getFilteredSpecification(testModel);
        
        List<Department> listDepts= departmentService.getAllSortedAsc();
        List<Client> listClients=clientService.getAllSortedAsc();
        List<DocType> listDocTypes=docTypeService.getAllSortedAsc();
        List<Employee> listEmployees=employeeService.getAllSortedAsc();
        model.addAttribute("listDocs", listDocs);
        model.addAttribute("listDepts", listDepts);
        model.addAttribute("listClients", listClients);
        model.addAttribute("listDocTypes", listDocTypes);
        model.addAttribute("listEmployees",listEmployees);
        return new ModelAndView("userDocs");
    }

    @PostMapping("/orderCopy/{id}")
    public ModelAndView orderCopy(@PathVariable Integer id){
        documentRequestService.createRequest(RequestType.COPY, id);
        return new ModelAndView("redirect:/user/");
    }

    
    @PostMapping("/orderOriginal/{id}")
    public ModelAndView orderOriginal(@PathVariable Integer id){
        documentRequestService.createRequest(RequestType.ORIGINAL, id);
        return new ModelAndView("redirect:/user/");
    }
    
    
    @GetMapping("/about")
    public ModelAndView about(){
        return new ModelAndView("aboutAuthorForUser");
    }

    

}
