package com.coelho.manager.model.netbox.api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class Tenant {
    String id;
    String url;
    String display;
    String name;
    String slug;
}