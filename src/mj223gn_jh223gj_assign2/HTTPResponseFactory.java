package mj223gn_jh223gj_assign2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mj223gn_jh223gj_assign2.Header.HTTPHeader;
import mj223gn_jh223gj_assign2.exceptions.AccessRestrictedException;
import mj223gn_jh223gj_assign2.exceptions.ResourceNotFoundException;

public class HTTPResponseFactory {

	Map<Header.HTTPHeader, Header> headers;
	private String DefaultErrorPath = TCPServer.BASEPATH + "/errors/";

	public HTTPResponseFactory() {

	}

	public HTTPResponse getHTTPResponse(HTTPRequest request) throws ResourceNotFoundException, AccessRestrictedException, FileNotFoundException {
		HTTPResponse response = null;
		
		/* HTTP GET Method -> retrieve a resource */
		if (request.getType().equals(HTTPRequest.Method.GET)) {

				/* GET Requested File */
				File file = getResource(request.getUrl());
				
				/* Add file specific headers Length Header */
				headers = new HashMap<Header.HTTPHeader, Header>();

				/* Add Standard Server Headers */
				addServerHeaders(file);
				
				/* Create Response */
				response = new HTTPResponse(HTTPResponse.HTTPStatus.OK, headers);
				response.setResponseBody(file);

		}

		return response;
	}

	/**
	 * Returns the file of the given path. The Path is relative to the resources folder in the working directory.
	 *
	 * @param path to the file
	 * @return File which is found in the path or index.html/index.htm if the path is pointing to a directory containing this file.
	 * @throws ResourceNotFoundException thrown if the file or directory does not exist.
	 */
	private File getResource(String path) throws ResourceNotFoundException, AccessRestrictedException, FileNotFoundException {
		File file = null;
		file = new File(TCPServer.BASEPATH + path);

		if (!file.exists())
			throw new ResourceNotFoundException("No Resource <" + path + "> could be found.");

		if (path.contains("../") || path.contains("/secret")) {
			throw new AccessRestrictedException("Access restricted");
		}
		/* Path refers to a directory => search for index file */
		if (file.isDirectory()) {
			for(File f: file.listFiles()){
				if(f.getName().contains("index.")){
					file = f;
				}
			}
			/* If its still is a directory (has not found a index) we create an index page for that folder. */
			if(!file.isFile()){
				//Create a new file and a printwriter to write to it.
				File index = new File(TCPServer.BASEPATH + path +"/index.html");
				PrintWriter pw = new PrintWriter(index);

				//Simple HTML code for showing the files in the folder.
				pw.write("<!DOCTYPE html> \n <html> \n <body>");

				for(File f : file.listFiles()){
					if(!f.getName().contains("index.")) {
						//if it is a directory we write it in bold.
						if(f.isDirectory()) {
							pw.write("<p><strong>" + f.getName() + "</strong></p>\n");
						}
						//else standard file.
						else {
							pw.write("<p>" + f.getName() + "</p>\n");
						}
					}
				}
				pw.write("</body> \n </html> \n");
				pw.close();

				//sets this index page as the response file.
				file = index;
			}

		}
		return file;
	}

	/* TASK: NEED TO ADD MORE STANDARD Headers! */
	private void addServerHeaders(File file) {
		headers.put(HTTPHeader.Date, new Header(HTTPHeader.Date, new Date().toString()));
		headers.put(Header.HTTPHeader.ContentLength, new Header(Header.HTTPHeader.ContentLength, Long.toString(file.length())));
		headers.put(Header.HTTPHeader.ContentType, new Header(Header.HTTPHeader.ContentType, Header.MIMEType.fromFileName(file.getName()).getTextFormat()));

	}

	/**
	 * Method thats creates error response from status codes.
	 * Default response is 500 internalServerError.
	 * @param status code of what error occurred
	 * @return HTTPResponse with error message
	 */
	public HTTPResponse getErrorResponse(HTTPResponse.HTTPStatus status){
		File file = new File(DefaultErrorPath + "500.html");
		HTTPResponse response;
		headers = new HashMap<Header.HTTPHeader, Header>();

		switch (status){
			case NotFound:
				file = new File(DefaultErrorPath + "404.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.NotFound, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case Forbidden:
				file = new File(DefaultErrorPath + "403.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.Forbidden, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case UnsupportedMediaType:
				file = new File(DefaultErrorPath + "415.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.UnsupportedMediaType, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case BadRequest:
				file = new File(DefaultErrorPath + "400.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.BadRequest, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case NotImplemented:
				file = new File(DefaultErrorPath + "501.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.NotImplemented, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case HTTPVersionNotSupported:
				file = new File(DefaultErrorPath + "505.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.NotImplemented, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			case NotAllowed:
				file = new File(DefaultErrorPath + "405.html");
				response = new HTTPResponse(HTTPResponse.HTTPStatus.NotImplemented, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
			default:
				response = new HTTPResponse(HTTPResponse.HTTPStatus.InternalServerError, headers);
				addServerHeaders(file);
				response.setResponseBody(file);
				return response;
		}
	}
	
	

}
