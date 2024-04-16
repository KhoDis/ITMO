@echo off
D:
cd \Study\ITMO\Programming\paradigms\src
set path=%path%;D:\Programs\AdoptOpenJDK\jdk-14.0.2.12-hotspot\bin\java.exe
javac search\*.java
java -jar search\BinarySearchMissingTest.jar
del /f search\*.class

cmd /k