/* SITUATION: Parsed an assignment expression. Two stack entries, lvalue, datam.
 *	Effects the assignment. 
 */
void _eq() {
	int  iDatum;  /* memcpy into these from pr using val.stuff */
	char cDatum;  /*  and val.size, giving needed cast */
	void* pDatum;
	void* where;

	struct stackentry *val = &stack[nxtstack-1]; /* value (on top) */
	struct stackentry *lval = &stack[nxtstack-2]; /* where to put it */
	if(verbose[VE]){
		fprintf(stderr,"\neq: lval");
		dumpStackEntry(nxtstack-2);
		fprintf(stderr,"\neq: val");
		dumpStackEntry(nxtstack-1);
	}
	popst();popst();  
	where = &((*lval).value.up);
	int class = (*lval).class;
	int type = (*lval).type;
//	int whereSize = typeToSize(class,type);  /* of the lvalue */

	if((*lval).lvalue != 'L') { 
		eset(LVALERR); 
		return; 
	}
	
	else if(class==0 && (*val).class==0) {
		if(type==Int){
			if( (*val).lvalue=='L' ) {
				iDatum = get_int((*val).value.up);
			}
			else {
				iDatum = (*val).value.ui;
			}
			if((*val).type==Char) iDatum = iDatum&0xff;
			put_int( (*lval).value.up, iDatum);
			pushk(iDatum);
		}
	}
}
