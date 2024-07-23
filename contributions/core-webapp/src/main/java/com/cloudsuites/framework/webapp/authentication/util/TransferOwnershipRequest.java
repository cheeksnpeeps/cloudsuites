package com.cloudsuites.framework.webapp.authentication.util;

public class TransferOwnershipRequest {
    private String newOwnerId; // The ID of the new owner to transfer ownership to

    public TransferOwnershipRequest() {
    }

    public TransferOwnershipRequest(String newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

    public String getNewOwnerId() {
        return newOwnerId;
    }

    public void setNewOwnerId(String newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

    @Override
    public String toString() {
        return "TransferOwnershipRequest{" +
                "newOwnerId='" + newOwnerId + '\'' +
                '}';
    }
}
