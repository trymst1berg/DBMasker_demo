// Generated with g9 DBmasker.

package repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

 import model.Address;

@Repository
public interface AddressRepository extends JpaRepository <Address, Integer>
{}

