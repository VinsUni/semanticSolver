/**
 * 
 */
package prototype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class ModelLoader {
	@Getter(AccessLevel.PRIVATE) @Setter(AccessLevel.PRIVATE) String test;
	
	public ModelLoader(String s) {
		this.setTest(s);
	}
	
	public void go() {
		System.out.println(this.getTest());
	}
}