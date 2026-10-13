package WebProject.WebProject.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory_transaction")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // IMPORT: nhập kho
    // EXPORT: xuất kho
    @Column(name = "transaction_type", length = 20)
    private String transactionType;

    // Số lượng nhập / xuất
    @Column(name = "quantity")
    private int quantity;

    // Giá nhập tại thời điểm giao dịch
    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    // Tổng giá trị giao dịch
    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    // Số lượng trước giao dịch
    @Column(name = "quantity_before")
    private int quantityBefore;

    // Số lượng sau giao dịch
    @Column(name = "quantity_after")
    private int quantityAfter;

    // Thời gian nhập / xuất
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // Người thực hiện
    @Column(name = "created_by", length = 255)
    private String createdBy;

    // Ghi chú
    @Column(name = "note", columnDefinition = "nvarchar(1000)")
    private String note;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant productVariant;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}