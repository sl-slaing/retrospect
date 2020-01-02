package com.example.retrospect.core.repositories;

import com.example.retrospect.core.models.Tenant;
import com.example.retrospect.core.serialisable.SerialisableTenant;
import com.example.retrospect.core.serialisers.TenantSerialiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TenantRepository {
    private static final String STORAGE_CONTAINER = "RETROSPECT_TENANTS";
    private final TenantSerialiser serialiser;
    private final DataStorage<SerialisableTenant> storage;

    @Autowired
    public TenantRepository(DataStorageFactory storageFactory, TenantSerialiser serialiser) {
        this.storage = storageFactory.getStorage(SerialisableTenant.class);
        this.serialiser = serialiser;
    }

    public List<Tenant> getAllTenants() {
        return storage.getAll(TenantRepository.STORAGE_CONTAINER)
                .map(serialiser::deserialise)
                .collect(Collectors.toList());
    }

    public Tenant getTenant(String id) {
        var serialisable = storage.getOne(TenantRepository.STORAGE_CONTAINER, id);
        return serialisable != null
                ? serialiser.deserialise(serialisable)
                : null;
    }

    public void deleteTenant(String id) {
        storage.remove(TenantRepository.STORAGE_CONTAINER, id);
    }

    public void addOrUpdate(Tenant tenant) {
        storage.addOrUpdate(
                TenantRepository.STORAGE_CONTAINER,
                tenant.getId(),
                serialiser.serialise(tenant));
    }
}
