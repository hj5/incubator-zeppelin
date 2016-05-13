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

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 文件操作工具类
 * 
 * @author hj
 *
 */
public class FileUtil {

  private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

  public static File createFile(String destFileName) {
    File file = new File(destFileName);
    createFile(destFileName, file);
    return file;
  }

  public static boolean createFile(String destFileName, File file) {

    if (file.exists()) {
      logger.error("创建单个文件" + destFileName + "失败，目标文件已存在！");
      return false;
    }
    if (destFileName.endsWith(File.separator)) {
      logger.error("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
      return false;
    }
    // 判断目标文件所在的目录是否存在
    if (!file.getParentFile().exists()) {
      // 如果目标文件所在的目录不存在，则创建父目录
      logger.info("目标文件所在目录不存在，准备创建它！");
      if (!file.getParentFile().mkdirs()) {
        logger.error("创建目标文件所在目录失败！");
        return false;
      }
    }
    // 创建目标文件
    try {
      if (file.createNewFile()) {
        logger.info("创建单个文件" + destFileName + "成功！");
        return true;
      } else {
        logger.error("创建单个文件" + destFileName + "失败！");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("创建单个文件" + destFileName + "失败！" + e.getMessage());
      return false;
    }
  }

  public static boolean createDir(String destDirName) {
    File dir = new File(destDirName);
    if (dir.exists()) {
      logger.error("创建目录" + destDirName + "失败，目标目录已经存在");
      return false;
    }
    if (!destDirName.endsWith(File.separator)) {
      destDirName = destDirName + File.separator;
    }
    // 创建目录
    if (dir.mkdirs()) {
      logger.info("创建目录" + destDirName + "成功！");
      return true;
    } else {
      logger.error("创建目录" + destDirName + "失败！");
      return false;
    }
  }

  public static File createTempFile(String prefix, String suffix, String dirName) {
    File tempFile = null;
    if (dirName == null) {
      try {
        // 在默认文件夹下创建临时文件
        tempFile = File.createTempFile(prefix, suffix);
        // 返回临时文件的路径
        return tempFile;
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("创建临时文件失败！" + e.getMessage());
        return null;
      }
    } else {
      File dir = new File(dirName);
      // 如果临时文件所在目录不存在，首先创建
      if (!dir.exists()) {
        if (!FileUtil.createDir(dirName)) {
          logger.error("创建临时文件失败，不能创建临时文件所在的目录！");
          return null;
        }
      }
      try {
        // 在指定目录下创建临时文件
        tempFile = File.createTempFile(prefix, suffix, dir);
        return tempFile;
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("创建临时文件失败！" + e.getMessage());
        return null;
      }
    }
  }

  // 删除文件夹
  // param folderPath 文件夹完整绝对路径

  public static void delFolder(String folderPath) {
    try {
      delAllFile(folderPath); // 删除完里面所有内容
      String filePath = folderPath;
      filePath = filePath.toString();
      java.io.File myFilePath = new java.io.File(filePath);
      myFilePath.delete(); // 删除空文件夹
    } catch (Exception e) {
      logger.error("删除文件夹失败！" + e.getMessage());
      e.printStackTrace();
    }
  }

  /*
   * 删除指定文件夹下所有.java和.class文件 或 文件列表
   * 
   * @param path 文件夹完整绝对路径 或 文件列表
   * 
   * @return
   */
  public static boolean delAllFile(String path) {
    boolean flag = false;
    File file = new File(path);
    if (!file.exists()) {
      return flag;
    }
    if (!file.isDirectory()) {
      return flag;
    }
    String[] tempList = file.list();
    File temp = null;
    for (int i = 0; i < tempList.length; i++) {
      if (path.endsWith(File.separator)) {
        temp = new File(path + tempList[i]);
      } else {
        temp = new File(path + File.separator + tempList[i]);
      }
      if (temp.isFile()
          && (temp.getAbsolutePath().endsWith(".java") 
              || temp.getAbsolutePath().endsWith(".class"))
          && temp.getAbsolutePath().contains("JavaRuntime")) {
        //用作缓存的vo不删除
        temp.delete();
      }
      if (temp.isDirectory()) {//不做目录内文件删除，只删除一级目录下文件
        // delAllFile(path + "/" + tempList[i]); // 先删除文件夹里面的文件
        // delFolder(path + "/" + tempList[i]); // 再删除空文件夹
        flag = true;
      }
    }
    return flag;
  }

  public static void main(String[] args) {
    // 创建目录
    String dirName = "tempdir";
    FileUtil.createDir(dirName);
    // 创建文件
    String fileName = dirName + "/temp2/tempFile.txt";
    FileUtil.createFile(fileName);
    // 创建临时文件
    String prefix = "temp";
    String suffix = ".txt";
    for (int i = 0; i < 10; i++) {
      System.out.println("创建了临时文件：" + FileUtil.createTempFile(prefix, suffix, dirName));
    }
    // 在默认目录下创建临时文件
    for (int i = 0; i < 10; i++) {
      System.out.println("在默认目录下创建了临时文件：" + FileUtil.createTempFile(prefix, suffix, null));
    }
    FileUtil.delFolder(dirName);
  }

}
