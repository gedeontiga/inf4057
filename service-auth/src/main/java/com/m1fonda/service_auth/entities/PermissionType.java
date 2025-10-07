package com.m1fonda.service_auth.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionType {
    ADMIN_CREATE_ALL,
    ADMIN_READ_ALL,
    ADMIN_UPDATE_ALL,
    ADMIN_DELETE_ALL,

    OWNER_CREATE_BANK,
    OWNER_READ_BANK,
    OWNER_UPDATE_BANK,
    OWNER_DELETE_BANK,
    OWNER_GET_ALL_AGENCIES,
    OWNER_UPDATE_AGENCY,
    OWNER_DELETE_AGENCY,
    OWNER_CREATE_AGENCY,

    MANAGER_CREATE_USER,
    MANAGER_READ_ALL_USERS,
    MANAGER_UPDATED_USER,
    MANAGER_DELETE_USER,
    MANAGER_GET_AGENCY,

    USER_GET_ALL_SERVICES;

    @Getter
    private String permission;
}
