/*
 * Copyright 2021 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.graphql.dgs.mvc

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.netflix.graphql.dgs.internal.DgsSchemaProvider
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.introspection.IntrospectionQuery
import graphql.schema.GraphQLSchema
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Provides an HTTP endpoint to retrieve the available schema.
 */
@RestController
class DgsRestSchemaJsonController(private val schemaProvider: DgsSchemaProvider) {

    // The @ConfigurationProperties bean name is <prefix>-<fqn>
    @RequestMapping("#{@'dgs.graphql-com.netflix.graphql.dgs.webmvc.autoconfigure.DgsWebMvcConfigurationProperties'.schemaJson.path}", produces = ["application/json"])
    fun schema(): String {
        val mapper = jacksonObjectMapper()

        val graphQLSchema: GraphQLSchema = schemaProvider.schema()
        val graphQL = GraphQL.newGraphQL(graphQLSchema).build()

        val executionInput: ExecutionInput = ExecutionInput.newExecutionInput().query(IntrospectionQuery.INTROSPECTION_QUERY)
            .build()
        val execute: ExecutionResult = graphQL.execute(executionInput)

        return mapper.writeValueAsString(execute.toSpecification())
    }
}