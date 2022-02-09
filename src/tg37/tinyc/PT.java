/*	Parse tools: boolean symNameIs, int find, int mustFind, findEOS, rem.
 *	returned ints are indexes into pr.
 */

package tg37.tinyc;

public class PT extends TJ {

/*	set error unless already set, capture cursor in errat */
void eset( int err ){
	if(error != 0){
		error = err; errat = cursor;
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
	int len = tokenLength();
	return token.equals(pr.subString(cursor,cursor+len-1));
}
// true if f/lname==token
boolean matchnm(String token){
	return match(token,pr+fname,lname-fname+1);
}

/*	return true if symname matches arg, no state change 
 */
	boolean symNameIs(String name){
		int x = strncmp(pr[fname], name, lname-fname+1);
		return( x!=0 );
	}

	/*	State is not changed by find or mustFind. Returned value is
	sole purpose of find. That plus setting err for mustFind. 
 */
	int find( int from, int upto, char c) {
		int x = from;
		while( pr[x] != c && x<upto) {
			++x;
		}
		return x<upto ? x : 0;
	}

/*	same as find but sets err on no match 
 */
	int mustFind( int from, int upto, char c, int err ) {
		int x = find(pr[from], pr[upto], c);
		if( x ) return x;
		else { eset(err); return 0; }
	}
	
/*	special find for end of string. Minds the old null ending.
 */
	int findEOS( char x ) {
		while( x<endapp) {
			if( pr[x]==0 || pr[x]==0x22 ) return x;
			++x;
		}
		eset(CURSERR);
		return 0;
	}
	
/*	skip over comments and/or empty lines in any order, new version
	tolerates 0x0d's, and implements // as well as old slash-star comments.
 */
	void rem() {
		for(;;) {
			while(    pr[cursor]==0x0a
					||pr[cursor]==0x0d
					||pr[cursor]==' '
					||pr[cursor]=='\t'
				  )++cursor;
			if( !(lit(xcmnt)||lit(xcmnt2)) ) return;
			while( pr[cursor] != 0x0a && pr[cursor] != 0x0d && cursor<endapp )
				++cursor;
		}
	}
}