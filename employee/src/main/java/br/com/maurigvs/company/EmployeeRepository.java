package br.com.maurigvs.company;

import java.util.Optional;

import br.com.maurigvs.company.model.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByTaxId(String taxId);

    Optional<Employee> findByEmailAddress(String emailAddress);
}