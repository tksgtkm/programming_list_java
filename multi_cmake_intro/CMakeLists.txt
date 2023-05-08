cmake_minimum_required(VERSION 3.10)
project(JavaSample CXX)

# Javaのバージョンを設定
find_package(Java REQUIRED)
include(UseJava)

# Javaのソースファイルを指定
set(SRC_FILES
src/main/java/multi/Greeter.java
  src/main/java/multi/English.java
  src/main/java/multi/French.java
  src/main/java/multi/GreeterFactory.java
  src/main/java/multi/GreeterTest.java
)

# Javaのバージョンを指定
set(CMAKE_JAVA_COMPILE_FLAGS "-source" "17" "-target" "17")

# Javaプロジェクトの設定
add_jar(GreeterTest
  SOURCES ${SRC_FILES}
  ENTRY_POINT multi.GreeterTest
  OUTPUT_DIR ${CMAKE_BINARY_DIR}/output
)