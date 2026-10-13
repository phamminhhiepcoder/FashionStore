package WebProject.WebProject.service;

import java.util.List;

import WebProject.WebProject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
	List<User> getAllUser();

	User saveUser(User user);

	User updateUser(User user);

	void deleteUserById(String id);

	User GetUserByEmail(String email);

	User findByIdAndRole(String id, String role);

	List<User> findAll();

	Page<User> findAll(Pageable pageable);

	User findById(Long id);

	Page<User> findByUsernameContaining(String name, Pageable pageable);
}
