#
# Makefile for NACHOS.
# Not very compact, but it should be compatible with pretty much any version
# of "make".  Using "make" should help avoid strange situations where "javac"
# fails to compile things that it probably ought to.
#
# When you add a source file to NACHOS, also add additional lines below,
# following the existing pattern.  Note that many of the lines below start
# with a TAB character, not spaces, and this is essential for correct
# interpretation by "make".
#

# Uncomment the following when running on Windows.
JC= javac -classpath 'machine.jar;.'

# Uncomment the following when running on Unix.
#JC= javac -classpath 'machine.jar:.'

MACHINE= machine.jar

default: kernel

# The machine.jar file is not to be rebuilt by students.
# The Cygwin "make" seems to insist on the Java source files existing
# even when machine.jar is present, so leave this commented out.
#
$(MACHINE): nachos/machine/*.java
	${JC} nachos/machine/*.java
	jar cvf machine.jar nachos/machine/*.class

javadoc:
	makejavadoc

cleanclass:
	(find . -name "*.class" -print | xargs rm -f)

cleanbackup:
	(find . -name "*~" -print | xargs rm -f)

KERNEL_CLASSES=\
	nachos/kernel/Nachos.class \
	nachos/kernel/devices/DiskDriver.class \
	nachos/kernel/devices/InterruptHandler.class \
	nachos/kernel/devices/test/ConsoleTest.class \
	nachos/kernel/filesys/BitMap.class \
	nachos/kernel/filesys/Directory.class \
	nachos/kernel/filesys/DirectoryEntry.class \
	nachos/kernel/filesys/FileHeader.class \
	nachos/kernel/filesys/FileSystem.class \
	nachos/kernel/filesys/FileSystemReal.class \
	nachos/kernel/filesys/FileSystemStub.class \
	nachos/kernel/filesys/OpenFile.class \
	nachos/kernel/filesys/OpenFileReal.class \
	nachos/kernel/filesys/OpenFileStub.class \
	nachos/kernel/filesys/test/FileSystemTest.class \
	nachos/kernel/threads/Condition.class \
	nachos/kernel/threads/List.class \
	nachos/kernel/threads/Lock.class \
	nachos/kernel/threads/Scheduler.class \
	nachos/kernel/threads/Semaphore.class \
	nachos/kernel/threads/SynchList.class \
	nachos/kernel/threads/test/AlarmTest.class \
	nachos/kernel/threads/test/ThreadTest.class \
	nachos/kernel/userprog/AddrSpace.class \
	nachos/kernel/userprog/ExceptionHandler.class \
	nachos/kernel/userprog/NoffHeader.class \
	nachos/kernel/userprog/Syscall.class \
	nachos/kernel/userprog/UserThread.class \
	nachos/kernel/userprog/test/ProgTest.class \
	nachos/Statistics.class \
	nachos/Debug.class

kernel: $(KERNEL_CLASSES)

nachos/kernel/Nachos.class: $(MACHINE) nachos/kernel/Nachos.java
	${JC} nachos/kernel/Nachos.java

nachos/kernel/userprog/test/ProgTest.class: $(MACHINE) nachos/kernel/userprog/test/ProgTest.java
	${JC} nachos/kernel/userprog/test/ProgTest.java

nachos/kernel/userprog/ExceptionHandler.class: $(MACHINE) nachos/kernel/userprog/ExceptionHandler.java
	${JC} nachos/kernel/userprog/ExceptionHandler.java

nachos/kernel/userprog/NoffHeader.class: $(MACHINE) nachos/kernel/userprog/NoffHeader.java
	${JC} nachos/kernel/userprog/NoffHeader.java

nachos/kernel/userprog/Syscall.class: $(MACHINE) nachos/kernel/userprog/Syscall.java
	${JC} nachos/kernel/userprog/Syscall.java

nachos/kernel/userprog/UserThread.class: $(MACHINE) nachos/kernel/userprog/UserThread.java
	${JC} nachos/kernel/userprog/UserThread.java

nachos/kernel/userprog/AddrSpace.class: $(MACHINE) nachos/kernel/userprog/AddrSpace.java
	${JC} nachos/kernel/userprog/AddrSpace.java

nachos/kernel/devices/test/ConsoleTest.class: $(MACHINE) nachos/kernel/devices/test/ConsoleTest.java
	${JC} nachos/kernel/devices/test/ConsoleTest.java

nachos/kernel/devices/InterruptHandler.class: $(MACHINE) nachos/kernel/devices/InterruptHandler.java
	${JC} nachos/kernel/devices/InterruptHandler.java

nachos/kernel/devices/DiskDriver.class: $(MACHINE) nachos/kernel/devices/DiskDriver.java
	${JC} nachos/kernel/devices/DiskDriver.java

nachos/kernel/filesys/test/FileSystemTest.class: $(MACHINE) nachos/kernel/filesys/test/FileSystemTest.java
	${JC} nachos/kernel/filesys/test/FileSystemTest.java

nachos/kernel/filesys/DirectoryEntry.class: $(MACHINE) nachos/kernel/filesys/DirectoryEntry.java
	${JC} nachos/kernel/filesys/DirectoryEntry.java

nachos/kernel/filesys/FileHeader.class: $(MACHINE) nachos/kernel/filesys/FileHeader.java
	${JC} nachos/kernel/filesys/FileHeader.java

nachos/kernel/filesys/FileSystem.class: $(MACHINE) nachos/kernel/filesys/FileSystem.java
	${JC} nachos/kernel/filesys/FileSystem.java

nachos/kernel/filesys/FileSystemReal.class: $(MACHINE) nachos/kernel/filesys/FileSystemReal.java
	${JC} nachos/kernel/filesys/FileSystemReal.java

nachos/kernel/filesys/FileSystemStub.class: $(MACHINE) nachos/kernel/filesys/FileSystemStub.java
	${JC} nachos/kernel/filesys/FileSystemStub.java

nachos/kernel/filesys/OpenFile.class: $(MACHINE) nachos/kernel/filesys/OpenFile.java
	${JC} nachos/kernel/filesys/OpenFile.java

nachos/kernel/filesys/OpenFileReal.class: $(MACHINE) nachos/kernel/filesys/OpenFileReal.java
	${JC} nachos/kernel/filesys/OpenFileReal.java

nachos/kernel/filesys/OpenFileStub.class: $(MACHINE) nachos/kernel/filesys/OpenFileStub.java
	${JC} nachos/kernel/filesys/OpenFileStub.java

nachos/kernel/filesys/BitMap.class: $(MACHINE) nachos/kernel/filesys/BitMap.java
	${JC} nachos/kernel/filesys/BitMap.java

nachos/kernel/filesys/Directory.class: $(MACHINE) nachos/kernel/filesys/Directory.java
	${JC} nachos/kernel/filesys/Directory.java

nachos/kernel/threads/test/AlarmTest.class: $(MACHINE) nachos/kernel/threads/test/AlarmTest.java
	${JC} nachos/kernel/threads/test/AlarmTest.java

nachos/kernel/threads/test/ThreadTest.class: $(MACHINE) nachos/kernel/threads/test/ThreadTest.java
	${JC} nachos/kernel/threads/test/ThreadTest.java

nachos/kernel/threads/List.class: $(MACHINE) nachos/kernel/threads/List.java
	${JC} nachos/kernel/threads/List.java

nachos/kernel/threads/Lock.class: $(MACHINE) nachos/kernel/threads/Lock.java
	${JC} nachos/kernel/threads/Lock.java

nachos/kernel/threads/Scheduler.class: $(MACHINE) nachos/kernel/threads/Scheduler.java
	${JC} nachos/kernel/threads/Scheduler.java

nachos/kernel/threads/Semaphore.class: $(MACHINE) nachos/kernel/threads/Semaphore.java
	${JC} nachos/kernel/threads/Semaphore.java

nachos/kernel/threads/SynchList.class: $(MACHINE) nachos/kernel/threads/SynchList.java
	${JC} nachos/kernel/threads/SynchList.java

nachos/kernel/threads/Condition.class: $(MACHINE) nachos/kernel/threads/Condition.java
	${JC} nachos/kernel/threads/Condition.java

nachos/Statistics.class: $(MACHINE) nachos/Statistics.java
	${JC} nachos/Statistics.java

nachos/Debug.class: $(MACHINE) nachos/Debug.java
	${JC} nachos/Debug.java
