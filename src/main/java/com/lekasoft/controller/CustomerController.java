package com.lekasoft.controller;

import com.lekasoft.dto.CustomerDTO;
import com.lekasoft.mapper.CustomerMapper;
import com.lekasoft.service.ICustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/customers")
public class CustomerController {

    private final ICustomerService service;
    private final CustomerMapper mapper;

    @PostMapping
    public ResponseEntity<CustomerDTO> save(@RequestBody CustomerDTO customerRequest){
        var customerDto = mapper.toCustomerDto(service.save(mapper.toCustomer(customerRequest)));
        return ResponseEntity.ok(customerDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(@PathVariable Long id, @RequestBody CustomerDTO customerRequest){
        var customer = service.update(id, mapper.toCustomer(customerRequest));
        return ResponseEntity.ok(mapper.toCustomerDto(customer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> findAll() {
        List<CustomerDTO> customerDTOS = service.findAll().stream()
                .map(mapper::toCustomerDto)
                .toList();
        return ResponseEntity.ok(customerDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> findById(@PathVariable Long id) {
        var customerDto = mapper.toCustomerDto(service.findById(id));
        return ResponseEntity.ok(customerDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
