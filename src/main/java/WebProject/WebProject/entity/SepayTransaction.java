package WebProject.WebProject.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data

@NoArgsConstructor
@Table(name = "sepay_transaction")
public class SepayTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mã giao dịch do SePay cung cấp
    @Column(name = "transaction_code", length = 255)
    private String transactionCode;

    // Mã giao dịch ngân hàng
    @Column(name = "bank_transaction_code", length = 255)
    private String bankTransactionCode;

    // Nội dung chuyển khoản
    @Column(name = "transfer_content", columnDefinition = "nvarchar(1000)")
    private String transferContent;

    // Số tiền giao dịch
    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    // Mã QR thanh toán
    @Column(name = "qr_code", columnDefinition = "nvarchar(2000)")
    private String qrCode;

    // Trạng thái giao dịch
    // PENDING, SUCCESS, FAILED, EXPIRED, RECONCILED
    @Column(name = "status", length = 50)
    private String status;

    // Thời hạn thanh toán
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // Thời điểm nhận giao dịch
    @Column(name = "transaction_at")
    private LocalDateTime transactionAt;

    // Thời điểm đối soát
    @Column(name = "reconciled_at")
    private LocalDateTime reconciledAt;

    // Đã đối soát hay chưa
    @Column(name = "reconciled")
    private boolean reconciled;

    // Ghi chú
    @Column(name = "note", columnDefinition = "nvarchar(1000)")
    private String note;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
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