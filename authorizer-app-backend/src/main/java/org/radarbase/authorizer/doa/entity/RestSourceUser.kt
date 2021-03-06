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

package org.radarbase.authorizer.doa.entity

import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "rest_source_user")
class RestSourceUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(
        name = "sequenceGenerator",
        sequenceName = "rest_source_user_id_seq",
        initialValue = 1,
        allocationSize = 1,
    )
    var id: Long? = null

    // Date when the user is created
    @Column(name = "created_at")
    var createdAt: Instant? = Instant.now()

    // Project ID to be used in org.radarcns.kafka.ObservationKey record keys
    @Column(name = "project_id")
    var projectId: String? = null

    // User ID to be used in org.radarcns.kafka.ObservationKey record keys
    @Column(name = "user_id")
    var userId: String? = null

    // Source ID to be used in org.radarcns.kafka.ObservationKey record keys
    @Column(name = "source_id")
    var sourceId: String = UUID.randomUUID().toString()

    @Column(name = "source_type")
    lateinit var sourceType: String

    // Date from when to collect data.
    @Column(name = "start_date")
    lateinit var startDate: Instant

    @Column(name = "end_date")
    var endDate: Instant? = null

    @Column(name = "external_user_id")
    lateinit var externalUserId: String

    // is authorized by user
    @Column(name = "authorized")
    var authorized = false

    @Column(name = "access_token")
    var accessToken: String? = null

    @Column(name = "refresh_token")
    var refreshToken: String? = null

    @Column(name = "expires_in")
    var expiresIn: Int? = null

    @Column(name = "expires_at")
    var expiresAt: Instant? = null

    @Column(name = "token_type")
    var tokenType: String? = null

    // The version to be appended to ID for RESET of a user
    // This should be updated whenever the user is RESET.
    // By default this is null for backwards compatibility
    @Column(name = "version")
    var version: String? = null

    // The number of times a user has been reset
    @Column(name = "times_reset")
    var timesReset: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other == null || javaClass != other.javaClass) return false

        other as RestSourceUser

        return id != null && id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"

    fun hasValidToken(): Boolean = this.authorized && this.accessToken != null && Instant.now().isBefore(this.expiresAt)
}
