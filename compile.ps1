javac -classpath ".\lib\craftbukkit-1.15.2.jar" .\src\fr\tonychouteau\murder\bukkit\*.java .\src\fr\tonychouteau\murder\bukkit\listener\*.java .\src\fr\tonychouteau\murder\bukkit\commands\*.java .\src\fr\tonychouteau\murder\bukkit\util\*.java .\src\fr\tonychouteau\murder\bukkit\game\*.java -d out -target 1.8 -source 1.8
# -Xlint:unchecked
cd .\out
jar -cvf MurderPlugin.jar *
cd ..