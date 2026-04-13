package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPermission {
    private Long userId;
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Builder.Default
    private List<String> permissions = new ArrayList<>();
    @Builder.Default
    private Set<Long> accessibleEntities = new HashSet<>();
    private String dataScope;
    private Long departmentId;
    @Builder.Default
    private List<Long> postIds = new ArrayList<>();
}
