package com.wirequery.manager.domain.template

import java.time.LocalDateTime
import java.time.ZoneId

object TemplateFixtures {
    private val LOCAL_DATE_TIME_FIXTURE = LocalDateTime.now()
    private val OFFSET_DATE_TIME_FIXTURE =
        LOCAL_DATE_TIME_FIXTURE
            .atZone(ZoneId.systemDefault())
            .toOffsetDateTime()

    val TEMPLATE_FIXTURE_WITH_ID_1 =
        Template(
            id = 1,
            name = "Some name",
            description = "Some description",
            fields =
                listOf(
                    Template.Field(key = "Some key", label = "Some label", type = Template.FieldType.TEXT),
                ),
            nameTemplate = "Some nameTemplate",
            descriptionTemplate = "Some descriptionTemplate",
            allowUserInitiation = true,
            createdAt = OFFSET_DATE_TIME_FIXTURE,
        )

    val TEMPLATE_ENTITY_FIXTURE_1 =
        TemplateEntity(
            id = null,
            name = "Some name",
            description = "Some description",
            fields =
                listOf(
                    TemplateEntity.FieldEntity(
                        key = "Some key",
                        label = "Some label",
                        type = Template.FieldType.TEXT,
                    ),
                ),
            nameTemplate = "Some nameTemplate",
            descriptionTemplate = "Some descriptionTemplate",
            allowUserInitiation = true,
        )

    val CREATE_TEMPLATE_FIXTURE_1 =
        TemplateService.CreateTemplateInput(
            name = "Some name",
            description = "Some description",
            fields = listOf(Template.Field(key = "Some key", label = "Some label", type = Template.FieldType.TEXT)),
            nameTemplate = "Some nameTemplate",
            descriptionTemplate = "Some descriptionTemplate",
            allowUserInitiation = true,
        )

    val UPDATE_TEMPLATE_FIXTURE_1 =
        TemplateService.UpdateTemplateInput(
            name = "Some name",
            description = "Some description",
            fields =
                listOf(
                    Template.Field(key = "Some key", label = "Some label", type = Template.FieldType.TEXT),
                ),
            nameTemplate = "Some nameTemplate",
            descriptionTemplate = "Some descriptionTemplate",
            allowUserInitiation = true,
        )

    val TEMPLATE_ENTITY_FIXTURE_WITH_ID_1 =
        TEMPLATE_ENTITY_FIXTURE_1.copy(
            id = 1,
            createdAt = LOCAL_DATE_TIME_FIXTURE,
        )
}
