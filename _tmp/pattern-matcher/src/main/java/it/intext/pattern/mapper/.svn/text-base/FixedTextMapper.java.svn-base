package it.intext.pattern.mapper;

public class FixedTextMapper implements TextMapper {
	
	protected String value;
	
	public FixedTextMapper(String value) {
		this.value = value;
	}

	public String map(String text) {
		return this.value;
	}

	public String getMappingTag() {
		return this.value;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FixedTextMapper [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
