/*
 *  Copyright 2020 The Hyve
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.radarbase.authorizer.inject

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.OkHttpClient
import org.glassfish.jersey.internal.inject.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.radarbase.authorizer.Config
import org.radarbase.authorizer.RestSourceClients
import org.radarbase.authorizer.api.RestSourceClientMapper
import org.radarbase.authorizer.api.RestSourceUserMapper
import org.radarbase.authorizer.doa.RestSourceUserRepository
import org.radarbase.authorizer.doa.RestSourceUserRepositoryImpl
import org.radarbase.authorizer.service.RestSourceAuthorizationService
import org.radarbase.jersey.config.ConfigLoader
import org.radarbase.jersey.config.JerseyResourceEnhancer
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.ws.rs.ext.ContextResolver

class AuthorizerResourceEnhancer(private val config: Config) : JerseyResourceEnhancer {
    private val restSourceClients = RestSourceClients(config.restSourceClients)

    override val classes: Array<Class<*>>
        get() {
            return if (config.service.enableCors == true) {
                arrayOf(
                    ConfigLoader.Filters.logResponse,
                    ConfigLoader.Filters.cors)
            } else {
                arrayOf(
                    ConfigLoader.Filters.logResponse)
            }
        }

    override val packages: Array<String> = arrayOf(
        "org.radarbase.authorizer.exception",
        "org.radarbase.authorizer.resources")

    override fun AbstractBinder.enhance() {
        // Bind instances. These cannot use any injects themselves
        bind(config)
            .to(Config::class.java)

        bind(restSourceClients)
            .to(RestSourceClients::class.java)

        bind(RestSourceUserMapper::class.java)
            .to(RestSourceUserMapper::class.java)
            .`in`(Singleton::class.java)

        bind(RestSourceClientMapper::class.java)
            .to(RestSourceClientMapper::class.java)
            .`in`(Singleton::class.java)

        bind(RestSourceUserRepositoryImpl::class.java)
            .to(RestSourceUserRepository::class.java)
            .`in`(Singleton::class.java)

        bind(RestSourceAuthorizationService::class.java)
            .to(RestSourceAuthorizationService::class.java)
            .`in`(Singleton::class.java)

    }
}
