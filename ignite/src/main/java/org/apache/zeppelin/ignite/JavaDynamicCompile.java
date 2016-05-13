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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.ignite.Ignite;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * compile and run str.
 * 
 * @author hj
 *
 */
public class JavaDynamicCompile {

  private static Logger logger = LoggerFactory.getLogger(JavaDynamicCompile.class);

  private static synchronized void compile(File source, OutputStream consoleOut) throws Exception {
    // 编译生成的java文件
    // String[] cpargs = new String[] { "-d", source.getParent(), source.getName() };
    // int status = Main.compile(cpargs);
    // 动态编译
    JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
    // int status = javac.run(null, null, null, "-d", System.getProperty("user.dir") + "/bin",
    // file.getName());
    int status = javac.run(null, consoleOut, consoleOut, source.getCanonicalPath());

    if (status != 0) {
      Exception e = new Exception("没有编译成功！语法错误！\n"
          + (consoleOut == null ? "" : consoleOut.toString().replaceAll("JavaRuntime.*\\.java",
              "->")));
      logger.info("compile error！", e);
      throw e;
    }
  }

  public static synchronized void run(String code, Ignite ignite, OutputStream consoleOut)
      throws Throwable {
    File sourceFile = null;
    try {
      sourceFile = makeSource(code);

      String classname = getBaseFileName(sourceFile);

      compile(sourceFile, consoleOut);

      new File(classname + ".class").deleteOnExit();

      // URL[] urls = new URL[] { new URL("file:" + sourceFile.getParent() + "/") };
      // URLClassLoader classLoader = new URLClassLoader(urls);
      ClassLoader classLoader = null;
      try {
        classLoader = U.gridClassLoader();
        logger.info("classLoader======>" + classLoader);
      } catch (Exception e) {
      } finally {
        if (classLoader == null) {
          if (ignite == null) {
            logger.error("请初始化ignite运行环境！");
            throw new Exception("请初始化ignite运行环境！");
          }
          classLoader = ignite.getClass().getClassLoader();
        }
      }
      if (classLoader == null) {
        logger.error("JavaDynamicCompile.java:classLoader is null");
        throw new Exception("JavaDynamicCompile.java:classLoader is null");
      }
      Class.forName(classname);
      Class class1 = classLoader.loadClass(classname);
      Method method = class1.getDeclaredMethod(classname + "Method", Ignite.class);
      Object[] args1 = { ignite };
      method.invoke(class1.newInstance(), args1);
    } finally {
      if (sourceFile != null)
        FileUtil.delAllFile(sourceFile.getParent());
    }

  }

  /**
   * java源文件创建：创建临时文件及其存储目录
   * 
   * @return File：java source file
   * 
   * @throws Throwable
   */
  private static File makeSource(String code) throws Throwable {
    // String tmpDir = "runtimeTmp" + UUIDGenerator.getUUID();
    // File sourceFile = FileUtil.createTempFile("JavaRuntime", ".java", tmpDir);
    // sourceFile.deleteOnExit();
    String zeppelin_server = System.getProperty("user.dir");
    StringBuffer sb = new StringBuffer(zeppelin_server);
    // sb.append(File.separator).append("interpreter").append(File.separator).append("ignite")
    sb.append(File.separator).append("JavaRuntime").append(UUIDGenerator.getUUID()).append(".java");
    String tmpFile = sb.toString();
    File sourceFile = FileUtil.createFile(tmpFile);
    // 获得类名
    String classname = getBaseFileName(sourceFile);
    // 将代码输出到文件
    PrintWriter out = new PrintWriter(new FileOutputStream(sourceFile));
    out.println(getClassCode(code, classname));
    out.close();
    return sourceFile;
  }

  private static String getClassCode(String code, String className) throws Exception {
    StringBuffer text = new StringBuffer();

    String[] codeArr = code.split("\\$object\\$", 2);
    if (codeArr.length == 2) {
      if (codeArr[0].trim().startsWith("import") && !codeArr[0].trim().equals("")) {
        text.append(codeArr[0]);
      }
    } else {
      throwFormatErr("(1)");
    }
    String m = "import org.apache.ignite.Ignite;\nimport org.apache.ignite.IgniteCache;\nimport org.apache.ignite.Ignition;\nimport org.apache.ignite.configuration.CacheConfiguration;\nimport org.apache.ignite.configuration.IgniteConfiguration;\nimport org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;\nimport org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;\nimport org.apache.zeppelin.interpreter.InterpreterContext;\nimport org.apache.zeppelin.interpreter.InterpreterResult;\nimport org.apache.zeppelin.interpreter.InterpreterResult.Code;\nimport org.apache.zeppelin.interpreter.InterpreterResult.Type;\nimport org.apache.ignite.cache.query.annotations.QuerySqlField;\n";
    text.append(m);
    codeArr = codeArr[1].split("\\$main\\$");
    if (codeArr.length == 2) {
      if (codeArr[0].contains("class ") || codeArr[0].trim().startsWith("final")
          || codeArr[0].trim().equals("")) {
        text.append(codeArr[0]);
      } else {
        throwFormatErr("(2)");
      }

      text.append("public class " + className + "{\n");
      text.append(" public static void " + className + "Method(Ignite ignite){\n");
      text.append(codeArr[1]);
      text.append(" }\n");
      text.append("}");
      return text.toString();
    } else if (codeArr.length > 2) {
      throw new Exception("格式错误：代码中只能存在1个$main$关键字");
    } else {
      throw new Exception("格式错误：正确格式：\n“import ...\n$main$:\nyour main code ...”");
    }
  }

  private static String getBaseFileName(File file) {
    String fileName = file.getName();
    int index = fileName.indexOf(".");
    String result = "";
    if (index != -1) {
      result = fileName.substring(0, index);
    } else {
      result = fileName;
    }
    return result;
  }

  private static String throwFormatErr(String sign) throws Exception {
    Exception e = new Exception(
        sign
            + "格式错误，正确格式：\n“import ...\n$object$\ndefine class（类名不能以‘JavaRuntime’开头）...\n$main$\nyour main code ...”");
    logger.info("compile error！", e);
    throw e;
  }
}
