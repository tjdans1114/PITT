import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HTTPResponse {
	public static String version = "HTTP/1.1";
	
	public static ByteBuffer[] respond(int code, Event event) {
		if (code == 200) {
			return getResponse(String.valueOf(code), "OK", event);
		} else if (code == 304) {
			return getResponse(String.valueOf(code), "NOT MODIFIED", event);
		} else if (code == 400) {
			return getResponse(String.valueOf(code), "BAD REQUEST", event);
		} else if (code == 404) {
			return getResponse(String.valueOf(code), "NOT FOUND", event);
		} else if (code == 405) {
			return getResponse(String.valueOf(code), "METHOD NOT ALLOWED", event);
		} else if (code == 206) {
			return getResponse(String.valueOf(code), "Partial Content", event);
		} else {
			return getResponse(String.valueOf(code), "SERVICE UNAVAILABLE", event);
		}
	}
	
	public static ByteBuffer[] getResponse(String code, String status, Event event) {
		ByteBuffer content = event.response_data[1];
		String firstLine = version + " " + code + " " + status + "\r\n";
		
		String header = "Content-Length: " + (event.Range[1] - event.Range[0]) + "\n" + "Content-Type: " +
				event.content_type + "\n" + "Accept-Ranges: bytes" + "\n" + "Content-Range: bytes " 
				+ String.valueOf(event.Range[0]) + "-" + String.valueOf(event.Range[1] - 1) +
				"/" + String.valueOf(event.size) + "\n" + "Last-Modified: " + event.LastModified;
		ByteBuffer headerBuf = Charset.forName("UTF-8").encode(firstLine + header + "\r\n" + "\r\n");
		content.rewind();
		//int size = content.limit() + headerBuf.limit();
		//ByteBuffer result = ByteBuffer.allocate(size).put(headerBuf).put(content);
		return new ByteBuffer[] {headerBuf, content};
	}
}
