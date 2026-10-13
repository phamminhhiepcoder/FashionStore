package WebProject.WebProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import WebProject.WebProject.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	User findById(String id);

	User findByIdAndRole(String id, String role);

	void deleteById(String id);

	Page<User> findByUsernameContaining(String username, Pageable pageable);
}
