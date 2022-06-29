package tg37.tinyc;

public class ST extends PT {
	static Expr exp;
	static Stack stk;
	static String prog;

    private static ST instance;
    private ST(){super();}
    public static synchronized ST getInstance(){
        if(instance == null){
            instance = new ST();
			exp = Expr.getInstance();
			stk = Stack.getInstance();
			tj = TJ.getInstance();
			prog = tj.prog;
        }
        return instance;
    }

	public boolean quit() {
        System.out.println("Use ^C");
        return false;
    }
    public void at(int line){
    	System.err.println(", at ST~" + line);
    }
    public void st() {
//System.err.println("ST~29 cursor: "+tj.cursor);
        int whstcurs, whcurs, objt, agin ;
        tj.brake=false;
        rem();
		tj.stcurs = tj.cursor;
        if(decl()) {
            rem();
            return;
        }
        else if( lit(xlb) ) {    /* compound statement */
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
            rem();
        }
        else if(lit(xreturn)) {
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
            tj.brake=true;
            return;
        }
        else if( exp.asgn() ) {      /* if expression discard its value */
            stk.toptoi();
            lit(xsemi);
        }
        else {
System.err.println("ST~127, progAt: "+tj.prog.charAt(tj.cursor));
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
            char c = tj.prog.charAt(tj.cursor);
            if(c==l)++counter;
            if(c==r)--counter;
//System.err.println("ST~166: c,counter "+c+","+counter);
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
        if( lit(xchar) ) {
            do {
                varAlloc( TJ.Type.CHAR, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } 
        else if( lit(xint) ) {
            do {
                varAlloc( TJ.Type.INT, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } 
        else {
            return false;  /* not decl */
        }
        lit(xsemi);    /* is decl */
        return true;                                          
    }
    /*      SITUATION: int or char is parsed.
     *      Parses one variable. Makes allocation and symbol entry.
     */
    public void varAlloc(TJ.Type type, Stuff vpassed) {
//System.err.println("ST~203, varAlloc, ");
        boolean isArray=false;
        int alen=1;
        if( !exp.symName() ) {         /*defines fname,lname. True is match.*/
            tj.eset(tj.SYMERR);
            return;
        }
//System.err.println("ST~209, varAlloc, symName parsed");
        tj.cursor=tj.lname;
        if( lit("(") ) {
//System.err.println("ST~213, varAlloc, ( parsed");
            isArray = true;   /* distance to data (was vclass) */
            int fn=tj.fname; /* localize globals that asgn() may change */
            int ln=tj.lname;
            if( exp.asgn() ) alen=stk.toptoi()+1;  /* dimension */
//System.err.println("ST~218, varAlloc, 0 parsed");
            tj.fname=fn;               /* restore the globals */
            tj.lname=ln;
            int x = mustFind(tj.cursor,tj.cursor+5,')',tj.RPARERR);
            if(x>0)tj.cursor = x+1;
        } else {
//System.err.println("ST~224, varAlloc, NOT (  --->> "
//	+tj.prog.substring(tj.cursor-19,tj.cursor+19));
            isArray = false;
            alen = 1;
        }
//System.err.println("ST~2, varAlloc, about to call new Var(..)");
        new Var(isArray, type, alen, vpassed);
    }
}