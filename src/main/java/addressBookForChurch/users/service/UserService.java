package addressBookForChurch.users.service;

import addressBookForChurch.users.dto.UserDTO;
import addressBookForChurch.users.entity.Users;
import addressBookForChurch.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsersRepository userRepository;

    public Page<UserDTO> getAllUsers(Pageable pageable, String search) {
        Page<Users> users;
        if (StringUtils.hasText(search)) {
            users = userRepository.findAllByNameContaining(search, pageable);
        } else {
            users = userRepository.findAll(
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by("name").ascending()));
        }
        return users.map(
            user -> new UserDTO(user.getId(), user.getName(), user.getPhone(), user.getPrayerNote(), user.getPicture())
        );
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
