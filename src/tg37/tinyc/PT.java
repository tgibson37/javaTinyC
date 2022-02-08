/*	Parse tools: boolean symNameIs, int find, int mustFind, findEOS, rem.
 *	returned ints are indexes into pr. NEEDS TJ.pr and other TJ stuff.
 */

package tg37.tinyc;

public class PT {

/*	set error unless already set, capture cursor in errat */
void eset( int err ){
	if(TJ.error != 0){
		TJ.error = err; TJ.errat = TJ.cursor;
	}
}
//true if s1==s2 for length n
boolean match(String s1, String s2, int n) {
	for(int i=0; i<n; ++i) {
		if(s1.charAt(i) != s2.charAt(i))return false;
	}
	return true;
}
// true if cursor==token
boolean matchcur(String token){
	return matchN(token, TJ.cursor, token.length());
}
// true if f/lname==token
boolean matchnm(String token){
	return matchN(token,TJ.fname,TJ.lname-TJ.fname+1);
}

/*	return true if symname matches arg, no state change 
 */
	boolean symNameIs(String name){
		int x = strncmp(TJ.pr[TJ.fname], name, TJ.lname-TJ.fname+1);
		return( x!=0 );
	}

	/*	State is not changed by find or mustFind. Returned value is
	sole purpose of find. That plus setting err for mustFind. 
 */
	int find( int from, int upto, char c) {
		int x = from;
		while( TJ.pr[x] != c && x<upto) {
			++x;
		}
		return x<upto ? x : 0;
	}

/*	same as find but sets err on no match 
 */
	int mustFind( int from, int upto, char c, int err ) {
		int x = find(TJ.pr[from], TJ.pr[upto], c);
		if( x ) return x;
		else { eset(err); return 0; }
	}
	
/*	special find for end of string. Minds the old null ending.
 */
	int findEOS( char x ) {
		while( x<TJ.endapp) {
			if( TJ.pr[x]==0 || TJ.pr[x]==0x22 ) return x;
			++x;
		}
		eset(TJ.CURSERR);
		return 0;
	}
	
/*	skip over comments and/or empty lines in any order, new version
	tolerates 0x0d's, and implements // as well as old slash-star comments.
 */
	void rem() {
		for(;;) {
			while(    TJ.pr[TJ.cursor]==0x0a
					||TJ.pr[TJ.cursor]==0x0d
					||TJ.pr[TJ.cursor]==' '
					||TJ.pr[TJ.cursor]=='\t'
				  )++TJ.cursor;
			if( !(lit(TJ.xcmnt)||lit(TJ.xcmnt2)) ) return;
			while( TJ.pr[TJ.cursor] != 0x0a && TJ.pr[TJ.cursor] != 0x0d && TJ.cursor<TJ.endapp )
				++TJ.cursor;
		}
	}
}