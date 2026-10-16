package WebProject.WebProject.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data

@NoArgsConstructor
@Table(name = "product")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_Name", columnDefinition = "nvarchar(1111)")
	private String productName;

	@Column(name = "description", columnDefinition = "nvarchar(11111)")
	private String description;

	private String gender;

	@Column(name = "sold")
	private int sold;

	@Column(name = "is_Active")
	private int is_Active;

	@Column(name = "is_Selling")
	private int is_Selling;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonBackReference
	private Category category;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ProductVariant> variants;

	public BigDecimal getMinPrice() {
		if (variants == null || variants.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return variants.stream()
				.map(ProductVariant::getPrice)
				.filter(price -> price != null)
				.min(Comparator.naturalOrder())
				.orElse(BigDecimal.ZERO);
	}

	public String getMainImage() {
		String defaultImage = "https://res.cloudinary.com/shelflife-online/image/upload/a_0,c_fill,f_auto,q_100,w_1280/v1575961299/assets/1019456_1019456-Blac_03.jpg";

		if (variants == null || variants.isEmpty()) {
			return defaultImage;
		}

		for (ProductVariant variant : variants) {
			if (variant.getProductImage() != null && !variant.getProductImage().isEmpty()) {
				ProductImage firstImage = variant.getProductImage().get(0);
				if (firstImage != null && firstImage.getUrl_Image() != null
						&& !firstImage.getUrl_Image().trim().isEmpty()) {
					return firstImage.getUrl_Image();
				}
			}
		}

		return defaultImage;
	}

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@javax.persistence.PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		createdAt = now;
		updatedAt = now;
	}

	@javax.persistence.PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}
