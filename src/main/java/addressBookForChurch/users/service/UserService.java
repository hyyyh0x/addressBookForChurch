package addressBookForChurch.users.service;

import addressBookForChurch.users.dto.UserDTO;
import addressBookForChurch.users.entity.Users;
import addressBookForChurch.users.repository.UsersRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;

    @Value("${admin-phone}")
    private String adminPhone;

    public List<UserDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        return users.stream().map(
            user -> new UserDTO(user.getId(), user.getName(), user.getPhone(), user.getPrayerNote(),
                user.getPicture())).toList();
    }

    public UserDTO getUserById(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("없는 아이디입니다."));
        return new UserDTO(user.getId(), user.getName(), user.getPhone(), user.getPrayerNote(),
            user.getPicture());
    }

    public UserDTO createUser(UserDTO userDTO) {
        if(userRepository.existsByPhone(userDTO.phone())){
            throw new IllegalArgumentException("있는 전화번호입니다.");
        }
        Users users = new Users(userDTO.name(), userDTO.phone(), userDTO.prayerNote(),
            userDTO.picture());
        users = userRepository.save(users);
        return new UserDTO(users.getId(), users.getName(), users.getPhone(), users.getPrayerNote(),
            users.getPicture());
    }

    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        if(userRepository.existsByPhoneAndIdNot(userDTO.phone(), userId)){
            throw new IllegalArgumentException("있는 전화번호입니다.");
        }
        Users users = userRepository.findById(userId).orElseThrow();
        users.update(userDTO.name(), userDTO.phone(), userDTO.prayerNote(), userDTO.picture());
        Users user = userRepository.save(users);
        return new UserDTO(user.getId(), user.getName(), user.getPhone(), user.getPrayerNote(),
            users.getPicture());
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
