// Copyright 2023 Wouter Nederhof
//
// Use of this source code is governed by the AGPLv3
// license that can be found in the `licenses` folder.
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.wirequery.manager.domain.authorisation

enum class AuthorisationEnum(val label: String, val description: String) {
    QUERY("Query", "Allow querying and show Queries tab."),

    VIEW_STORED_QUERIES("View Stored Queries", "Allow viewing all stored queries."),
    VIEW_APPLICATION_API_KEY("View all API Keys", "Allow viewing all API keys."),
    CREATE_STORED_QUERY("Create Stored Query", "Allow query storing."),
    DELETE_STORED_QUERY("Delete Stored Queries", "Delete any stored query."),

    VIEW_QUERY_LOGS("View Query Logs", "Allow viewing query logs."),

    VIEW_SESSIONS("View Sessions", "Allow viewing all sessions."),
    CREATE_SESSION("Create Session", "Allow session creation."),
    DELETE_SESSION("Delete Session", "Allow any session deletion."),

    VIEW_TEMPLATES("View Templates", "Allow viewing all templates."),
    CREATE_TEMPLATE("Create Template", "Allow template creation."),
    USER_AUTH_TEMPLATE("Set User Init on Template", "Allow template creation / update with end-user initiation and recording."),
    UPDATE_TEMPLATE("Update Template", "Allow template update."),
    DELETE_TEMPLATE("Delete Template", "Allow any template deletion."),

    VIEW_APPLICATIONS("View Applications", "Allow listing applications."),
    UPDATE_APPLICATION("Update all applications", "Allow updating all applications"),
    DELETE_APPLICATION("Delete Application", "Delete any application."),

    VIEW_GROUPS("View Groups", "Allow user to view groups."),
    CREATE_GROUP("Create Groups and Application", "Allow user to create applications and groups."),
    DELETE_GROUP("Delete Groups", "Delete any group."),

    MANAGE_USERS("Manage Users And Groups", "Allow managing users."),

    MANAGE_ROLES("Manage Roles And Group Roles", "Allow managing Roles And Group Roles"),

    VIEW_AUDIT_LOGS("View Audit Logs", "Allow viewing audit logs."),

    UNQUARANTINE_APPLICATIONS("Unquarantine Application", "Allow unquarantining application."),
    MANAGE_QUARANTINE_RULES("Manage Quarantine Rules", "Allow management of quarantine rules."),
}
