#!bin/sh


# Make a Postscript copy of the Nachos code, suitable for duplication

COPYRIGHT=../COPYRIGHT

README=README test/README

MAKEFILES=threads/Makefile test/Makefile bin/Makefile

THREADS=threads/AssertException.java\
	threads/Condition.java\
	threads/Debug.java\
	threads/List.java\
	threads/ListElement.java\
	threads/ListTest.java\
	threads/Lock.java\
	threads/Nachos.java\
	threads/NachosThread.java\
	threads/Printable.java\
	threads/Scheduler.java\
	threads/Semaphore.java\
	threads/SynchList.java\
	threads/ThreadTest.java

USERPROG=userprog/AddrSpace.java\
	userprog/BitMap.java\
	userprog/ConsoleTest.java\
	userprog/NoffHeader.java\
	userprog/ProgTest.java

MACHINE=machine/Console.java\
	machine/Disk.java\
	machine/Interrupt.java\
	machine/Machine.java\
	machine/PendingInterrupt.java\
	machine/Statistics.java\
	machine/Timer.java\
	machine/TranslationEntry.java

FILESYS=filesys/Directory.java\
	filesys/DirectoryEntry.java\
	filesys/FileHeader.java\
	filesys/FileSystem.java\
	filesys/FileSystemReal.java\
	filesys/FileSystemStub.java\
	filesys/FileSystemTest.java\
	filesys/OpenFile.java\
	filesys/OpenFileReal.java\
	filesys/OpenFileStub.java\
	filesys/SynchDisk.java

INCLUDE=include/copyright.h\
	include/syscall.h

TEST=test/halt.c\
	test/matmult.c\
	test/shell.c\
	test/sort.c\
	test/start.s

BIN=bin/coff.h bin/noff.h bin/coff2noff.c

all: nachos.ps

nachos.ps:
	cd ../code; \
	a2ps -o - -b --margin=24 --pretty-print \
		${COPYRIGHT} ${README} \
		${MAKEFILES} ${INCLUDE} ${THREADS} ${USERPROG} ${FILESYS} \
		${TEST} ${MACHINE} ${BIN} > ../print/nachos.ps

clean:
	rm -f *~ \#* *.ps
