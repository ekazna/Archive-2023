package com.archive.archive.documentrequest;

import com.archive.archive.audit.DocActionService;
import com.archive.archive.audit.DocumentActionType;
import com.archive.archive.document.Doc;
import com.archive.archive.document.DocService;
import com.archive.archive.document.DocumentStatus;
import com.archive.archive.employee.Employee;
import com.archive.archive.exceptions.InvalidRequestStateException;
import com.archive.archive.security.CurrentUserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentRequestServiceTest {

    @Mock
    private DocumentRequestRepo documentRequestRepo;

    @Mock
    private DocService docService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private DocActionService docActionService;

    @Mock
    private DocumentRequestMapper documentRequestMapper;

    @InjectMocks
    private DocumentRequestService documentRequestService;

    @Test
    void createRequest_shouldCreateOriginalRequest(){

        Employee employee = new Employee();
        employee.setId(10);

        Doc document = new Doc();
        document.setId(100);
        document.setStatus(DocumentStatus.ACTIVE);
        document.setPresent(true);

        when(currentUserService.getCurrentEmployee())
                .thenReturn(employee);

        when(docService.getAccessibleActiveDocument(100))
                .thenReturn(document);

        when(documentRequestRepo
                .existsByDoc_IdAndEmployee_IdAndRequestTypeAndRequestStatusIn(
                        eq(100),
                        eq(10),
                        eq(RequestType.ORIGINAL),
                        anyCollection()
                ))
                .thenReturn(false);

        when(documentRequestRepo.save(any(DocumentRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        documentRequestService.createRequest(
                RequestType.ORIGINAL,
                100
        );

        verify(documentRequestRepo).save(
                argThat(request ->
                        request.getDoc() == document
                                && request.getEmployee() == employee
                                && request.getRequestType() == RequestType.ORIGINAL
                                && request.getRequestStatus() == RequestStatus.REQUESTED
                                && LocalDate.now().equals(request.getRequestDate())
                )
        );

        verify(docActionService).recordAction(
                DocumentActionType.REQUESTED_ORIGINAL,
                100,
                employee
        );
    }

    @Test
    void createRequest_shouldRejectDuplicateActiveRequest(){
        Employee employee = new Employee();
        employee.setId(10);

        Doc document = new Doc();
        document.setId(100);

        when(currentUserService.getCurrentEmployee())
                .thenReturn(employee);

        when(docService.getAccessibleActiveDocument(100))
                .thenReturn(document);

        when(documentRequestRepo
                .existsByDoc_IdAndEmployee_IdAndRequestTypeAndRequestStatusIn(
                        eq(100),
                        eq(10),
                        eq(RequestType.ORIGINAL),
                        anyCollection()
                ))
                .thenReturn(true);


        assertThrows(
                InvalidRequestStateException.class,
                () -> documentRequestService.createRequest(RequestType.ORIGINAL, 100)
        );

        verify(documentRequestRepo, never()).save(any(DocumentRequest.class));

        verifyNoInteractions(docActionService);
    }

    @Test
    void createRequest_shouldCreateCopyRequest(){
        Employee employee = new Employee();
        employee.setId(10);

        Doc document = new Doc();
        document.setId(100);
        document.setStatus(DocumentStatus.ACTIVE);
        document.setPresent(true);

        when(currentUserService.getCurrentEmployee())
                .thenReturn(employee);

        when(docService.getAccessibleActiveDocument(100))
                .thenReturn(document);

        when(documentRequestRepo
                .existsByDoc_IdAndEmployee_IdAndRequestTypeAndRequestStatusIn(
                        eq(100),
                        eq(10),
                        eq(RequestType.COPY),
                        anyCollection()
                ))
                .thenReturn(false);

        when(documentRequestRepo.save(any(DocumentRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        documentRequestService.createRequest(RequestType.COPY, 100);

        verify(documentRequestRepo).save(
                argThat(request ->
                        request.getDoc() == document
                                && request.getEmployee() == employee
                        && request.getRequestType() == RequestType.COPY
                        && request.getRequestStatus() == RequestStatus.REQUESTED
                )
        );

        verify(docActionService).recordAction(
                DocumentActionType.REQUESTED_COPY,
                100,
                employee
        );

    }


    @Test
    public void completeCopyRequest_shouldCompleteRequestedCopy(){

        Employee admin = new Employee();
        admin.setId(10);

        Doc document = new Doc();
        document.setId(100);
        document.setStatus(DocumentStatus.ACTIVE);

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.COPY);
        request.setRequestStatus(RequestStatus.REQUESTED);
        request.setDoc(document);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        when(currentUserService.getCurrentEmployee())
                .thenReturn(admin);

        //act
        documentRequestService.completeCopyRequest(50);

        //assert
        assertEquals(RequestStatus.COMPLETED, request.getRequestStatus());

        verify(docActionService).recordAction(
                DocumentActionType.SATISFIED_REQUEST,
                100,
                admin
        );
    }


    @Test
    public void completeCopyRequest_shouldRejectOriginalRequest(){

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.REQUESTED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        //assert + act
        assertThrows(InvalidRequestStateException.class,
                () -> documentRequestService.completeCopyRequest(50));

        assertEquals(RequestStatus.REQUESTED, request.getRequestStatus());

        verifyNoInteractions(docActionService);


    }

    @Test
    public void completeCopyRequest_shouldRejectAlreadyCompletedCopy(){

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.COPY);
        request.setRequestStatus(RequestStatus.COMPLETED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        //assert + act
        assertThrows(InvalidRequestStateException.class,
                () -> documentRequestService.completeCopyRequest(50));

        assertEquals(RequestStatus.COMPLETED, request.getRequestStatus());

        verifyNoInteractions(docActionService);


    }


    @Test
    public void issueOriginal_shouldIssueRequestedOriginal(){
        Employee admin = new Employee();
        admin.setId(1);

        Employee user = new Employee();
        user.setId(2);

        Doc document = new Doc();
        document.setId(100);
        document.setPresent(true);
        document.setStatus(DocumentStatus.ACTIVE);

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.REQUESTED);
        request.setEmployee(user);
        request.setDoc(document);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        when(currentUserService.getCurrentEmployee())
                .thenReturn(admin);

        //act
        documentRequestService.issueOriginal(50);

        //assert
        assertFalse(document.getPresent());

        assertEquals(RequestStatus.ISSUED, request.getRequestStatus());

        verify(docActionService).recordAction(
                DocumentActionType.ISSUED_ORIGINAL,
                100,
                admin);

        verify(docActionService).recordAction(
                DocumentActionType.TOOK_ORIGINAL,
                100,
                user);

    }


    @Test
    public void issueOriginal_shouldRejectCopyRequest(){

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.COPY);
        request.setRequestStatus(RequestStatus.REQUESTED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        //act+assert

        assertThrows(InvalidRequestStateException.class,
                () -> documentRequestService.issueOriginal(50));

        assertEquals(RequestStatus.REQUESTED, request.getRequestStatus());

        verifyNoInteractions(docActionService);

    }


    @Test
    void issueOriginal_shouldRejectWhenDocumentIsNotPresent() {

        Employee user = new Employee();
        user.setId(2);

        Doc document = new Doc();
        document.setId(100);
        document.setStatus(DocumentStatus.ACTIVE);
        document.setPresent(false);

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.REQUESTED);
        request.setEmployee(user);
        request.setDoc(document);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));


        InvalidRequestStateException exception =
                assertThrows(
                    InvalidRequestStateException.class,
                    () -> documentRequestService.issueOriginal(50)
        );

        assertEquals("Документ уже выдан", exception.getMessage());


        assertEquals(
                RequestStatus.REQUESTED,
                request.getRequestStatus()
        );

        assertFalse(document.getPresent());

        verifyNoInteractions(docActionService);
    }

    @Test
    void issueOriginal_shouldRejectAlreadyIssuedRequest() {

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.ISSUED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));


        InvalidRequestStateException exception = assertThrows(
                InvalidRequestStateException.class,
                () -> documentRequestService.issueOriginal(50)
        );

        assertEquals(
                "Можно выдать только запрошенные оригиналы",
                exception.getMessage()
        );

        assertEquals(
                RequestStatus.ISSUED,
                request.getRequestStatus()
        );

        verifyNoInteractions(docActionService);
    }

    @Test
    void returnOriginal_shouldReturnOriginalDocument(){

        Employee admin = new Employee();
        admin.setId(1);

        Employee user = new Employee();
        user.setId(2);

        Doc doc = new Doc();
        doc.setId(100);
        doc.setPresent(false);

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.ISSUED);
        request.setDoc(doc);
        request.setEmployee(user);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        when(currentUserService.getCurrentEmployee())
                .thenReturn(admin);

        // act
        documentRequestService.returnOriginal(50);

        //assert

        assertTrue(doc.getPresent());

        assertEquals(RequestStatus.COMPLETED, request.getRequestStatus());

        verify(docActionService).recordAction(
                DocumentActionType.ACCEPTED_ORIGINAL,
                100,
                admin
        );

        verify(docActionService).recordAction(
                DocumentActionType.RETURNED_ORIGINAL,
                100,
                user
        );
    }


    @Test
    void returnOriginal_shouldRejectCopyReturns(){

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.COPY);
        request.setRequestStatus(RequestStatus.ISSUED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        InvalidRequestStateException exception = assertThrows(
                InvalidRequestStateException.class,
                () -> documentRequestService.returnOriginal(50)
        );

        assertEquals("Вернуть можно только оригинал", exception.getMessage());

        assertEquals(RequestStatus.ISSUED, request.getRequestStatus());

        verifyNoInteractions(docActionService);

    }

    @Test
    void returnOriginal_shouldRejectNonIssuedOriginals(){

        DocumentRequest request = new DocumentRequest();
        request.setId(50);
        request.setRequestType(RequestType.ORIGINAL);
        request.setRequestStatus(RequestStatus.REQUESTED);

        when(documentRequestRepo.findById(50))
                .thenReturn(Optional.of(request));

        InvalidRequestStateException exception = assertThrows(
                InvalidRequestStateException.class,
                () -> documentRequestService.returnOriginal(50)
        );

        assertEquals("Оригинал должен быть выдан перед возвращением его в архив", exception.getMessage());

        assertEquals(RequestStatus.REQUESTED, request.getRequestStatus());

        verifyNoInteractions(docActionService);

    }

}