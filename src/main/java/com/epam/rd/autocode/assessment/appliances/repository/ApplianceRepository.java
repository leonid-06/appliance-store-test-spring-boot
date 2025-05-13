package com.epam.rd.autocode.assessment.appliances.repository;

import com.epam.rd.autocode.assessment.appliances.model.self.Appliance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplianceRepository extends JpaRepository<Appliance, Long>, JpaSpecificationExecutor<Appliance> {
    List<Appliance> findByDiscountGreaterThan(double discount);
    List<Appliance> findTop5ByOrderBySalesCountDesc(Pageable pageable);

    Optional<Appliance> findByName(String name);
}

//@Query("SELECT a FROM Appliance a WHERE a.discount > 0.5 OR a.salesCount IN (SELECT b.salesCount
// FROM Appliance b ORDER BY b.salesCount DESC LIMIT 5)")
//List<Appliance> findDiscountedOrTop5BySalesCount();


// List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
//                        equals
//SELECT * FROM employee WHERE first_name = ? AND last_name = ?



//List<Client> findByNameAndCity(String name, String city);  // AND
//List<Client> findByNameOrCity(String name, String city);   // OR
//List<Client> findByNameNot(String name);                   // NOT



//  To add sorting, you can use OrderBy
//List<Client> findByCityOrderByNameAsc(String city);
//List<Client> findByCityOrderByNameDesc(String city);
//                        equals
//SELECT * FROM client WHERE city = ? ORDER BY name ASC
//SELECT * FROM client WHERE city = ? ORDER BY name DESC


//List<Client> findByNameLike(String name);



//List<Client> findByAgeGreaterThan(int age);
//List<Client> findByAgeLessThan(int age);
//List<Client> findByAgeBetween(int start, int end);


//        for pagination and sorting use
//Page<Client> findByCity(String city, Pageable pageable);
//List<Client> findByCity(String city, Sort sort);
