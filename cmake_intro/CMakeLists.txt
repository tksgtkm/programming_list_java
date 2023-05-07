cmake_minimum_required(VERSION 3.10)
project(JavaSample CXX)

# Javaのバージョンを設定
find_package(Java REQUIRED)
include(UseJava)

# Javaのソースファイルを指定
set(SRC_FILES
  src/main/java/intro/IfSample.java
)

# Javaのバージョンを指定
set(CMAKE_JAVA_COMPILE_FLAGS "-source" "17" "-target" "17")

# Javaプロジェクトの設定
add_jar(IfSample
  SOURCES ${SRC_FILES}
  ENTRY_POINT intro.IfSample
  OUTPUT_DIR ${CMAKE_BINARY_DIR}/output
)