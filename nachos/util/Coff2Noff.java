package nachos.util;

import java.io.*;

public class Coff2Noff {

    /**
     * Coff2Noff cofffile nofffile
     */
    public static void main(String[] args) throws Exception {
	if(args.length != 2) {
	    System.err.println
		("Usage: Coff2Noff <coffFileName> <noffFileName>");
	    System.exit(1);
	}
	CoffInput coff = new CoffInput(args[0]);
	coff.read();

	File noffFile = new File(args[1]);
	noffFile.delete();
	noffFile.createNewFile();
	NoffOutput noff = new NoffOutput(noffFile);

	CoffInput.Section[] sections = coff.getSections();
	boolean haveBSS = false;
	for(int i = 0; i < sections.length; i++) {
	    CoffInput.Section s = sections[i];
	    if(s.size == 0) {
		// do nothing!
	    } else if(s.name.equals(".text")) {
		noff.setCode(s.pAddr, s.data);
	    } else if(s.name.equals(".rdata")) {
		// The linker outputs .rdata right after .text.
		// Since we assume .text is nonempty, so there is
		// nothing to do here.
	    } else if(s.name.equals(".data")) {
		noff.setInitData(s.pAddr, s.data);
	    } else if(s.name.equals(".bss") || s.name.equals(".sbss")) {
		if(haveBSS) {
		    System.err.println("Can't handle both bss and sbss");
		    noffFile.delete();
		    System.exit(1);
		} else {
		    haveBSS = true;
		    noff.setUninitData(s.pAddr, s.size);
		}
	    } else {
		System.err.println("Unknown segment type: " + s.name);
		noffFile.delete();
		System.exit(1);
	    }
	}
	noff.write();
	System.exit(0);
    }

}
