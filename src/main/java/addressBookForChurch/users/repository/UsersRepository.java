package addressBookForChurch.users.repository;

import addressBookForChurch.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByPhoneAndName(String phone, String name);
    boolean existsByPhoneAndNameAndIdNot(String phone, String name, Long id);
    Page<Users> findAll(Pageable pageable);
    Page<Users> findAllByNameContaining(String name, Pageable pageable);
}
