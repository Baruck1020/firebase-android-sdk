// Copyright 2018 Google LLC
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

package com.google.android.datatransport.runtime.scheduling.persistence;

import static com.google.common.truth.Truth.assertThat;

import com.google.android.datatransport.Priority;
import com.google.android.datatransport.runtime.EventInternal;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class InMemoryEventStoreTest {
  private static final String BACKEND_NAME = "backend1";

  private static final EventInternal TEST_EVENT =
      EventInternal.builder()
          .setTransportName("transport")
          .setPriority(Priority.DEFAULT)
          .setEventMillis(1)
          .setUptimeMillis(2)
          .setPayload("hello".getBytes())
          .build();
  private static final PersistedEvent TEST_PERSISTED_EVENT =
      PersistedEvent.create(1, BACKEND_NAME, TEST_EVENT);

  private final EventStore store = new InMemoryEventStore();

  @Test
  public void test_emptyStore_shouldReturnNothingUponLoadAll() {
    assertThat(store.loadAll("foo")).isEmpty();
  }

  @Test
  public void test_nonEmptyStore_shouldReturnItsStoredEvents() {
    store.persist(BACKEND_NAME, TEST_EVENT);

    assertThat(store.loadAll(BACKEND_NAME)).containsExactly(TEST_PERSISTED_EVENT);
  }

  @Test
  public void recordSuccess_shouldRemoveEventsFromStorage() {
    store.persist(BACKEND_NAME, TEST_EVENT);

    store.recordSuccess(Collections.singleton(TEST_PERSISTED_EVENT));

    assertThat(store.loadAll(BACKEND_NAME)).isEmpty();
  }

  @Test
  public void recordFailure_shouldRemoveEventsFromStorage() {
    store.persist(BACKEND_NAME, TEST_EVENT);

    store.recordFailure(Collections.singleton(TEST_PERSISTED_EVENT));

    assertThat(store.loadAll(BACKEND_NAME)).isEmpty();
  }
}
