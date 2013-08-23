/**
 * 
 */
package app;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ben Griffiths
 *
 */
public class RecognisedResource {
	@Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String uri;
    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PRIVATE) private String resourceLabel;
    @Getter(AccessLevel.PUBLIC) @Setter(AccessLevel.PUBLIC) private String typeLabel;

    public RecognisedResource(String uri, String resourceLabel, String typeLabel) {
           this.setUri(uri);
           this.setResourceLabel(resourceLabel);
           this.setTypeLabel(typeLabel);
    }
}
