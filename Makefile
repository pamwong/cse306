# Uncomment the following when running on Windows.
JC= javac -classpath 'machine.jar;.'

# Uncomment the following when running on Unix.
#JC= javac -classpath 'machine.jar:.'

classes: machine.jar
	${JC} nachos/kernel/Nachos.java

# The machine.jar file is not to be rebuilt by students.
# The Cygwin "make" seems to insist on the Java source files existing
# even when machine.jar is present, so leave this commented out.
#
# machine.jar: nachos/machine/*.java
#	${JC} nachos/machine/*.java
#	jar cvf machine.jar nachos/machine/*.class

javadoc:
	makejavadoc

cleanclass:
	(find . -name "*.class" -print | xargs rm)

cleanbackup:
	(find . -name "*~" -print | xargs rm)


