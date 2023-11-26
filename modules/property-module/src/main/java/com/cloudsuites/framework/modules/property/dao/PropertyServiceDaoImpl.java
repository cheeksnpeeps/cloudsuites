package com.cloudsuites.framework.modules.property.dao;

/*
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.spotify.futures.ApiFuturesExtra;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Component
public class PropertyServiceDaoImpl implements PropertyServiceDao{

	@Autowired 
	private Firestore firestore;
	
	
	@Override
	public CompletableFuture<String> createProperty(@NotNull String cid, @NotNull Property property) {
		property.setCompanyId(cid);
        ApiFuture<DocumentReference> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.add(property);
        CompletableFuture<DocumentReference> refFuture = ApiFuturesExtra.toCompletableFuture(result);
        return refFuture.thenApply(DocumentReference::getId);
	}

	@Override
	public CompletableFuture<Property> updateProperty(@NotNull String pid, Property property) {
		property.setCompanyId(pid);
		property.setPid(pid);
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(property.getPid())
        		.set(property);
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<Property> getProperty(String pid) {
		ApiFuture<DocumentSnapshot> result = firestore
				.collection(FirestoreCollections.PROPERTIES)
				.document(pid)
				.get();
        CompletableFuture<DocumentSnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObject(Property.class));
	}

	@Override
	public CompletableFuture<Property> getPropertyDetails(String pid) {
		ApiFuture<DocumentSnapshot> result = firestore
				.collection(FirestoreCollections.PROPERTIES)
				.document(pid)
				.get();
		CompletableFuture<DocumentSnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
		return resultFuture.thenApply(res -> res.toObject(Property.class))
				.thenCombine(getAllUnits(pid), (prop, units) -> {
					prop.setUnits(units);
					return prop;
				});
	}
	
	@Override
	public CompletableFuture<List<Property>> getAllProperties(String companyId) {
		ApiFuture<QuerySnapshot> result = firestore.collection(FirestoreCollections.PROPERTIES).get();
        CompletableFuture<QuerySnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObjects(Property.class));
	}

	@Override
	public CompletableFuture<Boolean> deleteProperty(String pid) {
		ApiFuture<WriteResult> result = firestore.collection(FirestoreCollections.PROPERTIES).document(pid).delete();
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<Boolean> disableProperty(@NotNull String pid) {
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.update(FirestoreCollections.STATUS_FIELD, Status.DISABLED.name(),
        				FirestoreCollections.UPDATED_AT_FIELD, System.currentTimeMillis());
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}
	
	@Override
	public CompletableFuture<Unit> addUnit(@NotNull String pid, @NotNull Integer unid, @NotNull Unit unit) {
		unit.setPid(pid);
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(String.valueOf(unid))
        		.set(unit);
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<Unit> updateUnit(@NotNull String pid, @NotNull String unid, @NotNull Unit unit) {
		unit.setPid(pid);
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(unid)
        		.set(unit, SetOptions.merge());
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<Unit> getUnit(@NotNull String pid, @NotNull String unid) {
        ApiFuture<DocumentSnapshot> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(unid)
        		.get();
        CompletableFuture<DocumentSnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObject(Unit.class));
	}
	
	@Override
	public CompletableFuture<Unit> getUnitDetails(@NotNull String pid, @NotNull String unid) {
        ApiFuture<DocumentSnapshot> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(unid)
        		.get();
        CompletableFuture<DocumentSnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(res -> res.toObject(Unit.class))
        		.thenCombine(getPropertyDetails(pid), (unit, property) -> {
        			unit.setProperty(property);
        			return unit;
        		});
	}

	@Override
	public CompletableFuture<Boolean> deleteUnit(@NotNull String pid, @NotNull String unid) {
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(unid)
        		.delete();
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}

	@Override
	public CompletableFuture<Boolean> disableUnit(@NotNull String unid, @NotNull String pid) {
        ApiFuture<WriteResult> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.document(unid)
        		.update(FirestoreCollections.STATUS_FIELD, Status.DISABLED.name(),
        				FirestoreCollections.UPDATED_AT_FIELD, System.currentTimeMillis());
        CompletableFuture<WriteResult> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        return resultFuture.thenApply(WriteResult::getUpdateTime);
	}
	
	@Override
	public CompletableFuture<List<Unit>> getAllUnits(@NotNull String pid) {
        ApiFuture<QuerySnapshot> result = firestore
        		.collection(FirestoreCollections.PROPERTIES)
        		.document(pid)
        		.collection(FirestoreCollections.UNITS)
        		.get();
        CompletableFuture<QuerySnapshot> resultFuture = ApiFuturesExtra.toCompletableFuture(result);
        
        return resultFuture.thenApply(res -> res.toObjects(Unit.class));
	}

}
*/