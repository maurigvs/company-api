package br.com.maurigvs.company;

import java.util.Optional;
import jakarta.enterprise.context.ApplicationScoped;

import br.com.maurigvs.company.model.Employee;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {

    public boolean existsByTaxId(String taxId) {
        return find("taxId", taxId)
                .stream().findFirst().isPresent();
    }

    public Optional<Employee> findByEmailAddress(String emailAddress) {
        return find("emailAddress", emailAddress)
                .stream().findFirst();
    }
}