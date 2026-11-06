package com.lekasoft.mapper;

import com.lekasoft.dto.CustomerDTO;
import com.lekasoft.model.Customer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toCustomer (CustomerDTO customerDTO);

    CustomerDTO toCustomerDto (Customer customer);
}
