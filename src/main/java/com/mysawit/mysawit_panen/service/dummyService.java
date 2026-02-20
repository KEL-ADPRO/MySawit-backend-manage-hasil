package com.mysawit.mysawit_panen.service;

import com.mysawit.mysawit_panen.model.dummyModel;
import com.mysawit.mysawit_panen.repository.dummyRepository;

import org.springframework.stereotype.Service;

@Service
public class dummyService {
    private final dummyRepository repository;

    public dummyService(dummyRepository repository) {
        this.repository = repository;
    }

    public dummyModel create() {
        dummyModel dummyModel = new dummyModel();
        return repository.save(dummyModel);
    }
}
