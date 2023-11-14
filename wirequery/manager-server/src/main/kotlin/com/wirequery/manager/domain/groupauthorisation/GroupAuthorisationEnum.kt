// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.groupauthorisation

enum class GroupAuthorisationEnum(val label: String, val description: String) {
    QUERY("Query", "Allow querying"),
    STORE_QUERY("Store Query", "Allow storing the querying"),
    CREATE_OR_EDIT_TEMPLATE_QUERY("Create or Edit Template Query", "Allow creating or editing template queries"),

    VIEW_API_KEY("View API Key", "Allow viewing the API Key of all applications in the group"),
    VIEW_APPLICATION("View Application", "Allow viewing all applications in the group"),
    UPDATE_APPLICATION("Update Application", "Allow updating all applications in the group"),
    DELETE_APPLICATION("Delete Application", "Allow deleting all applications in the group"),
    UNQUARANTINE_APPLICATION("Unquarantine Application", "Allow unquarantine all applications in the group"),

    VIEW_GROUP("View Group", "Allow viewing the group"),
    UPDATE_GROUP("Update Group", "Allow updating the group"),
    DELETE_GROUP("Delete Group", "Allow deleting the group"),

    VIEW_STORED_QUERY("View Stored Query", "Allow viewing all related stored queries"),
    DELETE_STORED_QUERY("Delete Stored Query", "Allow deleting all related stored queries"),

    ADD_TO_EXISTING_GROUP(
        "Add To Existing Group",
        "Allow adding existing applications to the group (required on both sides)",
    ),
    ADD_NEW_TO_GROUP("Add To New Group", "Allow adding new applications to the group"),

    DELETE_GROUP_APPLICATION("Delete Application from Group", "Allow deleting all applications in the group"),
    DELETE_GROUP_USER("Delete User from Group", "Allow deleting all users in the group"),
}
