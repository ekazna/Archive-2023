package com.archive.archive.audit;

public enum DocumentActionType {

    ADDED(1),
    EDITED(2),
    DELETED(3),
    SATISFIED_REQUEST(4),
    ISSUED_ORIGINAL(5),
    ACCEPTED_ORIGINAL(6),
    REQUESTED_COPY(7),
    REQUESTED_ORIGINAL(8),
    TOOK_ORIGINAL(9),
    RETURNED_ORIGINAL(10);

    private final int statusId;

    DocumentActionType(int statusId){
        this.statusId = statusId;
    }

    public int getStatusId() {
        return statusId;
    }
}
