#include "syscall.h"
int main()
{
   static char buf[] = {'a', 'b', 'c'};

   Write(&buf[0],1,ConsoleOutput);
}
