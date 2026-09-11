package com.archive.archive.services;
import java.time.LocalDate;
import java.util.List;

import com.archive.archive.exceptions.ResourceNotFoundException;
import com.archive.archive.models.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.archive.archive.config.IAuthenticationFacade;
import com.archive.archive.repositories.DocRepo;
import com.archive.archive.repositories.EmployeeRepo;
import com.archive.archive.repositories.OrderRepo;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepo orderRepo;
    private final EmployeeRepo employeeRepo;
    private final DocService docService;
    private final DocActionService docActionService;
    private final IAuthenticationFacade authenticationFacade;

    public OrderService(OrderRepo orderRepo,
                        EmployeeRepo employeeRepo,
                        DocService docService,
                        DocActionService docActionService,
                        IAuthenticationFacade authenticationFacade) {
        this.orderRepo = orderRepo;
        this.employeeRepo = employeeRepo;
        this.docService= docService;
        this.docActionService = docActionService;
        this.authenticationFacade = authenticationFacade;
    }


    public Employee getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return employeeRepo.findByLogin(authentication.getName());
    }


    @Transactional
    public void createRequest(RequestType requestType, Integer docId) {
        Employee employee = getCurrentUser();

        Doc doc = docService.getById(docId);

        Order request = new Order();

        request.setDoc(doc);
        request.setEmployee(employee);
        request.setOrderDate(LocalDate.now());

        request.setRequestType(requestType);
        request.setRequestStatus(RequestStatus.REQUESTED);

        orderRepo.save(request);

        if (requestType == RequestType.COPY) {
            docActionService.recordAction(
                    DocumentActionType.REQUESTED_COPY, docId, employee
            );
        } else {
            docActionService.recordAction(
                    DocumentActionType.REQUESTED_ORIGINAL,
                    docId, employee);
        }
    }

    @Transactional
    public void issueOriginal(Integer id) {

        Order request = orderRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Request", id));

        Doc doc = request.getDoc();

        doc.setPresent(false);

        request.setRequestStatus(RequestStatus.ISSUED);

        docActionService.recordAction(
                DocumentActionType.ISSUED_ORIGINAL,
                doc.getId(),
                getCurrentUser()
        );

        docActionService.recordAction(
                DocumentActionType.TOOK_ORIGINAL,
                doc.getId(),
                request.getEmployee()
        );
    }

    @Transactional
    public void completeCopyRequest(Integer id) {

        Order request = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request order", id));

        request.setRequestStatus(RequestStatus.COMPLETED);

        docActionService.recordAction(
                DocumentActionType.SATISFIED_REQUEST, request.getDoc().getId(), getCurrentUser()
        );
    }

    @Transactional
    public void returnOriginal(Integer id) {

        Order request = orderRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Request", id));

        Doc doc = request.getDoc();

        doc.setPresent(true);

        request.setRequestStatus(RequestStatus.COMPLETED);

        docActionService.recordAction(
                DocumentActionType.ACCEPTED_ORIGINAL,
                doc.getId(),
                getCurrentUser()
        );

        docActionService.recordAction(
                DocumentActionType.RETURNED_ORIGINAL,
                doc.getId(),
                request.getEmployee()
        );
    }




    public List<Order> findByTypeAndStatusAndSort(
            RequestType requestType,
            RequestStatus requestStatus,
            OrderSorting orderSorting
    ) {
        String sortType = orderSorting.getSortType();

        if (sortType == null) {
            return orderRepo.findByRequestTypeAndRequestStatus(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("employeeEmail")) {
            return orderRepo.findByRequestTypeAndRequestStatusOrderByEmployee_Email(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("docName")) {
            return orderRepo.findByRequestTypeAndRequestStatusOrderByDoc_Name(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("docFolder")) {
            return orderRepo.findByRequestTypeAndRequestStatusOrderByDoc_Folder(
                    requestType,
                    requestStatus
            );
        }

        if (sortType.equals("orderDate")) {
            return orderRepo.findByRequestTypeAndRequestStatusOrderByOrderDate(
                    requestType,
                    requestStatus
            );
        }

        return orderRepo.findByRequestTypeAndRequestStatus(
                requestType,
                requestStatus
        );
    }
    }