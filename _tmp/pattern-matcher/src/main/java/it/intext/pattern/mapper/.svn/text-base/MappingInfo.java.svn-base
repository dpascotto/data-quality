package it.intext.pattern.mapper;

public class MappingInfo implements Comparable<MappingInfo> {

	protected int start;
	protected int end;
	protected TagMapper mapper;
	protected String match;
	
	public MappingInfo() {}
	
	public MappingInfo(int start, int end, String match, TagMapper mapper) {
		this.start = start;
		this.end = end;
		this.match = match;
		this.mapper = mapper;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}

	public TagMapper getMapper() {
		return mapper;
	}

	public void setMapper(TagMapper mapper) {
		this.mapper = mapper;
	}

	public String getValue() {
		return this.mapper.map(this.match);
	}
	
	public String getTag() {
		return (this.mapper == null) ? null : this.mapper.getTag();
	}

	public String getlayer() {
		return (this.mapper == null) ? null : this.mapper.getLayer();
	}
	
	public int compareTo(MappingInfo o) {
		
		if (this.getStart() < o.getStart()) return -1;
		else if (this.getStart() > o.getStart()) return 1;
		else if (this.getEnd() > o.getEnd()) return -1;
		else if (this.getEnd() < o.getEnd()) return 1;
		else return 0;
	}

	public boolean isContained(MappingInfo container) {
		return ((this.getStart() >= container.getStart())
				&& (this.getEnd() < container.getEnd()))
				||
				((this.getStart() > container.getStart())
						&& (this.getEnd() <= container.getEnd()));
	}
	
	public boolean contains(MappingInfo target) {
		return target.isContained(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingInfo [start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", mapper=");
		builder.append(mapper);
		builder.append(", match=");
		builder.append(match);
		builder.append("]");
		return builder.toString();
	}
	
	
}
