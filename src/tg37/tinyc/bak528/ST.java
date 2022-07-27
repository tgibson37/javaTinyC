package tg37.tinyc;

public class ST extends PT {
	Expr exp;
	Stack stk;
	String prog;
	public ST(TJ tj){
		super(tj);
		this.exp = tj.exp;
		this.stk = tj.stk;
		this.prog = tj.prog;
	}
    public boolean quit() {
        System.out.println("Use ^C");
        return false;
    }
    public void at(int line){
    	System.err.println(", at ST~" + line);
//    	System.err.print(" cursor=" + cursor);
//    	System.err.println(" -->"+prog.substring(cursor,cursor+9)+"<--");
//    	System.err.println("prog  " + prog);
    }
    public void st() {
//System.err.println("ST~15 prog: "+tj.prog);
if(tj.prog==null)System.exit(99);
        int whstcurs, whcurs, objt, agin ;
        tj.brake=false;
        rem();
at(19);
//System.err.println("ST~20 prog: "+tj.prog);
		tj.stcurs = tj.cursor;
System.err.println("ST~21,cursor->"+tj.prog.substring(tj.cursor,tj.cursor+9)+"<--");
at(22);
        if(decl()) {
at(18);
            rem();
at(26);
            return;
        }
        else if( lit(xlb) ) {    /* compound statement */
at(30);
            for(;;) {
                rem();
                if(tj.leave||tj.brake||tj.error!=0)return;
                if(lit(xrb)) {
                    rem();
                    return;
                }
                st();
            }
        }
        else if(lit(xif)) {
at(42);
            if(exp.asgn()) {
                if(stk.toptoi()!=0) {
                    st();
                    rem();
                    if(lit(xelse)) {
                        skipSt();
                    }
                }
                else {
                    skipSt();
                    rem();
                    if(lit(xelse)) {
                        st();
                    }
                }
                rem();
                return;
            }
        }
        else if(lit(xwhile)) {
at(63);
            lit(xlpar);    /* optional left paren */
            if( !exp.asgn() )return;   /* error */
            lit(xrpar);
            boolean condition = stk.toptoi()!=0;
            if( condition ) {
                /* prepare for repeating/skipping while (stcurs)
                	or object */
                agin = tj.stcurs;
                objt = tj.cursor;
                st();

                if(tj.brake) {
                    tj.cursor = objt;	/* break: done with the while */
                    skipSt();		/* skip over the object */
                    tj.brake = false;
                    return;
                }
                else {
                    tj.cursor = agin;	/* no break: do the entire while again */
                    return;
                }
            }
            else {
                skipSt();
            }
        }
        else if(lit(xsemi)) {
at(91);
            rem();
        }
        else if(lit(xreturn)) {
at(95);
            char c = prog.charAt(tj.cursor);
            boolean eos = ( lit(xrpar)
                            || c == '['
                            || c == ']'
                            || c == ';'
                            || c == '\n'
                            || c == 0x0d
                            || c == '/'
                          );
            if ( eos ) {
                stk.pushzero(); /* default return value */
            }
            else {
                exp.asgn();  /* specified return value */
            }
            tj.leave=true;		/* signal st() to leave the function */
            return;
        }
        else if(lit(xbreak)) {
at(115);
            tj.brake=true;
            return;
        }
        else if( exp.asgn() ) {      /* if expression discard its value */
at(120);
            stk.toptoi();
            lit(xsemi);
        }
        else {
at(125);
            tj.eset(tj.STATERR);
        }
    }
    /*      skip a possibly compound statement. Shortcoming is brackets
     *      in comments, they must be balanced. */
    public void skipSt() {
        rem();
        if( lit(xlb) ) {               /* compound */
            skip('[',']');
            rem();
            return;
        }
        else if( lit(xif)||lit(xwhile) ) {
            lit(xlpar);                    /* optional left paren */
            skip('(',')');
            skipSt();
            rem();
            if(lit(xelse))skipSt();
            rem();
            return;
        }
        else {                                  /* simple statement, eol or semi     697 ends */
            while(++tj.cursor<tj.endapp) {
                char c = prog.charAt(tj.cursor);
                if( (c==0x0d)||(c=='\n')||(c==';') )break;
            }
            ++tj.cursor;
            rem();
        }
    }
    /* skips balance l-r assuming left is matched.
     *      Returns 0 on OK, else count of missing ]'s.
     *      On OK cursor points just past last matched r, else cursor == endappp.
     */
    public int skip(char l, char r) {
        int counter = 1;
        while( counter>0 && tj.cursor<tj.endapp ) {
            char c = prog.charAt(tj.cursor);
            if(c==l)++counter;
            if(c==r)--counter;
            ++tj.cursor;
        };
        if( counter>0 )return counter;
        ++tj.cursor;    // bump past matched arg r.
        return 0;
    }
    /* Match char or int, else do nothing. If match parse
     *  all comma separated declarations of that particular type
     *      making var table entries and allocating value storage. Returns false
     *      if not a declaration statement, true if it is. Leaves cursor just past
     *  optional semi.
     */
    public boolean decl() {
        TJ.Type t;
at(180);
        if( lit(xchar) ) {
at(182);
            do {
                varAlloc( TJ.Type.CHAR, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } else if( lit(xint) ) {
at(187);
            do {
                varAlloc( TJ.Type.INT, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } else {
at(192);
            return false;  /* not decl */
        }
at(195);
        lit(xsemi);    /* is decl */
at(197);
        return true;                                          
    }
    /*      SITUATION: int or char is parsed.
     *      Parses one variable. Makes allocation and symbol entry.
     */
    public void varAlloc(TJ.Type type, Stuff vpassed) {
        boolean isArray=false;
        int alen=1;
        if( !exp.symName() ) {             /*defines fname,lname. True is match.*/
            tj.eset(tj.SYMERR);
            return;
        }
        tj.cursor=lname+1;
        if( lit("(") ) {
            isArray = true;   /* distance to data (was vclass) */
            int fn=fname; /* localize globals that asgn() may change */
            int ln=lname;
            if( exp.asgn() ) alen=stk.toptoi()+1;  /* dimension */
            fname=fn;               /* restore the globals */
            lname=ln;
            int x = mustFind(tj.cursor,tj.cursor+5,')',tj.RPARERR);
            if(x>0)tj.cursor = x+1;
        } else {
            isArray = false;
            alen = 1;
        }
        new Var(isArray, type, alen, vpassed);
    }
}