package bbt.tao.warehouse.dto.transaction;

import bbt.tao.warehouse.model.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionDTO {
    private Long id;
    private String reference;
    private TransactionType type;
    private LocalDateTime date;
    private String notes;
    private List<TransactionItemDTO> items = new ArrayList<>();
    private String createdBy;
}


