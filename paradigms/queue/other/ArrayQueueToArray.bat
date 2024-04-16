@echo off
D:
cd \Study\ITMO\Programming\paradigms\src
javac queue\*.java
java -ea -jar queue\ArrayQueueToArrayTest.jar
del /f queue\*.class

cmd /k