JC= javac -classpath machine.jar:.

all:
	${JC} nachos/kernel/Nachos.java

machine.jar: nachos/machine/*.java
	${JC} nachos/machine/*.java
	jar cvf machine.jar nachos/machine/*.class

javadoc:
	makejavadoc

cleanclass:
	(find . -name "*.class" -print | xargs rm)

cleanbackup:
	(find . -name "*~" -print | xargs rm)


