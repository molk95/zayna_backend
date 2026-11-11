package com.zayna.user.dto;

import com.zayna.user.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "Status is required")
        AccountStatus status
) {
}