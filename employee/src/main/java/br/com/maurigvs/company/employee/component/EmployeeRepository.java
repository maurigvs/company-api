package br.com.maurigvs.company.employee.component;

import br.com.maurigvs.company.employee.component.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}