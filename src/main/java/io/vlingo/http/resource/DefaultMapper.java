// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.http.resource;

import io.vlingo.http.resource.serialization.JsonSerialization;

public class DefaultMapper implements Mapper {
  public static final Mapper instance = new DefaultMapper();
  
  @Override
  public <T> T from(final String data, final Class<T> type) {
    return JsonSerialization.deserialized(data, type);
  }

  @Override
  public <T> String from(final T data) {
    return JsonSerialization.serialized(data);
  }
}
