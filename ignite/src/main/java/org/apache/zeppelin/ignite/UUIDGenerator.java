/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.ignite;

import java.util.UUID;

/**
 * UUID生成器
 * @author hj
 *
 */
public class UUIDGenerator {
  public UUIDGenerator() {
  }

  public static String getUUID() {
    UUID uuid = UUID.randomUUID();
    String str = uuid.toString();
    // 去掉"-"符号
    String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18)
        + str.substring(19, 23) + str.substring(24);
    return temp;
  }

  // 获得指定数量的UUID
  public static String[] getUUID(int number) {
    if (number < 1) {
      return null;
    }
    String[] ss = new String[number];
    for (int i = 0; i < number; i++) {
      ss[i] = getUUID();
    }
    return ss;
  }

  public static void main(String[] args) {
    String[] ss = getUUID(10);
    for (int i = 0; i < ss.length; i++) {
      System.out.println("ss[" + i + "]=====" + ss[i]);
    }
  }
}
