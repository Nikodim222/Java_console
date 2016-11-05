#!/bin/sh
export jdkbin="/opt/jdk1.8.0_20/bin"
export project="Java_console"

find . -name "*.java" -type f -exec chmod -xw+r {} \;
find . -name "*.class" -type f -exec  rm -f {} \;
rm -f ./$project.jar
rm -Rf ./bin

mkdir ./bin && $jdkbin/javac ./src/main/startIt.java -g -d ./bin -sourcepath ./src && $jdkbin/jar mcvf ./MANIFEST.MF ./$project.jar -C bin/ . && chmod 700 ./$project.jar && chmod u+rx-w ./$project.jar && rm -Rf ./bin && $jdkbin/java -jar ./$project.jar "$@"
