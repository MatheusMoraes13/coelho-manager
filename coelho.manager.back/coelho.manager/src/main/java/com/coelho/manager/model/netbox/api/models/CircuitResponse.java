package com.coelho.manager.model.netbox.api.models;

import lombok.*;

@ToString
@Getter @Setter
@AllArgsConstructor
public class CircuitResponse {
    @NonNull private String cid;
    private Tenant tenant;
    private String description;
    private String comments;
}
