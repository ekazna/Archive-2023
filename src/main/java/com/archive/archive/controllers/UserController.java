package com.archive.archive.controllers;

import java.util.List;


import com.archive.archive.models.*;
import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


import com.archive.archive.services.ClientService;
import com.archive.archive.services.DepartmentService;
import com.archive.archive.services.DocService;
import com.archive.archive.services.DocTypeService;
import com.archive.archive.services.EmployeeService;
import com.archive.archive.services.OrderService;

@RestController
@RequestMapping("/user")
public class UserController {
    private final DepartmentService departmentService;
    private final ClientService clientService;
    private final DocService docService;
    private final DocTypeService docTypeService;
    private final EmployeeService employeeService;
    private final OrderService orderService;

    public UserController(
            DepartmentService departmentService,
            ClientService clientService,
            DocService docService,
            DocTypeService docTypeService,
            EmployeeService employeeService,
            OrderService orderService
    ) {
        this.departmentService = departmentService;
        this.clientService = clientService;
        this.docService = docService;
        this.docTypeService = docTypeService;
        this.employeeService = employeeService;
        this.orderService = orderService;
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
        orderService.createRequest(RequestType.COPY, id);
        return new ModelAndView("redirect:/user/");
    }

    
    @PostMapping("/orderOriginal/{id}")
    public ModelAndView orderOriginal(@PathVariable Integer id){
        orderService.createRequest(RequestType.ORIGINAL, id);
        return new ModelAndView("redirect:/user/");
    }
    
    
    @GetMapping("/about")
    public ModelAndView about(){
        return new ModelAndView("aboutAuthorForUser");
    }

    

}
