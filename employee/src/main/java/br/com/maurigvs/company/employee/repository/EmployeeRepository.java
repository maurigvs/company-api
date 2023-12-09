package br.com.maurigvs.company.employee.repository;

import br.com.maurigvs.company.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByTaxId(String taxId);
}