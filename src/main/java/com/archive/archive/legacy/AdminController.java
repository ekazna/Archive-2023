package com.archive.archive.legacy;

import com.archive.archive.audit.DocAction;
import com.archive.archive.document.Doc;
import com.archive.archive.document.dto.DocumentUpdateRequest;
import com.archive.archive.employee.Employee;
import com.archive.archive.legacy.filter.TestModel;
import com.archive.archive.reference.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.archive.archive.audit.DocActionService;
import com.archive.archive.document.DocService;
import com.archive.archive.employee.EmployeeService;
import com.archive.archive.documentrequest.DocumentRequestService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final DepartmentService departmentService;
    private final ClientService clientService;
    private final DocService docService;
    private final DocTypeService docTypeService;
    private final EmployeeService employeeService;
    private final DocumentRequestService documentRequestService;
    private final DocActionService docActionService;
    
    
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
        return new ModelAndView("adminDocs");
    }

    @GetMapping("/add/")
    public ModelAndView addDoc(Model model, @Param("doc") Doc doc){

        List<Department> listDepts= departmentService.getAllSortedAsc();
        List<Client> listClients=clientService.getAllSortedAsc();
        List<DocType> listDocTypes=docTypeService.getAllSortedAsc();
        List<Employee> listEmployees=employeeService.getAllSortedAsc();
        model.addAttribute("listDepts", listDepts);
        model.addAttribute("listClients", listClients);
        model.addAttribute("listDocTypes", listDocTypes);
        model.addAttribute("listEmployees",listEmployees);

        return new ModelAndView("adminAddDoc");
    }

    @PostMapping("/add/save/")
    public ModelAndView saveDoc(Model model, @ModelAttribute Doc doc){
        docService.save(doc);
        return new ModelAndView("redirect:/admin/add/");
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteDoc(@PathVariable Integer id){
        docService.dispose(id);
        return new ModelAndView("redirect:/admin/");
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editDoc(Model model, @PathVariable Integer id, @Param("doc") Doc doc){
        doc = docService.getById(id);
        model.addAttribute(doc);

        List<Department> listDepts= departmentService.getAllSortedAsc();
        List<Client> listClients=clientService.getAllSortedAsc();
        List<DocType> listDocTypes=docTypeService.getAllSortedAsc();
        List<Employee> listEmployees=employeeService.getAllSortedAsc();
        model.addAttribute("listDepts", listDepts);
        model.addAttribute("listClients", listClients);
        model.addAttribute("listDocTypes", listDocTypes);
        model.addAttribute("listEmployees",listEmployees);


        return new ModelAndView("adminEditDoc");


    }

    @PutMapping("/edit/{id}/updating/")
    public ModelAndView updateDoc(
            @PathVariable Integer id,
            @Valid @ModelAttribute DocumentUpdateRequest request){
        docService.update(id, request);
        return new ModelAndView("redirect:/admin/");
    }

//
//
//    @GetMapping("/copies/")
//    public ModelAndView seeCopyOrders(Model model, @Param("orderSorting") OrderSorting orderSorting){
//        List<DocumentRequest> documentRequestList = documentRequestService.findByTypeAndStatusAndSort(
//                RequestType.COPY, RequestStatus.REQUESTED, orderSorting);
//        model.addAttribute("orderList", documentRequestList);
//
//        return new ModelAndView("adminCopyOrders");
//    }

//    @DeleteMapping("/copies/{id}")
//    public ModelAndView copyOrderComplete(@PathVariable Integer id){
//        documentRequestService.completeCopyRequest(id);
//        return new ModelAndView("redirect:/admin/copies/");
//    }


//    @GetMapping("/originals/")
//    public ModelAndView seeOriginalOrders(Model model, @Param("orderSorting") OrderSorting orderSorting){
//        List<DocumentRequest> documentRequestList = documentRequestService.findByTypeAndStatusAndSort(
//                RequestType.ORIGINAL, RequestStatus.REQUESTED, orderSorting );
//        model.addAttribute(documentRequestList);
//
//        return new ModelAndView("adminOriginalOrders");
//    }


    @PutMapping("/originals/{id}")
    public ModelAndView issueOriginal(@PathVariable Integer id){
        documentRequestService.issueOriginal(id);
        return new ModelAndView("redirect:/admin/originals/");
    }


//    @GetMapping("/returns/")
//    public ModelAndView returnOriginalOrders(Model model, @Param("orderSorting") OrderSorting orderSorting){
//        List<DocumentRequest> documentRequestList = documentRequestService.findByTypeAndStatusAndSort(
//                RequestType.ORIGINAL, RequestStatus.ISSUED, orderSorting);
//        model.addAttribute(documentRequestList);
//
//        return new ModelAndView("adminDocReturns");
//    }

    @DeleteMapping("/returns/{id}")
    public ModelAndView returnComplete(@PathVariable Integer id){
        documentRequestService.returnOriginal(id);
        return new ModelAndView("redirect:/admin/returns/");
    }

    @GetMapping("/statistics/")
    public ModelAndView seeStatistics(Model model){
        HashMap<String, Long> statsList = docActionService.actStatistics();
        model.addAttribute("statsList", statsList);
        HashMap<String, Long> deptStatsList = docService.deptStatistics();
        model.addAttribute("deptStatsList", deptStatsList);
        

        List<DocAction> actionList = docActionService.getAll();
        model.addAttribute("actionList", actionList);
        
        return new ModelAndView("adminStatistics");
    }


    @GetMapping("/expired/")
    public ModelAndView viewAllDocs(Model model){

        List<Doc> listDocs = docService.getDocsToDelete();
        model.addAttribute("listDocs", listDocs);
        return new ModelAndView("adminExpired");
    }

    @DeleteMapping("/expired/delete/{id}")
    public ModelAndView deleteExpiredDoc(@PathVariable Integer id){
        docService.dispose(id);
        return new ModelAndView("redirect:/admin/expired/");
    }

    @GetMapping("/about")
    public ModelAndView about(){
        return new ModelAndView("aboutAuthor");
    }

}
