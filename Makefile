JC = /usr/site/jdk-1.1.6/bin/javac -classpath .:../machine:../filesys:../userprog:../threads:../vm:/usr/site/jdk-1.1.6/lib/classes.zip

all:
	${JC} ../threads/Nachos.java

cleanclass:
	(find .. -name "*.class" -print | xargs rm)

cleanbackup:
	(find .. -name "*~" -print | xargs rm)
