# use normal make for this Makefile
#
# Makefile for building user programs to run on top of Nachos
#
# Several things to be aware of:
#
#    Nachos assumes that the location of the program startup routine (the
# 	location the kernel jumps to when the program initially starts up)
#       is at location 0.  This means: start.o must be the first .o passed 
# 	to ld, in order for the routine "Start" to be loaded at location 0
#

.SUFFIXES:

NACHOSDIR=..
INCDIR = $(NACHOSDIR)/include

# Change the following to point to where you have the MIPS cross-compiler
# installed.
GCCDIR = /cse306/mips-gcc/

CC = $(GCCDIR)gcc
AS = $(GCCDIR)as
LD = $(GCCDIR)ld
CPP = $(GCCDIR)cpp

COFF2NOFF = $(GCCDIR)coff2noff

LDFLAGS = -T script -N
ASFLAGS = -mips1
CPPFLAGS = -I$(INCDIR)

CFLAGS = -G 0 -I$(INCDIR) -B$(GCCDIR) -c -fno-builtin
SFLAGS = -G 0 -I$(INCDIR) -B$(GCCDIR) -c -S -fno-builtin

all:

#$(NACHOSDIR)/bin/coff2noff:
#	@echo "*** Missing $(NACHOSDIR)/bin/coff2noff, making that first"
#	cd ../bin; make coff2noff
#	@echo "*** Returning to make in test directory"

start.o: start.s $(INCDIR)/syscall.h
	$(CPP) $(CPPFLAGS) start.s > strt.s
	$(AS) $(ASFLAGS) -o start.o strt.s
	rm strt.s

halt.s: halt.c
	$(CC) $(SFLAGS) halt.c
halt.o: halt.c
	$(CC) $(CFLAGS) halt.c
halt: halt.o start.o
	$(LD) $(LDFLAGS) start.o halt.o -o halt.coff
	rm halt.o
	$(COFF2NOFF) halt.coff halt
	rm halt.coff

# If you are using gmake, the following rules should enable you to
# compile any of the test programs.  Otherwise, you have to copy the
# above code for "halt", making appropriate changes, to compile other
# test programs.

%.s: %.c
	$(CC) $(SFLAGS) $< 
%.o: %.c
	$(CC) $(CFLAGS) $<
%: %.o start.o
	$(LD) $(LDFLAGS) start.o $< -o $*.coff
	rm $*.o
	$(COFF2NOFF) $*.coff $@
	rm $*.coff
