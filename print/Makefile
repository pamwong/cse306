#!bin/sh


# Make a Postscript copy of the Nachos code, suitable for duplication

COPYRIGHT=COPYRIGHT

README=README.stark README.druschel test/README

MAKEFILES=Makefile test/Makefile bin/Makefile

KERNEL=\
	nachos/kernel/Nachos.java\
	nachos/kernel/devices/DiskDriver.java\
	nachos/kernel/devices/InterruptHandler.java\
	nachos/kernel/devices/test/ConsoleTest.java\
	nachos/kernel/filesys/BitMap.java\
	nachos/kernel/filesys/Directory.java\
	nachos/kernel/filesys/DirectoryEntry.java\
	nachos/kernel/filesys/FileHeader.java\
	nachos/kernel/filesys/FileSystem.java\
	nachos/kernel/filesys/FileSystemReal.java\
	nachos/kernel/filesys/FileSystemStub.java\
	nachos/kernel/filesys/OpenFile.java\
	nachos/kernel/filesys/OpenFileReal.java\
	nachos/kernel/filesys/OpenFileStub.java\
	nachos/kernel/filesys/test/FileSystemTest.java\
	nachos/kernel/threads/Condition.java\
	nachos/kernel/threads/List.java\
	nachos/kernel/threads/Lock.java\
	nachos/kernel/threads/Scheduler.java\
	nachos/kernel/threads/Semaphore.java\
	nachos/kernel/threads/SynchList.java\
	nachos/kernel/threads/test/AlarmTest.java\
	nachos/kernel/threads/test/ThreadTest.java\
	nachos/kernel/userprog/AddrSpace.java\
	nachos/kernel/userprog/ExceptionHandler.java\
	nachos/kernel/userprog/NoffHeader.java\
	nachos/kernel/userprog/Syscall.java\
	nachos/kernel/userprog/UserThread.java\
	nachos/kernel/userprog/test/ProgTest.java\
	nachos/Statistics.java\
	nachos/Debug.java

MACHINE=machine/Console.java\
	machine/Disk.java\
	machine/ExceptionHandler.java\
	machine/GUIConsole.java\
	machine/Interrupt.java\
	machine/InterruptHandler.java\
	machine/Machine.java\
	machine/NachosThread.java\
	machine/PendingInterrupt.java\
	machine/SegmentDescriptor.java\
	machine/StreamConsole.java\
	machine/Timer.java\
	machine/TranslationEntry.java

TEST=   include/copyright.h\
	include/syscall.h\
	test/halt.c\
	test/matmult.c\
	test/shell.c\
	test/sort.c\
	test/start.s

BIN=bin/coff.h bin/noff.h bin/coff2noff.c

all: nachos.ps

nachos.ps:
	cd ..; \
	a2ps -o - -b --margin=24 --pretty-print --print-anyway=1 \
		${COPYRIGHT} ${README} ${MAKEFILES} \
		${KERNEL} ${TEST} > print/nachos.ps

clean:
	rm -f *~ \#* *.ps
