import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Event {
	public String method;
	public String uri;
	public boolean blocking;
	public String connection;
	public String content_type;
	public ByteBuffer[] response_data;
	public SocketChannel socket;
	public SelectionKey key;
	public String LastModified;
	public String ModifedSince;
	public int errCode;
	public int Range[];
	public int size;
	
	public Event(String method, String uri, boolean blocking, String connection, String ModifiedSince, int errCode, int Range[]) {
		this.method = method;
		this.uri = uri;
		this.blocking = blocking;
		this.connection = connection;
		this.response_data = new ByteBuffer[] {ByteBuffer.allocate(0), ByteBuffer.allocate(0)};
		this.socket = null;
		this.content_type = uri.endsWith(".html") ? "text/html" : "*/*";
		this.ModifedSince = ModifiedSince;
		this.errCode = errCode;
		this.Range = Range;
		//this.range = 0;
	}
	
	public Event() {
		this.method = "GET";
		this.uri = "/";
		this.blocking = false;
		this.connection = "keep-alive";
		this.socket = null;
		this.response_data = new ByteBuffer[] {ByteBuffer.allocate(0), ByteBuffer.allocate(0)};
		this.content_type = "*/*";
		this.errCode = 0;
		//this.range = 0;
	}
	
	public boolean isIO() {
		return this.blocking;
	}
}
