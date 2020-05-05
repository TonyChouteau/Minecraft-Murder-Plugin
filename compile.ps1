javac -classpath ".\lib\craftbukkit-1.15.2.jar" .\src\fr\tonychouteau\murder\bukkit\MurderPlugin.java .\src\fr\tonychouteau\murder\bukkit\listener\PlayerListener.java -d out -target 1.8 -source 1.8
cd .\out
jar -cvf MurderPlugin.jar *
cd ..