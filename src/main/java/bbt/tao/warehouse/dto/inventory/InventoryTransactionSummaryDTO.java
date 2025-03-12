package bbt.tao.warehouse.dto.inventory;

import lombok.*;
import java.time.LocalDateTime;
import bbt.tao.warehouse.model.enums.TransactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionSummaryDTO {
    private Long id;
    private TransactionType transactionType;
    private Long productId;
    private Double quantity;
    private LocalDateTime transactionDate;
}
