package it.intext.pattern.mapper;

public class TagMapper {
	
	public enum Mode { Replace, Inject, Annotate };
	
	protected String layer;
	protected String tag;
	protected TextMapper mapper;
	protected Mode mode;
	
	public TagMapper(String layer, String tag, TextMapper mapper) {
		this(layer, tag, mapper, Mode.Replace);
	}

	public TagMapper(String layer, String tag, TextMapper mapper, Mode mode) {
		this.layer = layer;
		this.tag = tag;
		this.mapper = mapper;
		this.mode = mode;
	}
	
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public TextMapper getMapper() {
		return mapper;
	}
	public void setMapper(TextMapper mapper) {
		this.mapper = mapper;
	}
	
	public String map(String text) {
		if (this.mapper == null) return text;
		else return this.mapper.map(text);
	}
	
	public Mode getMode() {
		return this.mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	
	public String getMappingTag() {
		return this.mapper.getMappingTag();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TagMapper [layer=");
		builder.append(layer);
		builder.append(", tag=");
		builder.append(tag);
		builder.append(", mapper=");
		builder.append(mapper);
		builder.append(", mode=");
		builder.append(mode);
		builder.append("]");
		return builder.toString();
	} 
	
	
}
