/**
 * 
 */
package it.intext.pattern.mapper;

/**
 * @author Vieri
 *
 */
public class XmlTagMapper implements TextMapper {

	protected String tag;
	
	public XmlTagMapper(String tag) {
		this.tag = tag;
	} 
	
	public String map(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<");
		sb.append(tag);
		sb.append(">");
		sb.append(text);
		sb.append("</");
		sb.append(tag);
		sb.append(">");
		return sb.toString();
	}

	public String getMappingTag() {
		return this.tag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("XmlTagMapper [tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}

	
}
