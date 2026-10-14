package WebProject.WebProject.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User {
	@Id()
	private String id;

	@Column(name = "login_Type", columnDefinition = "nvarchar(1111)")
	private String login_Type;

	@Column(name = "role", columnDefinition = "nvarchar(1111)")
	private String role;

	@Column(name = "password", columnDefinition = "nvarchar(1111)")
	private String password;

	@Column(name = "user_Name", columnDefinition = "nvarchar(1111)")
	private String username;

	@Column(name = "avatar", columnDefinition = "nvarchar(1111)")
	private String avatar;

	@Column(name = "email", columnDefinition = "nvarchar(1111)")
	private String email;

	@Column(name = "phone_Number", columnDefinition = "nvarchar(1111)")
	private String phone_Number;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Order> order;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private List<Cart> cart;
	@Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
