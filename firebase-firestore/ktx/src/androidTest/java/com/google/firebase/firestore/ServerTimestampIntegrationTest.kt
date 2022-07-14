// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.firebase.firestore

import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot.ServerTimestampBehavior
import com.google.firebase.firestore.ktx.annotations.KServerTimestamp
import com.google.firebase.firestore.setData
import com.google.firebase.firestore.testCollection
import com.google.firebase.firestore.waitFor
import java.util.Date
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.junit.Test

class ServerTimestampIntegrationTest {

    @Test
    fun encoding_Timestamp_is_supported() {
        val docRefKotlin = testCollection("ktx").document("123")
        val docRefPOJO = testCollection("pojo").document("456")

        @Serializable
        class TimestampPOJO {
            @Contextual @KServerTimestamp @ServerTimestamp var timestamp1: Timestamp? = null

            @Contextual @KServerTimestamp @ServerTimestamp val timestamp2: Date? = null
        }

        val timestampPOJO = TimestampPOJO()
        timestampPOJO.timestamp1 = Timestamp(Date(100L))
        docRefPOJO.set(timestampPOJO)
        docRefKotlin.setData(timestampPOJO)
        val expected = waitFor(docRefKotlin.get()).getData(ServerTimestampBehavior.NONE)
        val actual = waitFor(docRefPOJO.get()).getData(ServerTimestampBehavior.NONE)
        assertThat(expected).containsExactlyEntriesIn(actual)
    }

    // TODO: Add more integration test
}
