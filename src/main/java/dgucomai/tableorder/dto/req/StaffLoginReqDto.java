package dgucomai.tableorder.dto.req;

import jakarta.validation.constraints.NotBlank;

public record StaffLoginReqDto(@NotBlank String name, @NotBlank String password) {}
