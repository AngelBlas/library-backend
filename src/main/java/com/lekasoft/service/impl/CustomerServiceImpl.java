package com.lekasoft.service.impl;

import com.lekasoft.model.Customer;
import com.lekasoft.repository.ICustomerRepository;
import com.lekasoft.repository.IGenericRepository;
import com.lekasoft.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl extends CRUDImpl<Customer, Long> implements ICustomerService {

    private final ICustomerRepository repository;

    @Override
    protected IGenericRepository<Customer, Long> getRepo() {
        return repository;
    }
}
