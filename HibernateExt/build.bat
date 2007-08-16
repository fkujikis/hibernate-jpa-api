set HIBERNATECORE=%~dp0..\hibernate-3.2
java -cp "%HIBERNATECORE%\lib\ant-launcher-1.6.5.jar" org.apache.tools.ant.launch.Launcher -lib %HIBERNATECORE%/lib %1 %2 %3 %4 %5
