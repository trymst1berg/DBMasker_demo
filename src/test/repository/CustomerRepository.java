// Generated with g9 DBmasker.

package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, Integer>
{}

