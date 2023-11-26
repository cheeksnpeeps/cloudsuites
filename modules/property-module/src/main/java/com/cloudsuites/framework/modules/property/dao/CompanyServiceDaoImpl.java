package com.cloudsuites.framework.modules.property.dao;

/*
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CompanyServiceDaoImpl implements CompanyServiceDao{


	@Override
	public CompletableFuture<String> createCompany(@NotNull PropertyManagementCompany company) {
        ApiFuture<DocumentReference> result = firestore
        		.collection(FirestoreCollections.COMPANIES)
        		.add(company);
        CompletableFuture<DocumentReference> refFuture = ApiFuturesExtra.toCompletableFuture(result);
        return refFuture.thenApply(DocumentReference::getId);
	}

	@Override
	public CompletableFuture<PropertyManagementCompany> updateCompany(@NotNull String cid, @NotNull PropertyManagementCompany company) {
		company.setUpdatedAt(System.currentTimeMillis());
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.COMPANIES)
        		.document(cid)
        		.set(company, SetOptions.merge());
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<PropertyManagementCompany> getPropertyManagementCompany(@NotNull String cid) {
		ApiFuture<DocumentSnapshot> result = firestore.collection(FirestoreCollections.COMPANIES).document(cid).get();
        CompletableFuture<DocumentSnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObject(PropertyManagementCompany.class));
	}

	@Override
	public CompletableFuture<List<PropertyManagementCompany>> getAllCompanies() {
		ApiFuture<QuerySnapshot> result = firestore.collection(FirestoreCollections.COMPANIES).get();
        CompletableFuture<QuerySnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObjects(PropertyManagementCompany.class));
	}
	
	@Override
	public CompletableFuture<Boolean> disableCompany(@NotNull String cid) {
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.COMPANIES)
        		.document(cid)
        		.update(FirestoreCollections.STATUS_FIELD, Status.DISABLED.name(),
        				FirestoreCollections.UPDATED_AT_FIELD, System.currentTimeMillis());
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}
	
	@Override
	public CompletableFuture<Boolean> deleteCompany(@NotNull String cid) {
		ApiFuture<WriteResult> result = firestore.collection(FirestoreCollections.COMPANIES).document(cid).delete();
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

}
*/