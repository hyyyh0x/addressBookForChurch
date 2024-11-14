package addressBookForChurch.users.repository;

import addressBookForChurch.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByPhone(String phone);
    boolean existsByPhoneAndIdNot(String phone, Long id);
    Page<Users> findAll(Pageable pageable);
    Page<Users> findAllByName(String name, Pageable pageable);
}
