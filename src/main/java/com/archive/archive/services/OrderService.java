package com.archive.archive.services;
import java.time.LocalDate;
import java.util.List;

import com.archive.archive.exceptions.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.archive.archive.config.IAuthenticationFacade;
import com.archive.archive.models.Doc;
import com.archive.archive.models.Employee;
import com.archive.archive.models.Order;
import com.archive.archive.models.OrderSorting;
import com.archive.archive.repositories.DocRepo;
import com.archive.archive.repositories.EmployeeRepo;
import com.archive.archive.repositories.OrderRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepo orderRepo;
    private final EmployeeRepo employeeRepo;
    private final DocRepo docRepo;
    private final DocActionService docActionService;
    private final  IAuthenticationFacade authenticationFacade;

    public OrderService(OrderRepo orderRepo,
                        EmployeeRepo employeeRepo,
                        DocRepo docRepo,
                        DocActionService docActionService,
                        IAuthenticationFacade authenticationFacade){
        this.orderRepo = orderRepo;
        this.employeeRepo = employeeRepo;
        this.docRepo = docRepo;
        this.docActionService = docActionService;
        this.authenticationFacade = authenticationFacade;
    }


    public Employee getCurrentUser(){
        Authentication authentication = authenticationFacade.getAuthentication();
        return employeeRepo.findByLogin(authentication.getName());
    }


    @Transactional
    public void orderDoc(Integer orderType, Integer docId){

        Employee employee = getCurrentUser();

        Order order = new Order();
        
        order.setDoc(docRepo.findById(docId).get());
        order.setEmployee(employee);

        order.setOrderDate(LocalDate.now());
        order.setType(orderType);
    
        orderRepo.save(order);

        /////////////    doc actions    ///////////////
        if (orderType == 1) {
            docActionService.addActionInfo(7, docId, employee);
        } else{
            docActionService.addActionInfo(8, docId, employee);
        }
        /////////////    doc actions    ///////////////
        
        
    }

    public List<Order> findByType(Integer typeId){
        return orderRepo.findByType(typeId);
    }

    public List<Order> findByTypeAndSort(Integer typeId, OrderSorting orderSorting){
        String sortType = orderSorting.getSortType();
        if (sortType!=null){
            if (sortType.equals("employeeEmail")){
                return orderRepo.findByTypeOrderByEmployee_Email(typeId);
            }
            if (sortType.equals("docName")){
                return orderRepo.findByTypeOrderByDoc_Name(typeId);
            }
            if (sortType.equals("docFolder")){
                return orderRepo.findByTypeOrderByDoc_Folder(typeId);
            }
            if (sortType.equals("orderDate")){
                return orderRepo.findByTypeOrderByOrderDate(typeId);
            } else {
                return orderRepo.findByType(typeId);
            }
        } else {
            return orderRepo.findByType(typeId);
        }
    }


    @Transactional
    public void deleteCopyOrder(Integer id){

        /////////////    doc actions    ///////////////
        Doc doc = orderRepo.findById(id).get().getDoc();
        docActionService.addActionInfo(4, doc.getId(), getCurrentUser());
        /////////////    doc actions    ///////////////
        
        

        orderRepo.deleteById(id);
    }



    @Transactional
    public void updateType(Integer id){
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        Doc doc = order.getDoc();


        doc.setPresent(false);
        order.setType(3);


        /////////////    doc actions  ADMIN  ///////////////
        docActionService.addActionInfo(5, doc.getId(), getCurrentUser());
        /////////////    doc actions  USER   ///////////////
        docActionService.addActionInfo(9, doc.getId(), order.getEmployee());
        /////////////    doc actions    ///////////////
        
        
    }


    @Transactional
    public void returnOrder(Integer id){
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        Doc doc = order.getDoc();
        doc.setPresent(true);

        /////////////    doc actions   ADMIN  ///////////////
        docActionService.addActionInfo(6, doc.getId(), getCurrentUser());
        /////////////    doc actions   USER   ///////////////
        docActionService.addActionInfo(10, doc.getId(), order.getEmployee());
        /////////////    doc actions    ///////////////
        
        orderRepo.deleteById(id);
    }
}
