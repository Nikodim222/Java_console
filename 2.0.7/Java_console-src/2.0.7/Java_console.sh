#!/bin/sh
export jdkbin="/opt/jdk1.8.0_20/bin"
export project="Java_console"

$jdkbin/java -jar ./$project.jar "$@"
