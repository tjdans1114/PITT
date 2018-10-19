import java.util.Arrays;

public class HttpParser {
	public String method;
	public String uri;
	public String version;
	public String connection;
	public String ModifiedSince;
	public int[] Range = {0, 0};
	public int errCode = 0;

	final static String CRLF = "\r\n";
	
	public Event Parse(String request) {
		try {
			String[] request_string = request.split(CRLF);
			String firstline = request_string[0];
			String[] headerInfo = Arrays.copyOfRange(request_string, 1, request_string.length);
			
			String[] firstlines = firstline.split(" ");
			method = firstlines[0]; // HTTP method ('GET', 'POST' , ...)
			uri = firstlines[1]; // Request URI
			version = firstlines[2]; // HTTP version
			
			for (String header : headerInfo) {
				if (header.contains("Connection")) {
					connection = header.split(" ")[1];
				}
				else if (header.contains("If-Modified-Since")) {
					String[] headerList = header.split(" ");
					if (headerList.length > 2){
						ModifiedSince = headerList[1] + " " + headerList[2];
					}
				}
				else if (header.contains("Range")) {
					String[] rangeList = header.split(" ")[1].split("=")[1].split("-");
					if (rangeList.length > 1) {
						Range[0] = Integer.parseInt(rangeList[0]);
						Range[1] = Integer.parseInt(rangeList[1]);
					} else {
						Range[0] = Integer.parseInt(rangeList[0]);
					}
					//System.out.println(Range[0]);
					//System.out.println(Range[1]);
				}
			}			
		} catch (Exception e) { // Impossible to Parse
			errCode = 400;
			System.out.println(e.toString());
		}
		
		boolean blockingJob = (uri != "/");
		Event event = new Event(method, uri, blockingJob, connection, ModifiedSince, errCode, Range);
		return event;
	}
}
