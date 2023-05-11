#!/bin/bash
set -e

# The Spring Boot 2 and 3 starters are very similar in nature. This script:
#
#   - Copies test and source folders from Spring 6 and Spring Boot Starter 3 to 5 and 2 respectively.
#   - Replaces javax with Jakarta
#   - Replaces spring6 with spring5

if [ ! $# -eq 1 ]; then 
    echo "Please provide the directory containing the JVM sdk. E.g.: $0 ./jvm"
    exit
fi

SPRING_5=$1/wirequery-spring-5
SPRING_6=$1/wirequery-spring-6
SPRING_BOOT_2=$1/wirequery-spring-boot-2-starter
SPRING_BOOT_3=$1/wirequery-spring-boot-3-starter

checkExists () {
    if [ ! -d "$2" ]; then
        echo "$1 SDK does not exist: $2."
        exit
    fi
}

function backportFromSpring6() {
    local TARGET_DIR=$1
    local SOURCE_DIR=$2
    local SOURCES_ROOT=$3
    local TARGET_DIR_MAIN_PKG=$4
    local SOURCE_DIR_MAIN_PKG=$5

    rm -rf "$TARGET_DIR/$SOURCES_ROOT"
    cp -r "$SOURCE_DIR/$SOURCES_ROOT" "$TARGET_DIR/$SOURCES_ROOT"
    find "$TARGET_DIR/$SOURCES_ROOT" -type f -exec sed -i '' -e 's/spring6/spring5/g' {} \;
    find "$TARGET_DIR/$SOURCES_ROOT" -type f -exec sed -i '' -e 's/springboot3/springboot2/g' {} \;
    find "$TARGET_DIR/$SOURCES_ROOT" -type f -exec sed -i '' -e 's/jakarta/javax/g' {} \;
    mv "$TARGET_DIR/$SOURCES_ROOT/$SOURCE_DIR_MAIN_PKG" "$TARGET_DIR/$SOURCES_ROOT/$TARGET_DIR_MAIN_PKG"
}

checkExists "WireQuery Spring 5 SDK" $SPRING_5
checkExists "WireQuery Spring 6 SDK" $SPRING_6
checkExists "WireQuery Spring Boot Starter 2 SDK" $SPRING_BOOT_2
checkExists "WireQuery Spring Boot Starter 3 SDK" $SPRING_BOOT_3

backportFromSpring6 $SPRING_5 $SPRING_6 "src/main/kotlin" "com/wirequery/spring5" "com/wirequery/spring6"
backportFromSpring6 $SPRING_5 $SPRING_6 "src/test/kotlin" "com/wirequery/spring5" "com/wirequery/spring6"
backportFromSpring6 $SPRING_BOOT_2 $SPRING_BOOT_3 "src/main/kotlin" "com/wirequery/springboot2" "com/wirequery/springboot3"
backportFromSpring6 $SPRING_BOOT_2 $SPRING_BOOT_3 "src/test/kotlin" "com/wirequery/springboot2" "com/wirequery/springboot3"
