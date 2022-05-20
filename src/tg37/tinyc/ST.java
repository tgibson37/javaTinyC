package tg37.tinyc;

public class ST extends PT {
    public boolean quit() {
        System.out.println("Use ^C");
        return false;
    }

    public void st() {
        int whstcurs, whcurs, objt, agin ;
        brake=false;
        rem();
        stcurs = cursor;
        if(decl()) {
            rem();
            return;
        }
        else if( lit(xlb) ) {    /* compound statement */
            for(;;) {
                rem();
                if(leave||brake||error!=0)return;
                if(lit(xrb)) {
                    rem();
                    return;
                }
//System.err.println("ST~32, xlb loop calling st, cursor->"
//	+pr.substring(cursor,cursor+9));
                st();
            }
        }
        else if(lit(xif)) {
System.err.println("    ST~40, xif");
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
System.err.println("    ST~61, xwhile");
            lit(xlpar);    /* optional left paren */
            if( !exp.asgn() )return;   /* error */
            lit(xrpar);
            boolean condition = stk.toptoi()!=0;
            if( condition ) {
                /* prepare for repeating/skipping while (stcurs)
                	or object */
                agin = stcurs;
                objt = cursor;
                st();

                if(brake) {
                    cursor = objt;	/* break: done with the while */
                    skipSt();		/* skip over the object */
                    brake = false;
                    return;
                }
                else {
                    cursor = agin;	/* no break: do the entire while again */
                    return;
                }
            }
            else {
                skipSt();
            }
        }
        else if(lit(xsemi)) {
System.err.println("    ST~89, xsemi");
            rem();
        }
        else if(lit(xreturn)) {
System.err.println("    ST~93, xreturn");
            char c = pr.charAt(cursor);
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
            leave=true;		/* signal st() to leave the function */
            return;
        }
        else if(lit(xbreak)) {
System.err.println("    ST~113, xbreak");
            brake=true;
            return;
        }
        else if( exp.asgn() ) {      /* if expression discard its value */
System.err.println("    ST~118, cursor -->"+pr.substring(cursor,cursor+19));
            stk.toptoi();
            lit(xsemi);
        }
        else {
System.err.println("    ST~123, eset");
            eset(STATERR);
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
            while(++cursor<endapp) {
                char c = pr.charAt(cursor);
                if( (c==0x0d)||(c=='\n')||(c==';') )break;
            }
            ++cursor;
            rem();
        }
    }
    /* skips balance l-r assuming left is matched.
     *      Returns 0 on OK, else count of missing ]'s.
     *      On OK cursor points just past last matched r, else cursor == endappp.
     */
    public int skip(char l, char r) {
        int counter = 1;
        while( counter>0 && cursor<endapp ) {
            char c = pr.charAt(cursor);
            if(c==l)++counter;
            if(c==r)--counter;
            ++cursor;
        };
        if( counter>0 )return counter;
        ++cursor;    // bump past matched arg r.
        return 0;
    }
    /* Match char or int, else do nothing. If match parse
     *  all comma separated declarations of that particular type
     *      making var table entries and allocating value storage. Returns false
     *      if not a declaration statement, true if it is. Leaves cursor just past
     *  optional semi.
     */
    public boolean decl() {
        Stuff.Type t;
        if( lit(xchar) ) {
            do {
                varAlloc( Stuff.Type.CHAR, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } else if( lit(xint) ) {
            do {
                varAlloc( Stuff.Type.INT, null );  /* 2nd arg is vpassed */
            } while( lit(xcomma) );
        } else {
            return false;  /* not decl */
        }
        lit(xsemi);    /* is decl */
        return true;                                          
    }
    /*      SITUATION: int or char is parsed.
     *      Parses one variable. Makes allocation and symbol entry.
     */
    public void varAlloc(Stuff.Type type, Stuff vpassed) {
        boolean isArray=false;
        int alen=1;
        if( !exp.symName() ) {             /*defines fname,lname. True is match.*/
            eset(SYMERR);
            return;
        }
        cursor=lname+1;
        if( lit("(") ) {
            isArray = true;   /* distance to data (was vclass) */
            int fn=fname; /* localize globals that asgn() may change */
            int ln=lname;
            if( exp.asgn() ) alen=stk.toptoi()+1;  /* dimension */
            fname=fn;               /* restore the globals */
            lname=ln;
            int x = mustFind(cursor,cursor+5,')',RPARERR);
            if(x>0)cursor = x+1;
        } else {
            isArray = false;
            alen = 1;
        }
        new Var(isArray, type, alen, vpassed);
    }
}