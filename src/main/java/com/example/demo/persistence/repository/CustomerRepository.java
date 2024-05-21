package com.example.demo.persistence.repository;

import com.example.demo.persistence.model.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, UUID> {

    @Query(
            value =
                    "select cu from Customer as cu left join fetch cu.accountBalances where cu.pesel = :pesel")
    Optional<Customer> findByPesel(String pesel);
}
