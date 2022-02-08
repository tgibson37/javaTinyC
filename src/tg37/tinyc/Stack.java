/*	Stack declares the stack, and offers basic push/pop methods.
 *	Requires TJ.class, specifically STACKSIZE.
 */
package tg37.tinyc;

public class Stack{
	int nextstack=0;
	Stuff stack[] = new Stuff[TJ.STACKSIZE];

	public Stack(int size) {
		/* the stack */
		stack = new Stuff[size];
	}
	/* basic pusher */
	void pushst(Stuff stuff) {
		if(nextstack >= TJ.STACKSIZE) TJ.error = TJ.PUSHERR;
		else stack[nextstack++] = stuff;
	}
	/* basic popper, entry stays accessible until pushed over */
	Stuff popst() {
		if( --nextstack < 0 ) { TJ.error = TJ.POPERR; return null; }
		return stack[nextstack];
	}

// tests...
	public static void main(String[] args){
		System.out.println("running Stack.main");
	}
}